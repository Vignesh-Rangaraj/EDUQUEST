import api from './api';
import {
  GamificationSummary,
  CoinTransaction,
  StudentBadgeItem,
  DailyMission,
  TeacherChallengeWrapper,
  JourneyStage,
  TeacherChallengeItem
} from '../types';

export const gamificationService = {
  getSummary: async (): Promise<GamificationSummary> => {
    const response = await api.get('/student/gamification/summary');
    return response.data;
  },

  getCoinHistory: async (): Promise<CoinTransaction[]> => {
    const response = await api.get('/student/gamification/coin-history');
    return response.data;
  },

  getBadges: async (): Promise<StudentBadgeItem[]> => {
    const response = await api.get('/student/gamification/badges');
    return response.data;
  },

  getDailyMissions: async (): Promise<DailyMission[]> => {
    const response = await api.get('/student/gamification/daily-missions');
    return response.data;
  },

  claimDailyMission: async (missionKey: string): Promise<{ success: boolean }> => {
    const response = await api.post('/student/gamification/daily-missions/claim', { missionKey });
    return response.data;
  },

  getWeeklyChallenges: async (): Promise<TeacherChallengeWrapper[]> => {
    const response = await api.get('/student/gamification/weekly-challenges');
    return response.data;
  },

  claimWeeklyChallenge: async (challengeId: number): Promise<{ success: boolean }> => {
    const response = await api.post('/student/gamification/weekly-challenges/claim', { challengeId });
    return response.data;
  },

  getJourneyMap: async (): Promise<JourneyStage[]> => {
    const response = await api.get('/student/gamification/journey-map');
    return response.data;
  },

  getLeaderboard: async (scope = 'SCHOOL', scopeId?: number, metric = 'XP') => {
    const params: Record<string, any> = { scope, metric };
    if (scopeId) params.scopeId = scopeId;
    const response = await api.get('/student/gamification/leaderboard', { params });
    return response.data;
  },

  // Teacher Challenge Management Endpoints
  createTeacherChallenge: async (challenge: Partial<TeacherChallengeItem>): Promise<TeacherChallengeItem> => {
    const response = await api.post('/teacher/challenges', challenge);
    return response.data;
  },

  updateTeacherChallenge: async (id: number, challenge: Partial<TeacherChallengeItem>): Promise<TeacherChallengeItem> => {
    const response = await api.put(`/teacher/challenges/${id}`, challenge);
    return response.data;
  },

  deleteTeacherChallenge: async (id: number): Promise<{ success: boolean; message: string }> => {
    const response = await api.delete(`/teacher/challenges/${id}`);
    return response.data;
  },

  archiveTeacherChallenge: async (id: number): Promise<TeacherChallengeItem> => {
    const response = await api.patch(`/teacher/challenges/${id}/archive`);
    return response.data;
  },

  restoreTeacherChallenge: async (id: number): Promise<TeacherChallengeItem> => {
    const response = await api.patch(`/teacher/challenges/${id}/restore`);
    return response.data;
  },

  getTeacherChallenges: async (classroomId: number): Promise<TeacherChallengeItem[]> => {
    const response = await api.get(`/teacher/challenges/classroom/${classroomId}`);
    return response.data;
  },

  getMyTeacherChallenges: async (): Promise<TeacherChallengeItem[]> => {
    const response = await api.get('/teacher/challenges/my-challenges');
    return response.data;
  },

  getTeacherChallengeStats: async () => {
    const response = await api.get('/teacher/challenges/stats');
    return response.data;
  },

  getAdminChallengeStats: async () => {
    const response = await api.get('/teacher/challenges/admin-stats');
    return response.data;
  }
};
