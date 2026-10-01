import { useState, useEffect, useCallback } from 'react';
import { aiService, ChatMessageDto, ChatSessionDto, ChatRequest } from './aiService';
import { useAuth } from '../../context/AuthContext';

export function useChat(initialLessonId?: number) {
  const { user } = useAuth();
  const languageKey = `eduquest_ai_language_${user?.id ?? 'default'}`;
  const [sessions, setSessions] = useState<ChatSessionDto[]>([]);
  const [activeSessionId, setActiveSessionId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessageDto[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [language, setLanguageState] = useState(() => localStorage.getItem(languageKey) || 'en');
  const [online, setOnline] = useState(() => typeof navigator === 'undefined' || navigator.onLine);
  const [hasMore, setHasMore] = useState(false);
  const [nextBeforeId, setNextBeforeId] = useState<number | undefined>();

  const setLanguage = (value: string) => {
    setLanguageState(value);
    localStorage.setItem(languageKey, value);
  };

  const fetchSessions = useCallback(async () => {
    try {
      const data = await aiService.getSessions();
      setSessions(data);
    } catch (err: any) {
      console.error('Failed to load chat sessions:', err);
    }
  }, []);

  const loadSessionHistory = useCallback(async (sessionId: number) => {
    try {
      setIsLoading(true);
      setError(null);
      const page = await aiService.getMessages(sessionId);
      setActiveSessionId(sessionId);
      setMessages(page.messages);
      setHasMore(page.hasMore);
      setNextBeforeId(page.nextBeforeId || undefined);
    } catch (err: any) {
      setError('Failed to load session history.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const loadOlderMessages = async () => {
    if (!activeSessionId || !hasMore || !nextBeforeId) return;
    try {
      const page = await aiService.getMessages(activeSessionId, nextBeforeId);
      setMessages((current) => [...page.messages, ...current]);
      setHasMore(page.hasMore);
      setNextBeforeId(page.nextBeforeId || undefined);
    } catch { setError('Could not load older messages.'); }
  };

  const createNewChat = async (title?: string) => {
    try {
      setIsLoading(true);
      setError(null);
      const newSession = await aiService.createSession(title || 'New AI Tutor Chat', 'CHAT', initialLessonId);
      setSessions(prev => [newSession, ...prev]);
      setActiveSessionId(newSession.id);
      setMessages([]);
      setHasMore(false);
      setNextBeforeId(undefined);
    } catch (err: any) {
      setError('Failed to create new chat session.');
    } finally {
      setIsLoading(false);
    }
  };

  const sendMessage = async (messageText: string, action: ChatRequest['action'] = 'CHAT') => {
    if (!messageText.trim() && action === 'CHAT') return;
    if (!online) { setError('AI Tutor requires internet connectivity.'); return; }

    const userMsg: ChatMessageDto = {
      sender: 'USER',
      content: messageText,
      language,
      createdAt: new Date().toISOString()
    };

    setMessages(prev => [...prev, userMsg]);
    setIsLoading(true);
    setError(null);

    try {
      const res = await aiService.sendMessage({
        sessionId: activeSessionId || undefined,
        message: messageText,
        lessonId: initialLessonId,
        action,
        language
      });

      if (!activeSessionId) {
        setActiveSessionId(res.sessionId);
      }

      const aiMsg: ChatMessageDto = {
        id: res.messageId,
        sessionId: res.sessionId,
        sender: 'AI',
        content: res.reply,
        language,
        createdAt: res.timestamp
      };

      setMessages(prev => [...prev, aiMsg]);
      await fetchSessions();
    } catch (err: any) {
      const serverMessage = err.response?.data?.message;
      if (typeof serverMessage === 'string' && serverMessage.trim()) {
        setError(serverMessage);
      } else if (!navigator.onLine) {
        setError('AI Tutor requires internet connectivity.');
      } else if (!err.response) {
        setError('Could not reach the EduQuest backend. Check that the backend is running.');
      } else if (err.response.status === 401 || err.response.status === 403) {
        setError('Your EduQuest session is no longer authorized. Please sign in again.');
      } else {
        setError('AI Tutor could not complete this request. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchSessions();
  }, [fetchSessions]);

  useEffect(() => {
    if (!activeSessionId && sessions.length > 0) loadSessionHistory(sessions[0].id);
  }, [activeSessionId, sessions, loadSessionHistory]);

  useEffect(() => {
    const onOnline = () => setOnline(true);
    const onOffline = () => setOnline(false);
    window.addEventListener('online', onOnline);
    window.addEventListener('offline', onOffline);
    return () => { window.removeEventListener('online', onOnline); window.removeEventListener('offline', onOffline); };
  }, []);

  return {
    sessions,
    activeSessionId,
    messages,
    isLoading,
    error,
    language,
    setLanguage,
    online,
    hasMore,
    loadOlderMessages,
    sendMessage,
    loadSessionHistory,
    createNewChat
  };
}
