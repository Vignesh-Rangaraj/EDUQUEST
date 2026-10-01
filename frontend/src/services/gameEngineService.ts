import api from './api';

export const gameEngineService = {
  getGameConfig: async (activityId: number) => {
    const response = await api.get(`/student/game/config/${activityId}`);
    return response.data;
  },

  submitGameAnswers: async (activityId: number, answersPayload: any) => {
    const payload = typeof answersPayload === 'object' && answersPayload !== null && ('answers' in answersPayload || 'gameType' in answersPayload)
      ? answersPayload
      : { answers: answersPayload };
    const response = await api.post(`/student/game/submit/${activityId}`, payload);
    return response.data;
  }
};
