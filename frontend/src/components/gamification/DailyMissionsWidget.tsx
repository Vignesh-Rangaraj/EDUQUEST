import React, { useState } from 'react';
import { DailyMission } from '../../types';
import { gamificationService } from '../../services/gamificationService';

interface DailyMissionsWidgetProps {
  missions: DailyMission[];
  onRewardClaimed?: () => void;
}

export const DailyMissionsWidget: React.FC<DailyMissionsWidgetProps> = ({ missions, onRewardClaimed }) => {
  const [claiming, setClaiming] = useState<string | null>(null);

  const handleClaim = async (missionKey: string) => {
    setClaiming(missionKey);
    try {
      const res = await gamificationService.claimDailyMission(missionKey);
      if (res.success && onRewardClaimed) {
        onRewardClaimed();
      }
    } catch (e) {
      console.error("Failed to claim daily mission", e);
    } finally {
      setClaiming(null);
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-5">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-bold text-gray-900 text-base flex items-center gap-2">
          <span>🎯</span> Daily Missions
        </h3>
        <span className="text-xs bg-indigo-50 text-indigo-700 font-semibold px-2.5 py-1 rounded-full border border-indigo-100">
          Resets in 24h
        </span>
      </div>

      <div className="space-y-3">
        {missions.map((m) => {
          const percent = Math.min(100, Math.round((m.currentProgress / m.targetCount) * 100));

          return (
            <div key={m.id} className="p-3 rounded-lg border border-gray-100 bg-gray-50/70 hover:bg-gray-50 transition-all">
              <div className="flex items-start justify-between">
                <div>
                  <h4 className="font-semibold text-sm text-gray-800">{m.title}</h4>
                  <p className="text-xs text-gray-500 mt-0.5">{m.description}</p>
                </div>
                <div className="flex items-center space-x-2 text-xs font-semibold">
                  <span className="text-indigo-600 bg-indigo-100/70 px-2 py-0.5 rounded">+{m.xpReward} XP</span>
                  <span className="text-amber-700 bg-amber-100/70 px-2 py-0.5 rounded">+{m.coinReward} 🪙</span>
                </div>
              </div>

              <div className="mt-3 flex items-center gap-3">
                <div className="flex-1 bg-gray-200 rounded-full h-2">
                  <div className="bg-indigo-600 h-2 rounded-full transition-all duration-300" style={{ width: `${percent}%` }} />
                </div>
                <span className="text-xs font-medium text-gray-600 min-w-[45px] text-right">
                  {m.currentProgress}/{m.targetCount}
                </span>

                {m.claimed ? (
                  <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-md border border-emerald-200">
                    ✓ Claimed
                  </span>
                ) : m.completed ? (
                  <button
                    onClick={() => handleClaim(m.missionKey)}
                    disabled={claiming === m.missionKey}
                    className="text-xs font-bold bg-gradient-to-r from-emerald-500 to-teal-600 hover:brightness-105 text-white px-3 py-1 rounded-md shadow-sm transition-all"
                  >
                    {claiming === m.missionKey ? 'Claiming...' : 'Claim Reward'}
                  </button>
                ) : (
                  <span className="text-xs text-gray-400 font-medium px-2 py-1">In Progress</span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
