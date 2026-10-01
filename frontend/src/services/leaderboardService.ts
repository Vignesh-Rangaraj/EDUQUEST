import api from './api';
import { LeaderboardEntry } from '../types';

export const leaderboardService = {
  async getLeaderboard(scope: 'CLASSROOM' | 'SCHOOL' = 'CLASSROOM'): Promise<LeaderboardEntry[]> {
    const response = await api.get<LeaderboardEntry[]>(`/student/leaderboard?scope=${scope}`);
    return response.data;
  }
};
