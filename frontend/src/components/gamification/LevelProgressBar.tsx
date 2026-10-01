import React from 'react';
import { LevelProgress } from '../../types';

interface LevelProgressBarProps {
  level: number;
  xp: number;
  levelProgress?: LevelProgress;
}

export const LevelProgressBar: React.FC<LevelProgressBarProps> = ({ level, xp, levelProgress }) => {
  const percent = levelProgress ? levelProgress.progressPercent : Math.min(100, (xp % 100));
  const xpInLevel = levelProgress ? levelProgress.xpInCurrentLevel : (xp % 100);
  const xpNeeded = levelProgress ? levelProgress.xpNeededForNextLevel : 100;

  return (
    <div className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white rounded-xl p-4 shadow-md">
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-full bg-yellow-400 text-blue-900 font-extrabold flex items-center justify-center text-lg border-2 border-white shadow">
            L{level}
          </div>
          <div>
            <h4 className="font-bold text-base leading-tight">Level {level} Learner</h4>
            <p className="text-xs text-blue-100">{xp} Total XP Earned</p>
          </div>
        </div>
        <div className="text-right">
          <span className="text-xs font-semibold bg-blue-800 bg-opacity-60 px-2.5 py-1 rounded-full text-blue-100">
            {xpInLevel} / {xpNeeded} XP
          </span>
        </div>
      </div>
      <div className="w-full bg-blue-900 bg-opacity-50 rounded-full h-3 p-0.5 border border-blue-400/30">
        <div
          className="bg-gradient-to-r from-yellow-300 to-amber-400 h-full rounded-full transition-all duration-500 shadow"
          style={{ width: `${percent}%` }}
        />
      </div>
    </div>
  );
};
