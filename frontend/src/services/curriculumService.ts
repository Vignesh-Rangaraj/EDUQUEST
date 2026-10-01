import api from './api';
import { CurriculumOverview } from '../types';

export const curriculumService = {
  async getCurriculumOverview(): Promise<CurriculumOverview> {
    const response = await api.get<CurriculumOverview>('/admin/curriculum');
    return response.data;
  }
};
