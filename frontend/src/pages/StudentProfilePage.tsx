import React, { useEffect, useState } from 'react';
import { studentService } from '../services/studentService';
import { gamificationService } from '../services/gamificationService';
import { Student, GamificationSummary, StudentBadgeItem } from '../types';
import { LevelProgressBar } from '../components/gamification/LevelProgressBar';
import { StreakWidget } from '../components/gamification/StreakWidget';
import { CoinWalletBadge } from '../components/gamification/CoinWalletBadge';
import { BadgeGallery } from '../components/gamification/BadgeGallery';
import { UserCheck, GraduationCap, Users, Calendar, ShieldCheck } from 'lucide-react';

export const StudentProfilePage: React.FC = () => {
  const [profile, setProfile] = useState<Student | null>(null);
  const [gamification, setGamification] = useState<GamificationSummary | null>(null);
  const [badges, setBadges] = useState<StudentBadgeItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const studentData = await studentService.getProfile();
        setProfile(studentData);
        const sum = await gamificationService.getSummary();
        setGamification(sum);
        setBadges(sum.badges || []);
      } catch (err) {
        console.error('Failed to load student profile', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-sky-600"></div>
      </div>
    );
  }

  const currentXp = gamification?.xp || profile?.xp || 0;
  const currentLevel = gamification?.level || profile?.level || 1;
  const currentStreak = gamification?.currentStreak || 0;
  const highestStreak = gamification?.highestStreak || 0;
  const coins = gamification?.coins || 0;

  return (
    <div className="space-y-8 max-w-4xl mx-auto">
      {/* Student Banner */}
      <div className="bg-gradient-to-r from-sky-600 to-indigo-700 text-white p-6 rounded-2xl shadow-md flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="p-4 bg-white/10 rounded-2xl backdrop-blur-sm">
            <UserCheck className="w-10 h-10" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">{profile?.fullName}</h1>
            <p className="text-sky-100 text-sm mt-0.5">Student Account • @{profile?.username}</p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <StreakWidget currentStreak={currentStreak} highestStreak={highestStreak} />
          <CoinWalletBadge coins={coins} />
        </div>
      </div>

      {/* Level Progression */}
      <LevelProgressBar level={currentLevel} xp={currentXp} levelProgress={gamification?.levelProgress} />

      {/* Badge & Achievement Gallery */}
      <BadgeGallery unlockedBadges={badges} />

      {/* Details Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
          <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2 border-b border-gray-100 dark:border-gray-700 pb-3">
            <GraduationCap className="w-5 h-5 text-sky-600" />
            Classroom & School
          </h2>

          <div className="space-y-3">
            <div>
              <p className="text-xs font-semibold text-gray-400 uppercase">Assigned Class</p>
              <p className="text-lg font-bold text-gray-900 dark:text-white">Class {profile?.classroomName}</p>
            </div>
            <div>
              <p className="text-xs font-semibold text-gray-400 uppercase">School Name</p>
              <p className="text-base font-semibold text-sky-700 dark:text-sky-300">EduQuest Demo School</p>
            </div>
            <div>
              <p className="text-xs font-semibold text-gray-400 uppercase">School Code</p>
              <p className="text-sm font-mono text-gray-600 dark:text-gray-300">EQ-DEMO-01</p>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
          <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2 border-b border-gray-100 dark:border-gray-700 pb-3">
            <Users className="w-5 h-5 text-emerald-600" />
            Parent & Audit Info
          </h2>

          <div className="space-y-3">
            <div>
              <p className="text-xs font-semibold text-gray-400 uppercase">Linked Parent / Guardian</p>
              <p className="text-lg font-bold text-emerald-600 dark:text-emerald-400">{profile?.parentFullName}</p>
            </div>
            <div>
              <p className="text-xs font-semibold text-gray-400 uppercase">Account Created</p>
              <p className="text-sm text-gray-600 dark:text-gray-300 flex items-center gap-1.5 mt-0.5">
                <Calendar className="w-4 h-4 text-gray-400" />
                {profile?.createdAt ? new Date(profile.createdAt).toLocaleString() : 'N/A'}
              </p>
            </div>
            <div>
              <p className="text-xs font-semibold text-gray-400 uppercase">Last Profile Update</p>
              <p className="text-sm text-gray-600 dark:text-gray-300 flex items-center gap-1.5 mt-0.5">
                <ShieldCheck className="w-4 h-4 text-emerald-500" />
                {profile?.updatedAt ? new Date(profile.updatedAt).toLocaleString() : 'N/A'}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
