import api from './api';
import { networkService } from '../offline/networkService';
import { offlineActivityRepository } from '../offline/offlineActivityRepository';
import { offlineContentRepository } from '../offline/offlineContentRepository';
import { offlineModuleRepository } from '../offline/offlineModuleRepository';
import {
  Module,
  CreateModulePayload,
  Activity,
  LessonContent,
  QuizQuestion,
  GameConfiguration,
  StudentModuleProgress
} from '../types';

export const moduleService = {
  // Teacher APIs
  async getTeacherModules(): Promise<Module[]> {
    const response = await api.get<Module[]>('/teacher/modules');
    return response.data;
  },

  async createModule(payload: CreateModulePayload): Promise<Module> {
    const response = await api.post<Module>('/teacher/modules', payload);
    return response.data;
  },

  async updateModule(id: number, payload: CreateModulePayload): Promise<Module> {
    const response = await api.put<Module>(`/teacher/modules/${id}`, payload);
    return response.data;
  },

  async publishModule(id: number): Promise<Module> {
    const response = await api.post<Module>(`/teacher/modules/${id}/publish`);
    return response.data;
  },

  async archiveModule(id: number): Promise<Module> {
    const response = await api.post<Module>(`/teacher/modules/${id}/archive`);
    return response.data;
  },

  async saveLessonContent(activityId: number, content: string, estimatedMinutes?: number): Promise<LessonContent> {
    const response = await api.post<LessonContent>(`/teacher/modules/${activityId}/lesson`, {
      activityId,
      content,
      estimatedMinutes
    });
    return response.data;
  },

  async saveQuizQuestion(activityId: number, question: Partial<QuizQuestion>): Promise<QuizQuestion> {
    const response = await api.post<QuizQuestion>(`/teacher/modules/${activityId}/quiz-question`, {
      activityId,
      ...question
    });
    return response.data;
  },

  async saveGameConfig(activityId: number, jsonConfiguration: string): Promise<GameConfiguration> {
    const response = await api.post<GameConfiguration>(`/teacher/modules/${activityId}/game-config`, {
      activityId,
      jsonConfiguration
    });
    return response.data;
  },

  // Student APIs
  async getStudentModules(): Promise<Module[]> {
    if (networkService.isOnline()) {
      try {
        const response = await api.get<Module[]>('/student/modules');
        await offlineModuleRepository.saveModules(response.data);
        return response.data;
      } catch { /* Fall through to the previously cached catalog. */ }
    }
    return offlineModuleRepository.getStudentModules();
  },

  async getModuleActivities(moduleId: number): Promise<Activity[]> {
    if (networkService.isOnline()) {
      try {
        const response = await api.get<Activity[]>(`/student/modules/${moduleId}/activities`);
        await offlineActivityRepository.saveActivities(response.data as any);
        return response.data;
      } catch { /* Fall through to the module's cached activity list. */ }
    }
    return (await offlineActivityRepository.getActivitiesByModuleId(moduleId))
      .filter((activity) => activity.status === 'PUBLISHED')
      .map((activity) => ({ ...activity, subject: activity.subject as any, activityType: activity.activityType as any, status: activity.status as any, unlockType: activity.unlockType as any }));
  },

  async getLessonContent(activityId: number): Promise<LessonContent> {
    if (networkService.isOnline()) {
      try {
        const response = await api.get<LessonContent>(`/student/activities/${activityId}/lesson`);
        await offlineContentRepository.saveLessonContent(response.data);
        return response.data;
      } catch { /* A cached copy may still be available when the server is unreachable. */ }
    }
    const cached = await offlineContentRepository.getLessonContent(activityId);
    if (!cached) throw new Error('Lesson not available offline.');
    return cached;
  },

  async getQuizQuestions(activityId: number): Promise<QuizQuestion[]> {
    if (networkService.isOnline()) {
      try {
        const response = await api.get<QuizQuestion[]>(`/student/activities/${activityId}/quiz-questions`);
        await offlineContentRepository.saveQuizQuestions(activityId, response.data);
        return response.data;
      } catch { /* A cached copy may still be available when the server is unreachable. */ }
    }
    const cached = await offlineContentRepository.getQuizQuestions(activityId);
    if (!cached.length) throw new Error('Quiz questions are not available offline.');
    return cached;
  },

  async getGameConfig(activityId: number): Promise<GameConfiguration> {
    const response = await api.get<GameConfiguration>(`/student/activities/${activityId}/game-config`);
    return response.data;
  },

  async getStudentModuleProgress(): Promise<StudentModuleProgress[]> {
    const response = await api.get<StudentModuleProgress[]>('/student/module-progress');
    return response.data;
  }
};
