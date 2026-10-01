import React, { useEffect, useState } from 'react';
import { Bot, User, Volume2, Pause, Play, Square } from 'lucide-react';
import { ChatMessageDto } from './aiService';
import { AI_LANGUAGES } from './LanguageSelector';

interface ChatMessageProps {
  message: ChatMessageDto;
  online: boolean;
}

export const ChatMessage: React.FC<ChatMessageProps> = ({ message, online }) => {
  const isUser = message.sender === 'USER';
  const [speaking, setSpeaking] = useState(false);
  const [paused, setPaused] = useState(false);
  useEffect(() => () => { window.speechSynthesis?.cancel(); }, []);
  const stop = () => { window.speechSynthesis?.cancel(); setSpeaking(false); setPaused(false); };
  const speak = () => {
    if (!('speechSynthesis' in window)) return;
    stop();
    const utterance = new SpeechSynthesisUtterance(message.content);
    utterance.lang = AI_LANGUAGES.find((item) => item.code === message.language)?.speech || 'en-US';
    utterance.onend = stop;
    utterance.onerror = stop;
    setSpeaking(true);
    window.speechSynthesis.speak(utterance);
  };
  const togglePause = () => {
    if (window.speechSynthesis.paused) { window.speechSynthesis.resume(); setPaused(false); }
    else { window.speechSynthesis.pause(); setPaused(true); }
  };

  return (
    <div className={`flex gap-3 mb-4 ${isUser ? 'flex-row-reverse' : 'flex-row'}`}>
      <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${
        isUser ? 'bg-blue-600 text-white' : 'bg-blue-100 text-blue-700 shadow-sm'
      }`}>
        {isUser ? <User className="w-5 h-5" /> : <Bot className="w-5 h-5" />}
      </div>

      <div className={`max-w-[80%] rounded-2xl px-4 py-3 text-sm leading-relaxed ${
        isUser
          ? 'bg-blue-600 text-white rounded-tr-none shadow-sm'
          : 'bg-white dark:bg-slate-800 text-slate-800 dark:text-slate-100 rounded-tl-none shadow-sm border border-slate-200 dark:border-slate-700'
      }`}>
        <div className="whitespace-pre-wrap">{message.content}</div>
        {!isUser && <div className="mt-2 flex gap-1" aria-label="Answer audio controls">
          <button type="button" onClick={speak} disabled={!online || !('speechSynthesis' in window)} aria-label="Read answer aloud" title={!online ? 'Voice output is unavailable offline' : ('speechSynthesis' in window ? 'Read aloud' : 'Voice output is unavailable in this browser')} className="rounded p-1 text-indigo-600 hover:bg-indigo-50 disabled:opacity-40"><Volume2 className="h-4 w-4" /></button>
          {speaking && <>
            <button type="button" onClick={togglePause} aria-label={paused ? 'Resume speech' : 'Pause speech'} title={paused ? 'Resume' : 'Pause'} className="rounded p-1 text-indigo-600 hover:bg-indigo-50">{paused ? <Play className="h-4 w-4" /> : <Pause className="h-4 w-4" />}</button>
            <button type="button" onClick={stop} aria-label="Stop speech" title="Stop" className="rounded p-1 text-indigo-600 hover:bg-indigo-50"><Square className="h-4 w-4" /></button>
          </>}
        </div>}
        {message.createdAt && (
          <div className={`text-[10px] mt-1.5 ${isUser ? 'text-indigo-200 text-right' : 'text-slate-400 dark:text-slate-500'}`}>
            {new Date(message.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
          </div>
        )}
      </div>
    </div>
  );
};
