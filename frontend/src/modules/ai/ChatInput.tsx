import React, { useState } from 'react';
import { Send, Sparkles, Lightbulb, HelpCircle, BookOpen, Layers } from 'lucide-react';
import { ChatRequest } from './aiService';
import { VoiceRecorder } from './VoiceRecorder';

interface ChatInputProps {
  onSendMessage: (message: string, action?: ChatRequest['action']) => void;
  disabled?: boolean;
  language: string;
}

export const ChatInput: React.FC<ChatInputProps> = ({ onSendMessage, disabled, language }) => {
  const [text, setText] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!text.trim() || disabled) return;
    onSendMessage(text, 'CHAT');
    setText('');
  };

  const handleActionClick = (action: ChatRequest['action']) => {
    if (disabled) return;
    const prompt = text.trim() ? text : 'Explain the current topic in detail.';
    onSendMessage(prompt, action);
    setText('');
  };

  return (
    <div className="border-t border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 p-4 space-y-3">
      {/* Quick Action Badges */}
      <div className="flex flex-wrap gap-2 text-xs">
        <button
          type="button"
          disabled={disabled}
          onClick={() => handleActionClick('EXPLAIN')}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 hover:bg-indigo-100 dark:hover:bg-indigo-900 transition-colors border border-indigo-200 dark:border-indigo-800 font-medium"
        >
          <Lightbulb className="w-3.5 h-3.5 text-amber-500" />
          Explain Concept
        </button>

        <button
          type="button"
          disabled={disabled}
          onClick={() => handleActionClick('SIMPLIFY')}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-purple-50 dark:bg-purple-950/60 text-purple-700 dark:text-purple-300 hover:bg-purple-100 dark:hover:bg-purple-900 transition-colors border border-purple-200 dark:border-purple-800 font-medium"
        >
          <Layers className="w-3.5 h-3.5 text-purple-500" />
          Simplify Language
        </button>

        <button
          type="button"
          disabled={disabled}
          onClick={() => handleActionClick('HINT')}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-amber-50 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 hover:bg-amber-100 dark:hover:bg-amber-900 transition-colors border border-amber-200 dark:border-amber-800 font-medium"
        >
          <HelpCircle className="w-3.5 h-3.5 text-amber-600" />
          Get Hint
        </button>

        <button
          type="button"
          disabled={disabled}
          onClick={() => handleActionClick('PRACTICE')}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 hover:bg-emerald-100 dark:hover:bg-emerald-900 transition-colors border border-emerald-200 dark:border-emerald-800 font-medium"
        >
          <BookOpen className="w-3.5 h-3.5 text-emerald-500" />
          Practice Questions
        </button>
      </div>

      {/* Input Row */}
      <form onSubmit={handleSubmit} className="flex gap-2 items-end">
        <input
          type="text"
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Ask EduQuest AI Tutor anything..."
          aria-label="Your question"
          disabled={disabled}
          className="flex-1 bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-100 text-sm rounded-xl px-4 py-2.5 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
        />
        <VoiceRecorder language={language} disabled={disabled} onTranscript={(transcript) => setText((current) => current ? `${current} ${transcript}` : transcript)} />
        <button
          type="submit"
          disabled={!text.trim() || disabled}
          className="bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl px-4 py-2.5 flex items-center justify-center gap-2 transition-colors disabled:opacity-50 font-medium text-sm"
        >
          <span>Send</span>
          <Send className="w-4 h-4" />
        </button>
      </form>
    </div>
  );
};
