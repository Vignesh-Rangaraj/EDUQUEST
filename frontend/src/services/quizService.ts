import api from './api';
import { SubmitQuizPayload, StudentQuizAttempt } from '../types';

export const quizService = {
  async submitQuiz(payload: SubmitQuizPayload): Promise<StudentQuizAttempt> {
    const response = await api.post<StudentQuizAttempt>('/student/quiz/submit', payload);
    return response.data;
  }
};
