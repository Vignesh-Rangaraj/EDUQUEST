import React, { useEffect, useRef, useState } from 'react';
import { Mic, MicOff } from 'lucide-react';
import { AI_LANGUAGES } from './LanguageSelector';

type Recognition = {
  lang: string; continuous: boolean; interimResults: boolean;
  onresult: ((event: any) => void) | null; onerror: ((event: any) => void) | null;
  onend: (() => void) | null; start: () => void; stop: () => void;
};

export const VoiceRecorder: React.FC<{ language: string; onTranscript: (text: string) => void; disabled?: boolean }> = ({ language, onTranscript, disabled }) => {
  const [listening, setListening] = useState(false);
  const [error, setError] = useState('');
  const recognition = useRef<Recognition | null>(null);

  useEffect(() => () => { try { recognition.current?.stop(); } catch { /* already stopped */ } }, []);

  const toggle = () => {
    setError('');
    if (listening) { recognition.current?.stop(); setListening(false); return; }
    if (!['en', 'ta', 'hi'].includes(language)) { setError('Voice input supports English, Tamil, and Hindi.'); return; }
    const browser = window as any;
    const Constructor = browser.SpeechRecognition || browser.webkitSpeechRecognition;
    if (!Constructor) { setError('Voice input is unavailable in this browser.'); return; }
    const speechLanguage = AI_LANGUAGES.find((item) => item.code === language)?.speech || 'en-US';
    const instance: Recognition = new Constructor();
    instance.lang = speechLanguage;
    instance.continuous = false;
    instance.interimResults = false;
    instance.onresult = (event) => {
      const transcript = event.results?.[0]?.[0]?.transcript;
      if (transcript) onTranscript(transcript);
    };
    instance.onerror = (event) => {
      setListening(false);
      setError(event.error === 'not-allowed' ? 'Microphone permission was denied.' : 'Voice input is unavailable. Please type your message.');
    };
    instance.onend = () => setListening(false);
    recognition.current = instance;
    try { instance.start(); setListening(true); }
    catch { setListening(false); setError('Could not start the microphone.'); }
  };

  return <div className="flex flex-col items-center">
    <button type="button" aria-label={listening ? 'Stop recording' : 'Start voice input'} aria-pressed={listening}
      disabled={disabled} onClick={toggle} className={`rounded-xl p-2.5 ${listening ? 'bg-rose-100 text-rose-700 animate-pulse' : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'} disabled:opacity-50`}>
      {listening ? <MicOff className="h-4 w-4" /> : <Mic className="h-4 w-4" />}
    </button>
    <span aria-live="polite" className="sr-only">{error || (listening ? 'Listening' : '')}</span>
    {error && <span className="absolute bottom-20 left-4 rounded bg-rose-50 p-2 text-xs text-rose-700" role="status">{error}</span>}
  </div>;
};
