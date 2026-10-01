import { db, LocalSyncQueueItem } from './db';
import { networkService } from './networkService';
import { syncApiService } from '../services/syncApiService';
import { offlineProgressRepository } from './offlineProgressRepository';
import { offlineStudentRepository } from './offlineStudentRepository';

type SyncEventListener = () => void;
const MAX_RETRY_DELAY_MS = 5 * 60 * 1000;
const BATCH_SIZE = 50;

class SyncService {
  private isProcessing = false;
  private listeners: Set<SyncEventListener> = new Set();
  private retryTimer: ReturnType<typeof setTimeout> | undefined;

  constructor() {
    networkService.subscribe((isOnline) => {
      if (isOnline) void this.processQueue();
    });
    void this.recoverProcessingItems();
  }

  private async recoverProcessingItems(): Promise<void> {
    await db.syncQueue.where('status').equals('PROCESSING').modify({
      status: 'FAILED',
      nextAttemptAt: new Date().toISOString(),
      lastError: 'Recovered after an interrupted synchronization.'
    });
    if (networkService.isOnline()) void this.processQueue();
  }

  public subscribe(listener: SyncEventListener): () => void {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  private notifyListeners(): void {
    this.listeners.forEach((listener) => listener());
  }

  public notifyQueueChanged(): void {
    this.notifyListeners();
  }

  public async enqueueAction(actionType: string, payload: any): Promise<string> {
    const queueId = `queue-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`;
    const item: LocalSyncQueueItem = {
      id: queueId,
      actionType,
      payload: JSON.stringify(payload),
      createdAt: new Date().toISOString(),
      status: 'PENDING',
      retryCount: 0,
      nextAttemptAt: new Date().toISOString()
    };
    await db.syncQueue.add(item);
    this.notifyListeners();
    if (networkService.isOnline()) void this.processQueue();
    return queueId;
  }

  public async getPendingCount(): Promise<number> {
    return db.syncQueue.where('status').equals('PENDING').or('status').equals('FAILED').count();
  }

  public async getLastSyncTime(): Promise<string | null> {
    const setting = await db.settings.get('last_sync_time');
    return setting?.value ?? null;
  }

  private parsePayload(item: LocalSyncQueueItem): Record<string, any> {
    try { return JSON.parse(item.payload) as Record<string, any>; } catch { return {}; }
  }

  private backoff(retryCount: number): number {
    return Math.min(1000 * (2 ** Math.max(0, retryCount - 1)), MAX_RETRY_DELAY_MS);
  }

  private async scheduleRetry(): Promise<void> {
    if (this.retryTimer) clearTimeout(this.retryTimer);
    if (networkService.isOffline()) return;
    const failed = await db.syncQueue.where('status').equals('FAILED').toArray();
    const pending = await db.syncQueue.where('status').equals('PENDING').toArray();
    const futureDates = [...failed, ...pending]
      .map((item) => item.nextAttemptAt ? Date.parse(item.nextAttemptAt) : Date.now())
      .filter((time) => Number.isFinite(time));
    if (!futureDates.length) return;
    const delay = Math.max(0, Math.min(...futureDates) - Date.now());
    this.retryTimer = setTimeout(() => { void this.processQueue(); }, delay);
  }

  private async markFailed(items: LocalSyncQueueItem[], error: string): Promise<void> {
    for (const item of items) {
      const retryCount = (item.retryCount || 0) + 1;
      await db.syncQueue.update(item.id, {
        status: 'FAILED',
        retryCount,
        nextAttemptAt: new Date(Date.now() + this.backoff(retryCount)).toISOString(),
        lastError: error.slice(0, 500)
      });
    }
  }

  public async processQueue(): Promise<void> {
    if (this.isProcessing || networkService.isOffline()) return;
    this.isProcessing = true;
    try {
      const now = Date.now();
      const candidates = await db.syncQueue
        .where('status').equals('PENDING').or('status').equals('FAILED').toArray();
      const due = candidates.filter((item) => !item.nextAttemptAt || Date.parse(item.nextAttemptAt) <= now);
      if (!due.length) return;

      // One authenticated sync request may contain only the student's own queue records.
      const studentId = Number(this.parsePayload(due[0]).studentId);
      const batch = due.filter((item) => Number(this.parsePayload(item).studentId) === studentId).slice(0, BATCH_SIZE);
      if (!Number.isFinite(studentId) || studentId <= 0) {
        await this.markFailed(batch, 'Queue item has no valid student owner.');
        return;
      }

      for (const item of batch) await db.syncQueue.update(item.id, { status: 'PROCESSING' });
      this.notifyListeners();

      const items = batch.map((item) => {
        const payload = this.parsePayload(item);
        return {
          id: item.id,
          actionType: item.actionType,
          studentId,
          activityId: Number(payload.activityId),
          score: payload.score == null ? undefined : Number(payload.score),
          completedAt: payload.completedAt,
          payload: item.payload
        };
      });

      const response = await syncApiService.sendSyncRequest({ studentId, items });
      const succeeded = new Set(response?.syncedItems || []);
      const failedError = (response?.errors || []).join('; ') || 'The server did not confirm this queue item.';
      const failedBatch: LocalSyncQueueItem[] = [];

      for (const item of batch) {
        if (succeeded.has(item.id)) {
          const payload = this.parsePayload(item);
          await db.syncQueue.delete(item.id);
          if (item.actionType !== 'EARN_XP' && payload.studentId && payload.activityId) {
            await offlineProgressRepository.markProgressSynced(`${payload.studentId}-${payload.activityId}`);
          }
          if (item.actionType === 'EARN_XP' && payload.xpTransactionId) {
            const xpTransaction = await db.xpTransactions.get(payload.xpTransactionId);
            if (xpTransaction && !xpTransaction.synced) {
              await db.transaction('rw', db.xpTransactions, db.students, async () => {
                await db.xpTransactions.update(payload.xpTransactionId, { synced: true });
                await offlineStudentRepository.addXpToCachedProfile(xpTransaction.studentId, xpTransaction.xp);
              });
            }
          }
        } else {
          failedBatch.push({ ...item, status: 'PROCESSING', lastError: failedError });
        }
      }

      if (failedBatch.length) await this.markFailed(failedBatch, failedError);
      if (succeeded.size > 0) await db.settings.put({ key: 'last_sync_time', value: new Date().toISOString() });
    } catch (error) {
      console.error('Error during auto-sync:', error);
      const processing = await db.syncQueue.where('status').equals('PROCESSING').toArray();
      await this.markFailed(processing, error instanceof Error ? error.message : 'Synchronization failed.');
    } finally {
      this.isProcessing = false;
      this.notifyListeners();
      await this.scheduleRetry();
    }
  }
}

export const syncService = new SyncService();
