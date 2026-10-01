import React, { useRef, useEffect } from 'react';
import { Sparkles, Loader2 } from 'lucide-react';
import { ChatMessageDto, ChatRequest } from './aiService';
import { ChatMessage } from './ChatMessage';
import { ChatInput } from './ChatInput';

interface ChatWindowProps {
  messages: ChatMessageDto[];
  isLoading: boolean;
  onSendMessage: (message: string, action?: ChatRequest['action']) => void;
  language: string;
  online: boolean;
  hasMore: boolean;
  onLoadOlder: () => void;
}

export const ChatWindow: React.FC<ChatWindowProps> = ({ messages, isLoading, onSendMessage, language, online, hasMore, onLoadOlder }) => {
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  return (
    <div className="flex flex-col h-full bg-slate-50 dark:bg-slate-900 rounded-2xl overflow-hidden border border-slate-200 dark:border-slate-800 shadow-xl">
      {/* Header */}
      <div className="bg-white dark:bg-slate-850 px-6 py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400">
            <Sparkles className="w-5 h-5" />
          </div>
          <div>
            <h3 className="font-semibold text-slate-800 dark:text-slate-100 text-sm">EduQuest AI Tutor</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">Instant AI learning support & assistance</p>
          </div>
        </div>
        <div className="flex items-center gap-1.5 text-xs text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/60 px-2.5 py-1 rounded-full border border-emerald-200 dark:border-emerald-800">
          <span className={`w-2 h-2 rounded-full ${online ? 'bg-emerald-500' : 'bg-amber-500'}`}></span>
          <span>{online ? 'Internet available' : 'Offline'}</span>
        </div>
      </div>

      {!online && <div role="status" className="mx-4 mt-3 rounded-xl border border-amber-200 bg-amber-50 p-3 text-xs text-amber-800">AI Tutor requires internet connectivity. Voice features are unavailable while offline.</div>}

      {/* Messages Scroll Area */}
      <div className="flex-1 overflow-y-auto p-6 space-y-4">
        {hasMore && <div className="text-center"><button type="button" onClick={onLoadOlder} className="rounded-lg px-3 py-2 text-xs font-medium text-indigo-700 hover:bg-indigo-50">Load older messages</button></div>}
        {messages.length === 0 ? (
          <div className="h-full flex flex-col items-center justify-center text-center p-8 space-y-4">
          <div className="p-4 rounded-2xl bg-blue-100 text-blue-700 shadow-sm">
              <Sparkles className="w-8 h-8" />
            </div>
            <div className="max-w-md">
              <h4 className="font-bold text-slate-800 dark:text-slate-100 text-base mb-1">Welcome to EduQuest AI Tutor!</h4>
              <p className="text-xs text-slate-500 dark:text-slate-400">
                Ask any question, request topic explanations, or click one of the quick action buttons below to test your understanding!
              </p>
            </div>
          </div>
        ) : (
          messages.map((msg, idx) => <ChatMessage key={msg.id || idx} message={msg} online={online} />)
        )}

        {isLoading && (
          <div className="flex items-center gap-3 text-slate-400 text-xs py-2">
            <Loader2 className="w-4 h-4 animate-spin text-indigo-500" />
            <span>EduQuest AI Tutor is thinking...</span>
          </div>
        )}
        <div ref={bottomRef} />
      </div>

      {/* Input Component */}
      <ChatInput onSendMessage={onSendMessage} disabled={isLoading || !online} language={language} />
    </div>
  );
};
