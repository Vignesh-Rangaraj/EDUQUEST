import api from './api';
import { Parent, StudentOverviewProgress, ParentChildActivityAnalytics } from '../types';

export const parentService = {
  getProfile: async (): Promise<Parent> => {
    const res = await api.get<Parent>('/parent/profile');
    return res.data;
  },

  getChildOverview: async (studentId: number): Promise<StudentOverviewProgress> => {
    const res = await api.get<StudentOverviewProgress>(`/parent/child/${studentId}/overview`);
    return res.data;
  },

  getChildActivityAnalytics: async (studentId: number): Promise<ParentChildActivityAnalytics> => {
    const res = await api.get<ParentChildActivityAnalytics>(`/parent/child/${studentId}/activity-analytics`);
    return res.data;
  }
};
