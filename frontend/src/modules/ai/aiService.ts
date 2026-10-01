import api from '../../services/api';

export interface ChatMessageDto {
  id?: number;
  sessionId?: number;
  sender: 'USER' | 'AI';
  content: string;
  language?: string;
  createdAt?: string;
}

export interface ChatSessionDto {
  id: number;
  userId: number;
  title: string;
  mode: string;
  lessonId?: number;
  createdAt: string;
  updatedAt: string;
  messages: ChatMessageDto[];
}

export interface ChatRequest {
  sessionId?: number;
  message: string;
  lessonId?: number;
  action?: 'CHAT' | 'EXPLAIN' | 'SIMPLIFY' | 'HINT' | 'PRACTICE';
  language?: string;
}

export interface ChatResponse {
  sessionId: number;
  reply: string;
  messageId: number;
  timestamp: string;
}

export interface ChatHistoryPage {
  messages: ChatMessageDto[];
  nextBeforeId: number | null;
  hasMore: boolean;
}

export const aiService = {
  sendMessage: async (request: ChatRequest): Promise<ChatResponse> => {
    const endpoint = request.action && request.action !== 'CHAT'
      ? `/ai/${request.action.toLowerCase()}`
      : '/ai/chat';
    const response = await api.post<ChatResponse>(endpoint, request);
    return response.data;
  },

  getSessions: async (): Promise<ChatSessionDto[]> => {
    const response = await api.get<ChatSessionDto[]>('/ai/session-summaries');
    return response.data;
  },

  getSessionHistory: async (sessionId: number): Promise<ChatSessionDto> => {
    const response = await api.get<ChatSessionDto>(`/ai/history/${sessionId}`);
    return response.data;
  },

  getMessages: async (sessionId: number, beforeId?: number): Promise<ChatHistoryPage> => {
    const response = await api.get<ChatHistoryPage>(`/ai/history/${sessionId}/messages`, { params: { beforeId } });
    return response.data;
  },

  createSession: async (title?: string, mode?: string, lessonId?: number): Promise<ChatSessionDto> => {
    const response = await api.post<ChatSessionDto>('/ai/session', null, {
      params: { title, mode, lessonId }
    });
    return response.data;
  }
};
