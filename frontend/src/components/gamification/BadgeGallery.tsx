import React from 'react';
import { StudentBadgeItem } from '../../types';

interface BadgeGalleryProps {
  unlockedBadges: StudentBadgeItem[];
}

const ALL_BADGES = [
  { code: 'FIRST_LESSON', name: 'First Step Achiever', category: 'Learning', icon: '🌟', description: 'Completed your 1st lesson' },
  { code: '5_LESSONS', name: 'Knowledge Seeker', category: 'Learning', icon: '📚', description: 'Completed 5 lessons' },
  { code: '10_LESSONS', name: 'Curious Scholar', category: 'Learning', icon: '🎓', description: 'Completed 10 lessons' },
  { code: 'MODULE_MASTER', name: 'Module Master', category: 'Learning', icon: '🏅', description: 'Completed an entire module' },
  { code: '3_MODULES', name: 'Module Conqueror', category: 'Learning', icon: '👑', description: 'Completed 3 modules' },
  { code: 'STREAK_3', name: '3-Day Fire', category: 'Consistency', icon: '🔥', description: 'Maintained a 3-day learning streak' },
  { code: 'STREAK_7', name: 'Week Champion', category: 'Consistency', icon: '⚡', description: 'Maintained a 7-day learning streak' },
  { code: 'STREAK_30', name: 'Unstoppable Scholar', category: 'Consistency', icon: '💎', description: 'Maintained a 30-day learning streak' },
  { code: '100_XP', name: 'Century Scholar', category: 'Achievement', icon: '💯', description: 'Earned 100 total XP' },
  { code: '500_XP', name: 'Grandmaster Scholar', category: 'Achievement', icon: '🌌', description: 'Earned 500 total XP' },
  { code: '1000_XP', name: 'Legendary Scholar', category: 'Achievement', icon: '🚀', description: 'Earned 1000 total XP' },
  { code: 'LEVEL_5', name: 'High Achiever', category: 'Achievement', icon: '🏆', description: 'Reached Level 5' },
  { code: 'COIN_COLLECTOR', name: 'Treasure Hunter', category: 'Achievement', icon: '🪙', description: 'Collected 50 Coins' }
];

export const BadgeGallery: React.FC<BadgeGalleryProps> = ({ unlockedBadges }) => {
  const unlockedCodes = new Set(unlockedBadges.map((b) => b.badgeCode));

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="font-bold text-gray-900 text-base flex items-center gap-2">
            <span>🏅</span> Achievements & Badges
          </h3>
          <p className="text-xs text-gray-500">
            {unlockedBadges.length} of {ALL_BADGES.length} Badges Unlocked
          </p>
        </div>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3">
        {ALL_BADGES.map((b) => {
          const isUnlocked = unlockedCodes.has(b.code);
          const earnedInfo = unlockedBadges.find((ub) => ub.badgeCode === b.code);

          return (
            <div
              key={b.code}
              className={`p-3 rounded-xl border flex flex-col items-center text-center transition-all ${
                isUnlocked
                  ? 'bg-amber-50/40 border-amber-200 shadow-sm'
                  : 'bg-gray-50 border-gray-100 opacity-50 grayscale'
              }`}
            >
              <div className="text-3xl mb-1.5">{b.icon}</div>
              <h4 className="font-bold text-xs text-gray-800 leading-snug">{b.name}</h4>
              <p className="text-[10px] text-gray-500 mt-1">{b.description}</p>
              {isUnlocked && earnedInfo && (
                <span className="mt-2 text-[9px] font-semibold text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded-full">
                  Unlocked {new Date(earnedInfo.earnedAt).toLocaleDateString()}
                </span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
