import api from './api';
import { Student, StudentOverviewProgress, Achievement, StudentProgress } from '../types';
import { networkService } from '../offline/networkService';
import { offlineStudentRepository } from '../offline/offlineStudentRepository';

export const studentService = {
  getProfile: async (): Promise<Student> => {
    if (networkService.isOnline()) {
      try {
        const res = await api.get<Student>('/student/profile');
        await offlineStudentRepository.saveStudentProfile({
          userAccountId: res.data.userId,
          username: res.data.username,
          fullName: res.data.fullName,
          xp: res.data.xp,
          level: res.data.level,
          profileData: JSON.stringify(res.data),
          lastUpdated: new Date().toISOString()
        });
        return res.data;
      } catch { /* Fall back to the last profile cached for this authenticated username. */ }
    }
    const username = (() => {
      try { return (JSON.parse(localStorage.getItem('eduquest_user') || '{}') as { username?: string }).username; }
      catch { return undefined; }
    })();
    const cached = username ? await offlineStudentRepository.getStudentProfileDataByUsername<Student>(username) : undefined;
    if (!cached) throw new Error('Student profile is not available offline.');
    return cached;
  },
  getProgress: async (): Promise<StudentProgress[]> => {
    const res = await api.get<StudentProgress[]>('/student/progress');
    return res.data;
  },
  getProgressOverview: async (): Promise<StudentOverviewProgress> => {
    const res = await api.get<StudentOverviewProgress>('/student/progress/overview');
    return res.data;
  },
  getAchievements: async (): Promise<Achievement[]> => {
    const res = await api.get<Achievement[]>('/student/achievements');
    return res.data;
  }
};
