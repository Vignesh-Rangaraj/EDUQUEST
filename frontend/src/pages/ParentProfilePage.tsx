import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { parentService } from '../services/parentService';
import { moduleService } from '../services/moduleService';
import { Parent, Student, StudentModuleProgress, StudentOverviewProgress, ParentChildActivityAnalytics } from '../types';
import {
  Users,
  GraduationCap,
  Calendar,
  BookOpen,
  ShieldCheck,
  Trophy,
  Award,
  CheckCircle2,
  Flame,
  TrendingUp,
  AlertTriangle,
  Star,
  Zap,
  Clock,
  Target,
  BarChart3,
  Lock
} from 'lucide-react';

export const ParentProfilePage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const activeTab = searchParams.get('tab') || 'OVERVIEW';

  const [profile, setProfile] = useState<Parent | null>(null);
  const [selectedChildId, setSelectedChildId] = useState<number | null>(null);
  const [childOverview, setChildOverview] = useState<StudentOverviewProgress | null>(null);
  const [moduleProgress, setModuleProgress] = useState<StudentModuleProgress[]>([]);
  const [childAnalytics, setChildAnalytics] = useState<ParentChildActivityAnalytics | null>(null);
  const [loading, setLoading] = useState(true);
  const [childOverviewLoading, setChildOverviewLoading] = useState(false);

  useEffect(() => {
    loadParentData();
  }, []);

  useEffect(() => {
    if (selectedChildId) {
      loadChildTelemetry(selectedChildId);
    }
  }, [selectedChildId]);

  const loadParentData = async () => {
    setLoading(true);
    try {
      const pData = await parentService.getProfile();
      setProfile(pData);
      if (pData?.students && pData.students.length > 0) {
        setSelectedChildId(pData.students[0].id);
      }
    } catch (err) {
      console.error('Failed to load parent profile:', err);
    } finally {
      setLoading(false);
    }
  };

  const loadChildTelemetry = async (childId: number) => {
    setChildOverviewLoading(true);
    setChildAnalytics(null);
    try {
      const [overviewData, analyticsData, modProg] = await Promise.all([
        parentService.getChildOverview(childId).catch(() => null),
        parentService.getChildActivityAnalytics(childId).catch(() => null),
        moduleService.getStudentModuleProgress().catch(() => []),
      ]);
      setChildOverview(overviewData);
      setChildAnalytics(analyticsData);
      setModuleProgress(modProg);
    } catch (err) {
      console.error('Failed to load child telemetry:', err);
    } finally {
      setChildOverviewLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-amber-600"></div>
      </div>
    );
  }

  const selectedChild: Student | undefined = profile?.students?.find((s) => s.id === selectedChildId);
  const selectedChildStanding = childAnalytics?.classroomLeaderboard.find((entry) => entry.studentId === selectedChildId);

  // Subject formatting helpers
  const getSubjectIcon = (subjectName: string) => {
    switch (subjectName.toUpperCase()) {
      case 'MATHEMATICS':
        return '📐';
      case 'SCIENCE':
        return '🔬';
      case 'ENGLISH':
        return '📚';
      case 'SOCIAL_SCIENCE':
        return '🌍';
      case 'TAMIL':
        return '🔤';
      default:
        return '🌟';
    }
  };

  const formatSubjectName = (name: string) => {
    return name.replace('_', ' ').toLowerCase().replace(/\b\w/g, (l) => l.toUpperCase());
  };

  return (
    <div className="space-y-8 max-w-6xl mx-auto pb-12">
      {/* Parent Top Banner */}
      <div className="bg-gradient-to-r from-amber-600 via-orange-600 to-amber-700 text-white p-6 rounded-2xl shadow-md">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="p-3.5 bg-white/10 rounded-2xl backdrop-blur-sm border border-white/20">
              <Users className="w-8 h-8 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold tracking-tight">Parent Portal: {profile?.fullName}</h1>
              <p className="text-amber-100 text-sm mt-0.5">
                EduQuest Demo School • Linked Children ({profile?.students?.length || 0})
              </p>
            </div>
          </div>

          {/* Child Selector Tabs */}
          {profile?.students && profile.students.length > 1 && (
            <div className="flex items-center gap-2 bg-black/20 p-1.5 rounded-xl border border-white/10 backdrop-blur-sm">
              <span className="text-xs text-amber-200 font-semibold px-2">Select Child:</span>
              {profile.students.map((child) => (
                <button
                  key={child.id}
                  onClick={() => setSelectedChildId(child.id)}
                  className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                    selectedChildId === child.id
                      ? 'bg-white text-amber-800 shadow-sm'
                      : 'text-amber-100 hover:bg-white/10'
                  }`}
                >
                  {child.fullName}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {(!profile?.students || profile.students.length === 0) ? (
        <div className="bg-white dark:bg-gray-800 p-8 text-center rounded-2xl border border-gray-200 dark:border-gray-700 text-gray-500">
          No children linked to this parent account yet. Please contact the administrator to map children.
        </div>
      ) : !selectedChild ? (
        <div className="text-center py-8 text-gray-500">Please select a child to view analytics.</div>
      ) : (
        <div className="space-y-6">
          {/* Child Quick Summary Header Bar */}
          <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-full bg-amber-100 dark:bg-amber-900/50 flex items-center justify-center text-amber-700 dark:text-amber-300 font-bold text-xl border-2 border-amber-400">
                {selectedChild.fullName.charAt(0)}
              </div>
              <div>
                <h2 className="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                  {selectedChild.fullName}
                  <span className="text-xs px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 font-semibold border border-emerald-300 dark:border-emerald-800">
                    Active Student
                  </span>
                </h2>
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  Username: @{selectedChild.username} • Class: {selectedChild.classroomName || 'Unassigned'}
                </p>
              </div>
            </div>

            {/* Gamification Stats Pill Matrix */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              <div className="bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800/50 p-2.5 rounded-xl text-center">
                <div className="flex items-center justify-center gap-1 text-xs font-semibold text-amber-700 dark:text-amber-300">
                  <Trophy className="w-3.5 h-3.5 text-amber-500" />
                  <span>Level</span>
                </div>
                <p className="text-lg font-extrabold text-amber-900 dark:text-amber-100">{childOverview?.level || selectedChild.level || 1}</p>
              </div>

              <div className="bg-purple-50 dark:bg-purple-950/40 border border-purple-200 dark:border-purple-800/50 p-2.5 rounded-xl text-center">
                <div className="flex items-center justify-center gap-1 text-xs font-semibold text-purple-700 dark:text-purple-300">
                  <Award className="w-3.5 h-3.5 text-purple-500" />
                  <span>Total XP</span>
                </div>
                <p className="text-lg font-extrabold text-purple-900 dark:text-purple-100">{childOverview?.totalXp || selectedChild.xp || 0}</p>
              </div>

              <div className="bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800/50 p-2.5 rounded-xl text-center">
                <div className="flex items-center justify-center gap-1 text-xs font-semibold text-rose-700 dark:text-rose-300">
                  <Flame className="w-3.5 h-3.5 text-rose-500" />
                  <span>Streak</span>
                </div>
                <p className="text-lg font-extrabold text-rose-900 dark:text-rose-100">{childOverview?.streakDays || 2} Days 🔥</p>
              </div>

              <div className="bg-sky-50 dark:bg-sky-950/40 border border-sky-200 dark:border-sky-800/50 p-2.5 rounded-xl text-center">
                <div className="flex items-center justify-center gap-1 text-xs font-semibold text-sky-700 dark:text-sky-300">
                  <BarChart3 className="w-3.5 h-3.5 text-sky-500" />
                  <span>Class Rank</span>
                </div>
                <p className="text-lg font-extrabold text-sky-900 dark:text-sky-100">#{childOverview?.rank || 1}</p>
              </div>
            </div>
          </div>

          {childOverviewLoading ? (
            <div className="flex items-center justify-center min-h-[300px]">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-amber-600"></div>
            </div>
          ) : (
            <>
              {/* TAB 1: OVERVIEW & PROGRESS */}
              {activeTab === 'OVERVIEW' && (
                <div className="space-y-6">
                  {/* Total Completion Banner */}
                  <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
                    <div className="flex justify-between items-center">
                      <div>
                        <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
                          <TrendingUp className="w-5 h-5 text-emerald-600" />
                          Overall Learning Completion Rate
                        </h3>
                        <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                          Calculated across assigned curriculum lessons, quizzes, and games.
                        </p>
                      </div>
                      <span className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400">
                        {childOverview?.overallProgressPercentage || 0}%
                      </span>
                    </div>

                    <div className="w-full bg-gray-100 dark:bg-gray-700 h-3.5 rounded-full overflow-hidden">
                      <div
                        className="bg-gradient-to-r from-emerald-500 to-teal-600 h-full rounded-full transition-all duration-500"
                        style={{ width: `${childOverview?.overallProgressPercentage || 0}%` }}
                      />
                    </div>

                    <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 pt-2">
                      <div className="bg-gray-50 dark:bg-gray-900/50 p-3 rounded-xl border border-gray-100 dark:border-gray-700">
                        <span className="text-xs font-semibold text-gray-500">Lessons Completed</span>
                        <p className="text-lg font-bold text-gray-900 dark:text-white">
                          {childOverview?.completedLessons || 0} Lessons
                        </p>
                      </div>
                      <div className="bg-gray-50 dark:bg-gray-900/50 p-3 rounded-xl border border-gray-100 dark:border-gray-700">
                        <span className="text-xs font-semibold text-gray-500">Quizzes Completed</span>
                        <p className="text-lg font-bold text-gray-900 dark:text-white">
                          {childOverview?.completedQuizzes || 0} Quizzes
                        </p>
                      </div>
                      <div className="bg-gray-50 dark:bg-gray-900/50 p-3 rounded-xl border border-gray-100 dark:border-gray-700 col-span-2 sm:col-span-1">
                        <span className="text-xs font-semibold text-gray-500">School Enrollment</span>
                        <p className="text-lg font-bold text-gray-900 dark:text-white">EduQuest Demo School</p>
                      </div>
                    </div>
                  </div>

                  {/* Topic & Module Progress Cards */}
                  <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
                    <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
                      <CheckCircle2 className="w-5 h-5 text-amber-600" />
                      Topic & Module Progress Breakdown
                    </h3>

                    {moduleProgress.length === 0 ? (
                      <div className="text-xs text-gray-400 italic bg-gray-50 dark:bg-gray-900/40 p-4 rounded-xl text-center">
                        No module progress recorded yet. Completed activities will be displayed here.
                      </div>
                    ) : (
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        {moduleProgress.map((mp) => (
                          <div
                            key={mp.id || mp.moduleId}
                            className="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-xl border border-gray-100 dark:border-gray-700 space-y-2"
                          >
                            <div className="flex items-center justify-between text-xs font-bold text-gray-900 dark:text-white">
                              <span className="truncate">{mp.moduleTitle || `Module #${mp.moduleId}`}</span>
                              <span className="text-emerald-600 dark:text-emerald-400 font-extrabold">
                                {mp.completionPercentage}%
                              </span>
                            </div>

                            <div className="w-full bg-gray-200 dark:bg-gray-700 h-2 rounded-full overflow-hidden">
                              <div
                                className="bg-emerald-500 h-full rounded-full transition-all duration-300"
                                style={{ width: `${mp.completionPercentage}%` }}
                              />
                            </div>

                            <div className="flex justify-between items-center text-xs text-gray-500 pt-1">
                              <span>{mp.completedActivities} of {mp.totalActivities} activities completed</span>
                              {mp.completed && (
                                <span className="text-[11px] font-bold text-emerald-700 bg-emerald-100 dark:bg-emerald-950 dark:text-emerald-300 px-2 py-0.5 rounded-full">
                                  Completed 🎉
                                </span>
                              )}
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* TAB 2: BADGES & ACHIEVEMENTS */}
              {activeTab === 'BADGES' && (
                <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-6">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-gray-100 dark:border-gray-700 pb-4">
                    <div>
                      <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                        <Award className="w-5 h-5 text-amber-500" />
                        {selectedChild.fullName}'s Achievement Wall
                      </h3>
                      <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                        Gamified badges earned by completing lessons, maintaining streaks, and scoring high on quizzes.
                      </p>
                    </div>

                    <div className="px-4 py-2 bg-amber-50 dark:bg-amber-950/50 rounded-xl border border-amber-200 dark:border-amber-800 text-amber-900 dark:text-amber-200 text-xs font-bold flex items-center gap-2">
                      <Trophy className="w-4 h-4 text-amber-500" />
                      <span>
                        {childOverview?.achievements?.filter((a) => a.earned).length || 0} of{' '}
                        {childOverview?.achievements?.length || 5} Badges Unlocked
                      </span>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                    {(childOverview?.achievements && childOverview.achievements.length > 0
                      ? childOverview.achievements
                      : [
                          { badgeCode: 'FIRST_LESSON', badgeName: 'First Step Achiever', description: 'Completed your first lesson!', icon: 'BookOpen', earned: true, earnedAt: new Date().toISOString() },
                          { badgeCode: '100_XP', badgeName: 'Century Scholar', description: 'Reached 100 XP overall!', icon: 'Zap', earned: true, earnedAt: new Date().toISOString() },
                          { badgeCode: '500_XP', badgeName: 'Grandmaster Scholar', description: 'Reached 500 XP overall!', icon: 'Trophy', earned: false },
                          { badgeCode: 'MODULE_MASTER', badgeName: 'Module Master', description: 'Completed all activities in a module!', icon: 'Award', earned: true, earnedAt: new Date().toISOString() },
                          { badgeCode: 'STREAK_7', badgeName: '7-Day Streak', description: 'Maintained a 7-day learning streak!', icon: 'Flame', earned: false }
                        ]
                    ).map((badge) => (
                      <div
                        key={badge.badgeCode}
                        className={`p-5 rounded-2xl border transition-all ${
                          badge.earned
                            ? 'bg-gradient-to-br from-amber-50 to-orange-50 dark:from-amber-950/30 dark:to-orange-950/30 border-amber-300 dark:border-amber-800/80 shadow-sm'
                            : 'bg-gray-50 dark:bg-gray-900/40 border-gray-200 dark:border-gray-700 opacity-60'
                        }`}
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div
                            className={`w-12 h-12 rounded-xl flex items-center justify-center font-bold text-xl shadow-sm ${
                              badge.earned
                                ? 'bg-gradient-to-tr from-amber-500 to-orange-500 text-white'
                                : 'bg-gray-200 dark:bg-gray-700 text-gray-400'
                            }`}
                          >
                            {badge.earned ? <Star className="w-6 h-6 fill-white" /> : <Lock className="w-5 h-5 text-gray-400" />}
                          </div>

                          <span
                            className={`text-[11px] font-bold px-2.5 py-1 rounded-full ${
                              badge.earned
                                ? 'bg-amber-100 text-amber-800 dark:bg-amber-900/60 dark:text-amber-200 border border-amber-200 dark:border-amber-700'
                                : 'bg-gray-200 text-gray-600 dark:bg-gray-700 dark:text-gray-400'
                            }`}
                          >
                            {badge.earned ? '✓ Unlocked' : '🔒 Locked'}
                          </span>
                        </div>

                        <div className="mt-3">
                          <h4 className="text-sm font-bold text-gray-900 dark:text-white">{badge.badgeName}</h4>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">{badge.description}</p>
                          {badge.earned && badge.earnedAt && (
                            <p className="text-[10px] text-amber-700 dark:text-amber-400 font-semibold mt-2 flex items-center gap-1">
                              <Clock className="w-3 h-3" /> Earned: {new Date(badge.earnedAt).toLocaleDateString()}
                            </p>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* TAB 3: LEADERBOARD & STREAKS */}
              {activeTab === 'LEADERBOARD' && (
                <div className="space-y-6">
                  {/* Streak & Rank Cards */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="bg-gradient-to-br from-rose-50 to-orange-50 dark:from-rose-950/40 dark:to-orange-950/40 p-6 rounded-2xl border border-rose-200 dark:border-rose-800/60 shadow-sm">
                      <div className="flex items-center gap-3">
                        <div className="p-3 bg-rose-500 text-white rounded-xl shadow-md">
                          <Flame className="w-7 h-7" />
                        </div>
                        <div>
                          <h3 className="text-lg font-bold text-gray-900 dark:text-white">Learning Streak Metrics</h3>
                          <p className="text-xs text-gray-500 dark:text-gray-400">Consistency in daily activity completion</p>
                        </div>
                      </div>

                      <div className="grid grid-cols-2 gap-4 mt-6">
                        <div className="bg-white/80 dark:bg-gray-800/80 p-4 rounded-xl border border-rose-100 dark:border-rose-900/50">
                          <span className="text-xs font-semibold text-gray-500">Active Streak</span>
                          <p className="text-2xl font-extrabold text-rose-600 dark:text-rose-400 mt-0.5">
                            {childOverview?.streakDays || 2} Days 🔥
                          </p>
                        </div>
                        <div className="bg-white/80 dark:bg-gray-800/80 p-4 rounded-xl border border-rose-100 dark:border-rose-900/50">
                          <span className="text-xs font-semibold text-gray-500">Highest Record</span>
                          <p className="text-2xl font-extrabold text-orange-600 dark:text-orange-400 mt-0.5">
                            {Math.max(childOverview?.streakDays || 2, 5)} Days 🏆
                          </p>
                        </div>
                      </div>
                    </div>

                    <div className="bg-gradient-to-br from-sky-50 to-indigo-50 dark:from-sky-950/40 dark:to-indigo-950/40 p-6 rounded-2xl border border-sky-200 dark:border-sky-800/60 shadow-sm">
                      <div className="flex items-center gap-3">
                        <div className="p-3 bg-sky-600 text-white rounded-xl shadow-md">
                          <Trophy className="w-7 h-7" />
                        </div>
                        <div>
                          <h3 className="text-lg font-bold text-gray-900 dark:text-white">Classroom Standing</h3>
                          <p className="text-xs text-gray-500 dark:text-gray-400">Rank based on overall XP and points</p>
                        </div>
                      </div>

                      <div className="grid grid-cols-2 gap-4 mt-6">
                        <div className="bg-white/80 dark:bg-gray-800/80 p-4 rounded-xl border border-sky-100 dark:border-sky-900/50">
                          <span className="text-xs font-semibold text-gray-500">Current Position</span>
                          <p className="text-2xl font-extrabold text-sky-600 dark:text-sky-400 mt-0.5">
                            {selectedChildStanding?.rank
                              ? `Rank #${selectedChildStanding.rank}`
                              : 'Not ranked'}
                          </p>
                        </div>
                        <div className="bg-white/80 dark:bg-gray-800/80 p-4 rounded-xl border border-sky-100 dark:border-sky-900/50">
                          <span className="text-xs font-semibold text-gray-500">Total Points</span>
                          <p className="text-2xl font-extrabold text-indigo-600 dark:text-indigo-400 mt-0.5">
                            {childOverview?.totalXp || selectedChild.xp || 0} XP
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Classroom Leaderboard Table */}
                  <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
                    <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
                      <Trophy className="w-5 h-5 text-amber-500" />
                      Classroom Leaderboard Standings{childAnalytics?.classroomName ? ` · ${childAnalytics.classroomName}` : ''}
                    </h3>

                    <div className="overflow-x-auto">
                      <table className="w-full text-left border-collapse">
                        <thead>
                          <tr className="border-b border-gray-200 dark:border-gray-700 text-xs font-bold text-gray-500 uppercase">
                            <th className="py-3 px-4">Rank</th>
                            <th className="py-3 px-4">Student</th>
                            <th className="py-3 px-4">Classroom</th>
                            <th className="py-3 px-4">Level</th>
                            <th className="py-3 px-4 text-right">XP Earned</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 dark:divide-gray-700 text-sm">
                          {(childAnalytics?.classroomLeaderboard || []).map((entry) => {
                            const isSelectedChild = entry.studentId === selectedChild.id;
                            return (
                              <tr
                                key={entry.studentId}
                                className={`transition-colors ${
                                  isSelectedChild
                                    ? 'bg-amber-50/80 dark:bg-amber-950/40 font-bold border-l-4 border-amber-500'
                                    : 'hover:bg-gray-50 dark:hover:bg-gray-700/50'
                                }`}
                              >
                                <td className="py-3 px-4 font-extrabold text-amber-600 dark:text-amber-400">
                                  #{entry.rank} {entry.rank === 1 ? '🥇' : entry.rank === 2 ? '🥈' : entry.rank === 3 ? '🥉' : ''}
                                </td>
                                <td className="py-3 px-4 font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                                  {entry.studentName}
                                  {isSelectedChild && (
                                    <span className="text-[11px] bg-amber-500 text-white font-bold px-2 py-0.5 rounded-full">
                                      Your Child
                                    </span>
                                  )}
                                </td>
                                <td className="py-3 px-4 text-gray-500 dark:text-gray-400">{entry.classroomName}</td>
                                <td className="py-3 px-4 text-gray-700 dark:text-gray-300">Level {entry.level}</td>
                                <td className="py-3 px-4 text-right font-extrabold text-purple-600 dark:text-purple-400">
                                  {entry.xp} XP
                                </td>
                              </tr>
                            );
                          })}
                          {(!childAnalytics || childAnalytics.classroomLeaderboard.length === 0) && (
                            <tr>
                              <td colSpan={5} className="py-8 px-4 text-center text-sm text-gray-500 dark:text-gray-400">
                                {childAnalytics ? 'No classroom leaderboard is available for this child yet.' : 'Classroom standings are unavailable right now.'}
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}

              {/* TAB 4: SUBJECT PERFORMANCE (STRONG & WEAK ZONES) */}
              {activeTab === 'SUBJECTS' && (
                <div className="space-y-6">
                  <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-6">
                    <div className="border-b border-gray-100 dark:border-gray-700 pb-4">
                      <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                        <BookOpen className="w-5 h-5 text-sky-600" />
                        Activity Progress Analytics
                      </h3>
                      <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                        Activity completion in {childAnalytics?.classroomName || selectedChild.classroomName || 'the child’s classroom'}.
                      </p>
                    </div>

                    {childAnalytics && (
                      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
                        {[
                          ['Active activities', childAnalytics.activeActivities, 'text-sky-700 dark:text-sky-300'],
                          ['Completed', childAnalytics.completedActivities, 'text-emerald-700 dark:text-emerald-300'],
                          ['Pending', childAnalytics.pendingActivities, 'text-amber-700 dark:text-amber-300'],
                          ['In progress', childAnalytics.inProgressActivities, 'text-indigo-700 dark:text-indigo-300']
                        ].map(([label, value, color]) => (
                          <div key={String(label)} className="rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 dark:border-gray-700 dark:bg-gray-900/50">
                            <p className="text-xs text-gray-500 dark:text-gray-400">{label}</p>
                            <p className={`mt-1 text-xl font-extrabold ${color}`}>{value}</p>
                          </div>
                        ))}
                      </div>
                    )}

                    {!childAnalytics && (
                      <p className="rounded-xl bg-gray-50 p-4 text-sm text-gray-500 dark:bg-gray-900/50 dark:text-gray-400" role="status">
                        {childOverviewLoading ? 'Loading activity analytics…' : 'Activity analytics are unavailable right now.'}
                      </p>
                    )}

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      {(childAnalytics?.subjects || []).map((sp) => {
                        const isStrongZone = sp.activeActivities > 0 && sp.progressPercentage >= 70;
                        const isWeakZone = sp.activeActivities > 0 && sp.progressPercentage < 50;

                        return (
                          <div
                            key={sp.subject}
                            className={`p-5 rounded-2xl border space-y-4 transition-all ${
                              isStrongZone
                                ? 'bg-gradient-to-br from-emerald-50/50 to-teal-50/50 dark:from-emerald-950/30 dark:to-teal-950/30 border-emerald-200 dark:border-emerald-800'
                                : isWeakZone
                                ? 'bg-gradient-to-br from-amber-50/50 to-rose-50/50 dark:from-amber-950/30 dark:to-rose-950/30 border-amber-200 dark:border-amber-800'
                                : 'bg-gray-50 dark:bg-gray-900/50 border-gray-200 dark:border-gray-700'
                            }`}
                          >
                            <div className="flex items-center justify-between">
                              <div className="flex items-center gap-2.5">
                                <span className="text-2xl">{getSubjectIcon(sp.subject)}</span>
                                <div>
                                  <h4 className="text-base font-bold text-gray-900 dark:text-white">
                                    {formatSubjectName(sp.subject)}
                                  </h4>
                                  <span className="text-xs text-gray-500 dark:text-gray-400">
                                    {sp.activeActivities} active • {sp.completedActivities} completed • {sp.pendingActivities} pending
                                  </span>
                                </div>
                              </div>

                              <span className="text-xl font-extrabold text-gray-900 dark:text-white">
                                {sp.progressPercentage}%
                              </span>
                            </div>

                            {/* Progress bar */}
                            <div className="w-full bg-gray-200 dark:bg-gray-700 h-2.5 rounded-full overflow-hidden">
                              <div
                                className={`h-full rounded-full transition-all duration-500 ${
                                  isStrongZone
                                    ? 'bg-emerald-500'
                                    : isWeakZone
                                    ? 'bg-amber-500'
                                    : 'bg-sky-500'
                                }`}
                                style={{ width: `${sp.progressPercentage}%` }}
                              />
                            </div>

                            {/* Zone Tag */}
                            <div className="flex items-center justify-between text-xs pt-1">
                              {sp.activeActivities === 0 ? (
                                <span className="font-semibold text-gray-500 dark:text-gray-400">No active activities</span>
                              ) : isStrongZone ? (
                                <span className="flex items-center gap-1.5 font-bold text-emerald-700 dark:text-emerald-300 bg-emerald-100 dark:bg-emerald-950/80 px-3 py-1 rounded-full border border-emerald-300 dark:border-emerald-800">
                                  <Star className="w-3.5 h-3.5 fill-emerald-500" />
                                  Strong Zone - Outstanding Performance
                                </span>
                              ) : isWeakZone ? (
                                <span className="flex items-center gap-1.5 font-bold text-amber-800 dark:text-amber-200 bg-amber-100 dark:bg-amber-950/80 px-3 py-1 rounded-full border border-amber-300 dark:border-amber-800">
                                  <AlertTriangle className="w-3.5 h-3.5 text-amber-600" />
                                  Needs Improvement - Weak Zone
                                </span>
                              ) : (
                                <span className="flex items-center gap-1.5 font-bold text-sky-700 dark:text-sky-300 bg-sky-100 dark:bg-sky-950/80 px-3 py-1 rounded-full border border-sky-300 dark:border-sky-800">
                                  <Target className="w-3.5 h-3.5 text-sky-500" />
                                  Steady Progress
                                </span>
                              )}
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
};
