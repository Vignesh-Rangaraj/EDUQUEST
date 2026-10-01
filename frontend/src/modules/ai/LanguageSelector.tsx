import React from 'react';

export const AI_LANGUAGES = [
  { code: 'en', label: 'English', speech: 'en-US' },
  { code: 'ta', label: 'தமிழ்', speech: 'ta-IN' },
  { code: 'hi', label: 'हिन्दी', speech: 'hi-IN' },
  { code: 'ml', label: 'മലയാളം', speech: 'ml-IN' },
  { code: 'te', label: 'తెలుగు', speech: 'te-IN' },
  { code: 'kn', label: 'ಕನ್ನಡ', speech: 'kn-IN' },
];

export const LanguageSelector: React.FC<{ value: string; onChange: (value: string) => void }> = ({ value, onChange }) => (
  <label className="flex items-center gap-2 text-xs text-slate-600 dark:text-slate-300">
    <span>Language</span>
    <select aria-label="Response language" value={value} onChange={(event) => onChange(event.target.value)}
      className="rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 px-2 py-1.5">
      {AI_LANGUAGES.map((language) => <option key={language.code} value={language.code}>{language.label}</option>)}
    </select>
  </label>
);
