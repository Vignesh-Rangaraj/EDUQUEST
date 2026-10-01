import React, { useState, useEffect } from 'react';
import { TeacherChallengeItem } from '../../types';
import { X, Award, Target, Trophy } from 'lucide-react';

interface ChallengeEditModalProps {
  challenge?: TeacherChallengeItem | null;
  classroomId: number;
  onSave: (challenge: Partial<TeacherChallengeItem>) => Promise<void>;
  onClose: () => void;
}

export const ChallengeEditModal: React.FC<ChallengeEditModalProps> = ({
  challenge,
  classroomId,
  onSave,
  onClose
}) => {
  const [title, setTitle] = useState(challenge?.title || '');
  const [description, setDescription] = useState(challenge?.description || '');
  const [targetType, setTargetType] = useState<TeacherChallengeItem['targetType']>(challenge?.targetType || 'LESSONS_COMPLETED');
  const [targetValue, setTargetValue] = useState<number>(challenge?.targetValue || 1);
  const [xpReward, setXpReward] = useState<number>(challenge?.xpReward || 50);
  const [coinReward, setCoinReward] = useState<number>(challenge?.coinReward || 20);
  const [badgeRewardCode, setBadgeRewardCode] = useState(challenge?.badgeRewardCode || '');
  const [startDate, setStartDate] = useState(challenge?.startDate || new Date().toISOString().split('T')[0]);
  const [endDate, setEndDate] = useState(
    challenge?.endDate || new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  );
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !description.trim()) {
      setError('Please fill in title and description');
      return;
    }

    setSubmitting(true);
    setError(null);
    try {
      await onSave({
        id: challenge?.id,
        classroomId,
        title,
        description,
        targetType,
        targetValue,
        xpReward,
        coinReward,
        badgeRewardCode,
        startDate,
        endDate
      });
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to save challenge');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-lg w-full p-6 shadow-xl border border-gray-200 dark:border-gray-700 space-y-4 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between pb-3 border-b border-gray-100 dark:border-gray-700">
          <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
            <Trophy className="w-5 h-5 text-amber-500" />
            {challenge?.id ? 'Edit Classroom Challenge' : 'Create Weekly Challenge'}
          </h3>
          <button onClick={onClose} className="p-1 text-gray-400 hover:text-gray-600 rounded-lg">
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && (
          <div className="p-3 text-xs rounded-xl bg-red-50 text-red-700 border border-red-200">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
              Challenge Title
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Weekly Math Sprint"
              className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-sm focus:ring-2 focus:ring-amber-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
              Description
            </label>
            <textarea
              rows={2}
              required
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Explain goals and rewards for students..."
              className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-sm focus:ring-2 focus:ring-amber-500 focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                Target Metric
              </label>
              <select
                value={targetType}
                onChange={(e) => setTargetType(e.target.value as any)}
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-xs focus:ring-2 focus:ring-amber-500 focus:outline-none"
              >
                <option value="LESSONS_COMPLETED">Lessons Completed</option>
                <option value="QUIZ_SCORE">Quiz Target Score</option>
                <option value="XP_EARNED">XP Earned</option>
                <option value="STREAK_DAYS">Streak Days</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                Target Goal Value
              </label>
              <input
                type="number"
                min={1}
                required
                value={targetValue}
                onChange={(e) => setTargetValue(Number(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-xs focus:ring-2 focus:ring-amber-500 focus:outline-none"
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                XP Reward
              </label>
              <input
                type="number"
                min={10}
                max={500}
                value={xpReward}
                onChange={(e) => setXpReward(Number(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-xs focus:ring-2 focus:ring-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                Coin Reward
              </label>
              <input
                type="number"
                min={5}
                max={200}
                value={coinReward}
                onChange={(e) => setCoinReward(Number(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-xs focus:ring-2 focus:ring-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                Badge Code
              </label>
              <input
                type="text"
                placeholder="Optional"
                value={badgeRewardCode}
                onChange={(e) => setBadgeRewardCode(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-xs focus:ring-2 focus:ring-amber-500 focus:outline-none"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                Start Date
              </label>
              <input
                type="date"
                required
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-xs"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                End Date
              </label>
              <input
                type="date"
                required
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-xs"
              />
            </div>
          </div>

          <div className="flex items-center justify-end space-x-3 pt-4 border-t border-gray-100 dark:border-gray-700">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold text-gray-600 hover:bg-gray-100 rounded-xl"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="px-5 py-2 text-xs font-bold text-white bg-amber-600 hover:bg-amber-700 rounded-xl shadow"
            >
              {submitting ? 'Saving...' : challenge?.id ? 'Update Challenge' : 'Create Challenge'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
