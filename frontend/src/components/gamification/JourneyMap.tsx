import React from 'react';
import { JourneyStage } from '../../types';

interface JourneyMapProps {
  stages?: JourneyStage[];
}

const DEFAULT_STAGES: JourneyStage[] = [
  { stageIndex: 1, name: 'Village', icon: '🏡', minXp: 0, maxXp: 100, isUnlocked: true, isCurrent: false, progressPercent: 100 },
  { stageIndex: 2, name: 'Farm', icon: '🌾', minXp: 100, maxXp: 250, isUnlocked: true, isCurrent: false, progressPercent: 100 },
  { stageIndex: 3, name: 'Forest', icon: '🌲', minXp: 250, maxXp: 500, isUnlocked: true, isCurrent: false, progressPercent: 100 },
  { stageIndex: 4, name: 'River', icon: '🌊', minXp: 500, maxXp: 1000, isUnlocked: true, isCurrent: true, progressPercent: 94 },
  { stageIndex: 5, name: 'Mountain', icon: '⛰️', minXp: 1000, maxXp: 2000, isUnlocked: false, isCurrent: false, progressPercent: 0 },
  { stageIndex: 6, name: 'Castle', icon: '🏰', minXp: 2000, maxXp: 5000, isUnlocked: false, isCurrent: false, progressPercent: 0 },
];

export const JourneyMap: React.FC<JourneyMapProps> = ({ stages }) => {
  const displayStages = (stages && stages.length > 0) ? stages : DEFAULT_STAGES;

  return (
    <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-200 dark:border-gray-700 p-5 space-y-4">
      <div className="flex items-center justify-between border-b border-gray-100 dark:border-gray-700 pb-3">
        <div>
          <h3 className="font-bold text-gray-900 dark:text-white text-base flex items-center gap-2">
            <span>🗺️</span> Learning Journey Map
          </h3>
          <p className="text-xs text-gray-500 dark:text-gray-400">Travel through stages by gaining XP</p>
        </div>
      </div>

      <div className="relative py-4">
        {/* Connection Line */}
        <div className="absolute top-1/2 left-8 right-8 h-1 bg-gray-200 dark:bg-gray-700 -translate-y-1/2 z-0" />

        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-4 relative z-10">
          {displayStages.map((st) => (
            <div
              key={st.stageIndex}
              className={`flex flex-col items-center p-3.5 rounded-2xl border transition-all text-center ${
                st.isCurrent
                  ? 'bg-amber-50 dark:bg-amber-950/40 border-amber-400 ring-2 ring-amber-400 shadow-md scale-105'
                  : st.isUnlocked
                  ? 'bg-emerald-50/60 dark:bg-emerald-950/20 border-emerald-200 dark:border-emerald-800'
                  : 'bg-gray-50 dark:bg-gray-800/40 border-gray-200 dark:border-gray-700 opacity-60'
              }`}
            >
              <div
                className={`w-12 h-12 rounded-full flex items-center justify-center text-2xl mb-2 shadow ${
                  st.isCurrent
                    ? 'bg-amber-400 text-white animate-pulse'
                    : st.isUnlocked
                    ? 'bg-emerald-500 text-white'
                    : 'bg-gray-300 dark:bg-gray-700 text-gray-500'
                }`}
              >
                {st.icon}
              </div>

              <h4 className="font-bold text-xs text-gray-900 dark:text-white">{st.name}</h4>
              <p className="text-[10px] text-gray-500 dark:text-gray-400 mt-0.5">{st.minXp} XP</p>

              {st.isCurrent ? (
                <span className="mt-2 text-[9px] font-extrabold uppercase bg-amber-500 text-white px-2 py-0.5 rounded-full shadow-sm">
                  Current Stage
                </span>
              ) : st.isUnlocked ? (
                <span className="mt-2 text-[9px] font-bold text-emerald-600 dark:text-emerald-400 bg-emerald-100 dark:bg-emerald-950 px-2 py-0.5 rounded-full">
                  ✓ Unlocked
                </span>
              ) : (
                <span className="mt-2 text-[9px] font-bold text-gray-400 bg-gray-100 dark:bg-gray-700 px-2 py-0.5 rounded-full">
                  🔒 Locked
                </span>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
