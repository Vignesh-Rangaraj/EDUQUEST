import { db, LocalStudent } from './db';

export const offlineStudentRepository = {
  async saveStudentProfile(student: LocalStudent): Promise<void> {
    const existing = await db.students.where('username').equals(student.username).first();
    if (existing) {
      await db.students.update(existing.id!, { ...student, lastUpdated: new Date().toISOString() });
    } else {
      await db.students.add({ ...student, lastUpdated: new Date().toISOString() });
    }
  },

  async getStudentProfileByUsername(username: string): Promise<LocalStudent | undefined> {
    return await db.students.where('username').equals(username).first();
  },

  async getStudentProfileDataByUsername<T>(username: string): Promise<T | undefined> {
    const student = await this.getStudentProfileByUsername(username);
    if (!student?.profileData) return undefined;
    try { return JSON.parse(student.profileData) as T; } catch { return undefined; }
  },

  async addXpToCachedProfile(studentId: number, xp: number): Promise<void> {
    const student = await db.students.filter((row) => row.profileData != null).toArray();
    const match = student.find((row) => {
      try { return Number((JSON.parse(row.profileData || '{}') as { id?: number }).id) === Number(studentId); }
      catch { return false; }
    });
    if (!match?.id || !match.profileData) return;
    try {
      const profile = JSON.parse(match.profileData) as { xp?: number; level?: number };
      profile.xp = Number(profile.xp || 0) + xp;
      await db.students.update(match.id, { xp: profile.xp, level: profile.level, profileData: JSON.stringify(profile), lastUpdated: new Date().toISOString() });
    } catch { /* Ignore malformed legacy profile snapshots. */ }
  }
};
