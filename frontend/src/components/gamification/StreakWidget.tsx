import React from 'react';

interface StreakWidgetProps {
  currentStreak: number;
  highestStreak: number;
}

export const StreakWidget: React.FC<StreakWidgetProps> = ({ currentStreak, highestStreak }) => {
  return (
    <div className="flex items-center space-x-2 bg-gradient-to-r from-orange-500 to-amber-600 text-white px-3.5 py-1.5 rounded-full shadow-sm">
      <span className="text-xl animate-bounce">🔥</span>
      <div className="flex items-baseline space-x-1">
        <span className="font-extrabold text-base">{currentStreak}</span>
        <span className="text-xs font-medium text-orange-100 uppercase tracking-wide">Day Streak</span>
      </div>
      {highestStreak > currentStreak && (
        <span className="text-[10px] bg-orange-700/60 px-2 py-0.5 rounded-full text-orange-200">
          Best: {highestStreak}d
        </span>
      )}
    </div>
  );
};
