import { db, LocalProgress, LocalSyncQueueItem, LocalXpTransaction } from './db';
import { syncService } from './syncService';

export interface ActivityProgressInput {
  studentId: number;
  activityId: number;
  score: number;
  completed: boolean;
  xpReward: number;
  actionType: 'COMPLETE_ACTIVITY' | 'UPDATE_PROGRESS';
}

export interface ActivityProgressResult {
  progress: LocalProgress;
  xpEarned: number;
  version: number;
}

const queueRecord = (id: string, actionType: string, payload: unknown): LocalSyncQueueItem => ({
  id,
  actionType,
  payload: JSON.stringify(payload),
  createdAt: new Date().toISOString(),
  status: 'PENDING',
  retryCount: 0,
  nextAttemptAt: new Date().toISOString()
});

export const offlineLearningService = {
  async recordActivityProgress(input: ActivityProgressInput): Promise<ActivityProgressResult> {
    const { studentId, activityId } = input;
    const progressId = `${studentId}-${activityId}`;
    let result!: ActivityProgressResult;

    await db.transaction('rw', db.progress, db.syncQueue, db.xpTransactions, async () => {
      const previous = await db.progress.get(progressId);
      const version = Number(previous?.version || 0) + 1;
      const completedAt = new Date().toISOString();
      const completed = Boolean(previous?.completed || input.completed);
      const score = Math.max(Number(previous?.score || 0), Number(input.score || 0));
      let xpEarned = 0;
      const xpId = `xp-${studentId}-${activityId}`;
      const existingXp = await db.xpTransactions.get(xpId);
      if (input.completed && !previous?.completed && !existingXp) {
        xpEarned = Math.max(0, Number(input.xpReward || 0));
        if (xpEarned > 0) {
          const transaction: LocalXpTransaction = { id: xpId, studentId, activityId, xp: xpEarned, synced: false, createdAt: completedAt };
          await db.xpTransactions.add(transaction);
          const xpQueueId = `xp-sync-${studentId}-${activityId}`;
          await db.syncQueue.put(queueRecord(xpQueueId, 'EARN_XP', { studentId, activityId, xp: xpEarned, xpTransactionId: xpId }));
        }
      }

      const progress: LocalProgress = {
        id: progressId,
        studentId,
        activityId,
        score,
        bestScore: Math.max(Number(previous?.bestScore || 0), score),
        attemptCount: Number(previous?.attemptCount || 0) + 1,
        completed,
        completedAt: previous?.completedAt && Date.parse(previous.completedAt) > Date.parse(completedAt) ? previous.completedAt : completedAt,
        synced: false,
        version,
        xpAwarded: Number(previous?.xpAwarded || 0) + xpEarned
      };
      await db.progress.put(progress);

      const progressQueueId = `progress-sync-${studentId}-${activityId}-v${version}`;
      await db.syncQueue.put(queueRecord(progressQueueId, input.actionType, {
        studentId, activityId, score: input.score, completed, completedAt, version, xpTransactionId: xpEarned ? xpId : undefined
      }));

      result = { progress, xpEarned, version };
    });

    syncService.notifyQueueChanged();
    void syncService.processQueue();
    return result;
  }
};
