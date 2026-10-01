import api from './api';
import { User } from '../types';

export interface LoginResponseData {
  token: string;
  userId: number;
  username: string;
  fullName: string;
  role: User['role'];
}

export const authService = {
  login: async (username: string, password: string): Promise<LoginResponseData> => {
    const response = await api.post<LoginResponseData>('/auth/login', { username, password });
    return response.data;
  },
  getCurrentUser: async (): Promise<User> => {
    const response = await api.get<User>('/auth/me');
    return response.data;
  }
};
