import React, { useEffect, useState } from 'react';
import { studentLessonService } from '../../services/studentLessonService';
import { ContinueLearning } from '../../types';
import { Play, BookOpen, Clock, Sparkles } from 'lucide-react';

interface Props {
  onContinue: (lessonId: number) => void;
}

export const ContinueLearningCard: React.FC<Props> = ({ onContinue }) => {
  const [data, setData] = useState<ContinueLearning | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    studentLessonService.getContinueLearning()
      .then(setData)
      .catch((err) => console.error('Failed to load continue learning data', err))
      .finally(() => setLoading(false));
  }, []);

  if (loading || !data || !data.lessonId) {
    return null; // Hide if no active lesson or loading
  }

  const targetLessonId = data.nextLessonId || data.lessonId;

  return (
    <div className="bg-blue-600 text-white p-5 rounded-2xl shadow-md space-y-3">
      <div className="flex items-center justify-between">
        <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-white/20 uppercase tracking-wider text-sky-100 flex items-center gap-1">
          <Sparkles className="w-3 h-3 text-amber-300" />
          Continue Learning
        </span>
        <span className="text-xs font-bold text-sky-100">{Math.round(data.progressPercentage)}% Completed</span>
      </div>

      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <p className="text-xs text-sky-200 font-semibold">Module: {data.moduleName}</p>
          <h3 className="text-lg font-bold mt-0.5 flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-amber-300" />
            {data.lessonTitle}
          </h3>
        </div>

        <button
          onClick={() => onContinue(targetLessonId)}
          className="px-5 py-2.5 bg-amber-400 hover:bg-amber-300 text-slate-900 font-extrabold text-xs rounded-xl transition-all shadow-md flex items-center justify-center gap-2 flex-shrink-0"
        >
          <Play className="w-4 h-4 fill-current" />
          <span>Resume Lesson</span>
        </button>
      </div>

      {/* Progress Bar */}
      <div className="w-full bg-white/20 rounded-full h-2 overflow-hidden">
        <div
          className="bg-amber-300 h-full rounded-full transition-all duration-500"
          style={{ width: `${Math.min(100, data.progressPercentage || 10)}%` }}
        ></div>
      </div>
    </div>
  );
};
