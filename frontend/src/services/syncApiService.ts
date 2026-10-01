import api from './api';

export interface SyncItemPayload {
  id: string;
  actionType: string;
  studentId?: number;
  activityId?: number;
  score?: number;
  completedAt?: string;
  payload: string;
}

export interface SyncBatchRequest {
  studentId?: number;
  items: SyncItemPayload[];
}

export interface SyncBatchResponse {
  success: boolean;
  syncedItems: string[];
  failedItems: string[];
  errors: string[];
}

export const syncApiService = {
  async sendSyncRequest(data: SyncBatchRequest): Promise<SyncBatchResponse> {
    const response = await api.post<SyncBatchResponse>('/student/sync', data);
    return response.data;
  }
};
