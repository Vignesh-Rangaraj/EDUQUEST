import { db, LocalProgress } from './db';

export const offlineProgressRepository = {
  async saveProgress(progress: LocalProgress): Promise<void> {
    const previous = await db.progress.get(progress.id);
    await db.progress.put({ ...progress, version: Math.max(previous?.version || 0, progress.version || 0) + 1 });
  },

  async getStudentProgress(studentId: number): Promise<LocalProgress[]> {
    const sId = Number(studentId);
    return await db.progress.filter((p) => Number(p.studentId) === sId).toArray();
  },

  async getUnsyncedProgress(): Promise<LocalProgress[]> {
    return await db.progress.filter((progress) => progress.synced === false).toArray();
  },

  async markProgressSynced(id: string): Promise<void> {
    await db.progress.update(id, { synced: true });
  },

  async mergeProgress(local: LocalProgress, remote: Partial<LocalProgress>): Promise<LocalProgress> {
    const previous = await db.progress.get(local.id);
    const localDate = local.completedAt ? Date.parse(local.completedAt) : 0;
    const remoteDate = remote.completedAt ? Date.parse(remote.completedAt) : 0;
    const merged: LocalProgress = {
      ...previous,
      ...local,
      ...remote,
      id: local.id,
      studentId: local.studentId,
      activityId: local.activityId,
      score: Math.max(Number(previous?.score || 0), Number(local.score || 0), Number(remote.score || 0)),
      bestScore: Math.max(Number(previous?.bestScore || 0), Number(local.bestScore || 0), Number(remote.bestScore || 0)),
      completed: Boolean(previous?.completed || local.completed || remote.completed),
      completedAt: remoteDate > localDate ? remote.completedAt! : (local.completedAt || previous?.completedAt || new Date().toISOString()),
      version: Math.max(Number(previous?.version || 0), Number(local.version || 0), Number(remote.version || 0)) + 1,
      synced: Boolean(remote.synced)
    };
    await db.progress.put(merged);
    return merged;
  }
};
