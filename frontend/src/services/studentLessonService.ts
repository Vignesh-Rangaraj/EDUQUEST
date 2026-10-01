import api from './api';
import { Activity, ContinueLearning } from '../types';
import { networkService } from '../offline/networkService';
import { offlineActivityRepository } from '../offline/offlineActivityRepository';

export const studentLessonService = {
  getLessonDetails: async (id: number): Promise<Activity> => {
    if (networkService.isOnline()) {
      try {
        const res = await api.get<Activity>(`/student/lessons/${id}`);
        await offlineActivityRepository.saveActivities([res.data as any]);
        return res.data;
      } catch { /* Use the last visited lesson snapshot below. */ }
    }
    const cached = await offlineActivityRepository.getActivityById(id);
    if (!cached) throw new Error('Lesson not available offline.');
    return {
      ...cached,
      subject: cached.subject as any,
      activityType: cached.activityType as any,
      status: cached.status as any,
      unlockType: cached.unlockType as any
    };
  },
  completeLesson: async (id: number): Promise<Activity> => {
    const res = await api.post<Activity>(`/student/lessons/${id}/complete`);
    return res.data;
  },
  getContinueLearning: async (): Promise<ContinueLearning> => {
    const res = await api.get<ContinueLearning>('/student/continue-learning');
    return res.data;
  }
};
