import React from 'react';
import { Plus, MessageSquare, Sparkles, AlertCircle } from 'lucide-react';
import { useChat } from './useChat';
import { ChatWindow } from './ChatWindow';
import { LanguageSelector } from './LanguageSelector';
import { useSearchParams } from 'react-router-dom';

interface AiTutorPageProps {
  lessonId?: number;
}

export const AiTutorPage: React.FC<AiTutorPageProps> = ({ lessonId }) => {
  const [searchParams] = useSearchParams();
  const queryLessonId = searchParams.get('lessonId');
  const contextLessonId = lessonId ?? (queryLessonId ? Number(queryLessonId) : undefined);
  const {
    sessions,
    activeSessionId,
    messages,
    isLoading,
    error,
    sendMessage,
    loadSessionHistory,
    createNewChat,
    language,
    setLanguage,
    online,
    hasMore,
    loadOlderMessages
  } = useChat(contextLessonId);

  return (
    <div className="h-[calc(100vh-5rem)] max-w-7xl mx-auto p-3 md:p-6 flex flex-col md:flex-row gap-4 md:gap-6 min-h-0">
      {/* Sessions Sidebar */}
      <div className="w-full max-h-36 md:max-h-none md:w-80 bg-white dark:bg-slate-850 rounded-2xl p-4 border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col shrink-0">
        <div className="flex items-center justify-between mb-4 pb-3 border-b border-slate-100 dark:border-slate-800">
          <div className="flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
            <h2 className="font-bold text-slate-800 dark:text-slate-100 text-sm">Learning Sessions</h2>
          </div>
          <button
            onClick={() => createNewChat()}
            disabled={isLoading}
            className="p-1.5 rounded-xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-300 hover:bg-indigo-100 dark:hover:bg-indigo-900 transition-colors"
            title="Start New Chat"
          >
            <Plus className="w-4 h-4" />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto space-y-1.5 pr-1">
          {sessions.length === 0 ? (
            <div className="text-xs text-slate-400 text-center py-6">No previous chats</div>
          ) : (
            sessions.map(s => {
              const isActive = s.id === activeSessionId;
              return (
                <button
                  key={s.id}
                  onClick={() => loadSessionHistory(s.id)}
                  className={`w-full text-left p-3 rounded-xl flex items-center gap-2.5 transition-all text-xs ${
                    isActive
                      ? 'bg-indigo-600 text-white font-medium shadow-sm'
                      : 'hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300'
                  }`}
                >
                  <MessageSquare className={`w-4 h-4 shrink-0 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                  <span className="truncate flex-1">{s.title || 'AI Chat Session'}</span>
                </button>
              );
            })
          )}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 min-h-0 flex flex-col overflow-hidden">
        <div className="flex justify-end mb-2"><LanguageSelector value={language} onChange={setLanguage} /></div>
        {error && (
          <div className="mb-3 p-3 bg-rose-50 dark:bg-rose-950/60 border border-rose-200 dark:border-rose-800 rounded-xl flex items-center gap-2 text-xs text-rose-700 dark:text-rose-300">
            <AlertCircle className="w-4 h-4 shrink-0 text-rose-500" />
            <span>{error}</span>
          </div>
        )}

        <ChatWindow
          messages={messages}
          isLoading={isLoading}
          onSendMessage={sendMessage}
          language={language}
          online={online}
          hasMore={hasMore}
          onLoadOlder={loadOlderMessages}
        />
      </div>
    </div>
  );
};

export default AiTutorPage;
