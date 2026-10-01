import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { studentService } from '../services/studentService';
import { moduleService } from '../services/moduleService';
import { activityService } from '../services/activityService';
import { leaderboardService } from '../services/leaderboardService';
import { gamificationService } from '../services/gamificationService';
import { offlineProgressRepository } from '../offline/offlineProgressRepository';
import { syncService } from '../offline/syncService';
import { offlineLearningService } from '../offline/offlineLearningService';
import { offlineXpRepository } from '../offline/offlineXpRepository';
import { networkService } from '../offline/networkService';
import { ContinueLearningCard } from '../components/student/ContinueLearningCard';
import { LevelProgressBar } from '../components/gamification/LevelProgressBar';
import { StreakWidget } from '../components/gamification/StreakWidget';
import { CoinWalletBadge } from '../components/gamification/CoinWalletBadge';
import { DailyMissionsWidget } from '../components/gamification/DailyMissionsWidget';
import { JourneyMap } from '../components/gamification/JourneyMap';
import { MiniGamePlayer } from '../components/gamification/MiniGamePlayer';
import { BadgeGallery } from '../components/gamification/BadgeGallery';

import {
  Student,
  Module,
  Activity,
  QuizQuestion,
  LeaderboardEntry,
  GamificationSummary,
  DailyMission,
  JourneyStage,
  Subject,
  DifficultyLevel
} from '../types';
import {
  UserCheck,
  BookOpen,
  CheckCircle,
  Play,
  Trophy,
  Lock,
  Clock,
  Gamepad2,
  X,
  HelpCircle,
  Circle,
  GraduationCap,
  Sparkles,
  Award,
  Compass,
  User,
  Check
} from 'lucide-react';

const SUBJECT_CONFIG: Record<string, { label: string; icon: string; badgeColor: string; cardBg: string; border: string }> = {
  MATHEMATICS: {
    label: 'Mathematics',
    icon: '📐',
    badgeColor: 'bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300',
    cardBg: 'from-blue-600 to-indigo-600',
    border: 'border-blue-200 dark:border-blue-800'
  },
  SCIENCE: {
    label: 'Science',
    icon: '🔬',
    badgeColor: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300',
    cardBg: 'from-emerald-600 to-teal-600',
    border: 'border-emerald-200 dark:border-emerald-800'
  },
  ENGLISH: {
    label: 'English',
    icon: '📚',
    badgeColor: 'bg-purple-100 text-purple-800 dark:bg-purple-950 dark:text-purple-300',
    cardBg: 'from-purple-600 to-violet-600',
    border: 'border-purple-200 dark:border-purple-800'
  },
  SOCIAL_SCIENCE: {
    label: 'Social Science',
    icon: '🌍',
    badgeColor: 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300',
    cardBg: 'from-amber-600 to-orange-600',
    border: 'border-amber-200 dark:border-amber-800'
  },
  TAMIL: {
    label: 'Tamil',
    icon: '🔤',
    badgeColor: 'bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300',
    cardBg: 'from-rose-600 to-pink-600',
    border: 'border-rose-200 dark:border-rose-800'
  }
};

const normalizeSubjectKey = (subj?: string): string => {
  if (!subj) return 'SCIENCE';
  const upper = subj.toUpperCase().trim();
  if (upper.includes('MATH')) return 'MATHEMATICS';
  if (upper.includes('SOCIAL')) return 'SOCIAL_SCIENCE';
  if (upper.includes('ENG')) return 'ENGLISH';
  if (upper.includes('SCI')) return 'SCIENCE';
  if (upper.includes('TAMIL')) return 'TAMIL';
  return upper;
};

export const StudentDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const activeTab = searchParams.get('tab') || 'MATHEMATICS';

  const [profile, setProfile] = useState<Student | null>(null);
  const [pendingXp, setPendingXp] = useState(0);
  const [isOnline, setIsOnline] = useState(networkService.isOnline());
  const [gamification, setGamification] = useState<GamificationSummary | null>(null);
  const [dailyMissions, setDailyMissions] = useState<DailyMission[]>([]);
  const [journeyStages, setJourneyStages] = useState<JourneyStage[]>([]);
  const [modules, setModules] = useState<Module[]>([]);
  const [selectedModule, setSelectedModule] = useState<Module | null>(null);
  const [moduleActivities, setModuleActivities] = useState<Activity[]>([]);
  const [progressMap, setProgressMap] = useState<Record<number, { completed: boolean; score: number; bestScore: number; synced: boolean }>>({});
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [leaderboardScope, setLeaderboardScope] = useState<'CLASSROOM' | 'SCHOOL'>('CLASSROOM');
  const [loading, setLoading] = useState(true);

  // Filter states
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'LESSON' | 'QUIZ' | 'GAME'>('ALL');
  const [subjectFilter, setSubjectFilter] = useState<string>('ALL');
  const [difficultyFilter, setDifficultyFilter] = useState<string>('ALL');

  // Modal states
  const [activeQuiz, setActiveQuiz] = useState<{ activity: Activity; questions: QuizQuestion[] } | null>(null);
  const [quizAnswers, setQuizAnswers] = useState<Record<number, string>>({});
  const [quizResult, setQuizResult] = useState<{ score: number; passed: boolean; correct: number; total: number } | null>(null);
  const [activeGamePreview, setActiveGamePreview] = useState<Activity | null>(null);
  const [submittingQuiz, setSubmittingQuiz] = useState(false);

  const [allActivities, setAllActivities] = useState<Activity[]>([]);

  const loadGamificationData = async () => {
    try {
      const [sum, missions, journey] = await Promise.all([
        gamificationService.getSummary(),
        gamificationService.getDailyMissions(),
        gamificationService.getJourneyMap()
      ]);
      setGamification(sum);
      setDailyMissions(missions);
      setJourneyStages(journey);
    } catch (e) {
      console.error('Failed to load gamification summary', e);
    }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const studentData = await studentService.getProfile();
      setProfile(studentData);
      setPendingXp(await offlineXpRepository.getPendingXp(studentData.id));
      await loadGamificationData();

      const [modList, allStudentActs, lbList, remoteProgress] = await Promise.all([
        moduleService.getStudentModules().catch(() => []),
        activityService.getStudentActivities().catch(() => []),
        leaderboardService.getLeaderboard(leaderboardScope).catch(() => []),
        studentService.getProgress().catch(() => [])
      ]);

      setAllActivities(allStudentActs);
      setLeaderboard(lbList);

      const map: Record<number, { completed: boolean; score: number; bestScore: number; synced: boolean }> = {};

      if (studentData?.id) {
        const localProgress = await offlineProgressRepository.getStudentProgress(Number(studentData.id));
        localProgress.forEach((p) => {
          const actId = Number(p.activityId);
          if (actId) {
            map[actId] = {
              completed: Boolean(p.completed),
              score: Number(p.score || 0),
              bestScore: Number(p.bestScore || p.score || 0),
              synced: Boolean(p.synced)
            };
          }
        });
      }

      if (Array.isArray(remoteProgress)) {
        remoteProgress.forEach((rp: any) => {
          const actId = Number(rp.activityId);
          if (actId) {
            const existing = map[actId];
            map[actId] = {
              completed: Boolean(rp.completed) || Boolean(existing?.completed),
              score: Math.max(Number(rp.score || 0), Number(existing?.score || 0)),
              bestScore: Math.max(Number(rp.score || 0), Number(existing?.bestScore || 0)),
              synced: true
            };
          }
        });
      }

      setProgressMap(map);

      setModules(modList);
      if (modList.length > 0) {
        setSelectedModule(modList[0]);
      } else {
        const dummyModule: Module = {
          id: -1,
          title: 'Class 6 Curriculum Activities',
          description: 'Explore lessons, quizzes, and games created by your teacher.',
          subject: (subjectFilter !== 'ALL' ? subjectFilter : 'SCIENCE') as Subject,
          difficultyLevel: 'BEGINNER' as DifficultyLevel,
          status: 'PUBLISHED',
          estimatedMinutes: 45
        };
        setSelectedModule(dummyModule);
        setModuleActivities(allStudentActs);
      }
    } catch (err) {
      console.error('Failed to load dashboard data', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (['MATHEMATICS', 'SCIENCE', 'ENGLISH', 'SOCIAL_SCIENCE', 'TAMIL'].includes(activeTab)) {
      setSubjectFilter(activeTab);
      const matchMod = modules.find(m => normalizeSubjectKey(m.subject) === activeTab);
      if (matchMod) {
        setSelectedModule(matchMod);
      } else {
        setSelectedModule(null);
      }
    } else if (activeTab === 'ALL_SUBJECTS') {
      setSubjectFilter('ALL');
      setSelectedModule(null);
    }
  }, [activeTab, modules]);

  const handleGameCompleted = async (activityId: number, result: { score: number; xpEarned: number }) => {
    if (!profile?.id) return;

    const actId = Number(activityId);
    const score = Number(result.score || 100);
    const passed = score >= 50;
    const existingProg = progressMap[actId];
    const bestScore = Math.max(existingProg?.bestScore || 0, score);

    const activity = allActivities.find((item) => Number(item.id) === actId);
    await offlineLearningService.recordActivityProgress({
      studentId: Number(profile.id), activityId: actId, score, completed: passed,
      xpReward: Number(activity?.xpReward || result.xpEarned || 10),
      actionType: passed ? 'COMPLETE_ACTIVITY' : 'UPDATE_PROGRESS'
    });
    setPendingXp(await offlineXpRepository.getPendingXp(Number(profile.id)));
    setProgressMap((prev) => ({
      ...prev,
      [actId]: { completed: passed || Boolean(existingProg?.completed), score, bestScore, synced: false }
    }));
  };

  useEffect(() => {
    loadData();
    const unsubscribeNetwork = networkService.subscribe(setIsOnline);

    const unsubscribeSync = syncService.subscribe(() => {
      if (profile?.id) {
        offlineProgressRepository.getStudentProgress(profile.id).then((localProgress) => {
          const map: Record<number, { completed: boolean; score: number; bestScore: number; synced: boolean }> = {};
          localProgress.forEach((p) => {
            map[p.activityId] = {
              completed: p.completed,
              score: p.score,
              bestScore: p.bestScore || p.score,
              synced: p.synced
            };
          });
          setProgressMap(map);
        });
        offlineXpRepository.getPendingXp(profile.id).then(setPendingXp);
        studentService.getProfile().then(setProfile).catch(() => {});
        leaderboardService.getLeaderboard(leaderboardScope).then(setLeaderboard).catch(() => {});
        loadGamificationData();
      }
    });

    return () => {
      unsubscribeNetwork();
      unsubscribeSync();
    };
  }, [profile?.id]);

  useEffect(() => {
    if (!selectedModule) return;
    if (selectedModule.id > 0) {
      let active = true;
      setModuleActivities([]);
      moduleService.getModuleActivities(selectedModule.id)
        .then((acts) => {
          if (active) setModuleActivities(acts.filter((activity) => activity.moduleId === selectedModule.id));
        })
        .catch(() => {
          if (active) setModuleActivities([]);
        });
      return () => { active = false; };
    }

    // Keep the legacy subject learning path when no published module is selected.
    const sKey = normalizeSubjectKey(selectedModule.subject);
    setModuleActivities(allActivities.filter(a => normalizeSubjectKey(a.subject) === sKey));
  }, [selectedModule, allActivities]);

  useEffect(() => {
    leaderboardService.getLeaderboard(leaderboardScope).then(setLeaderboard).catch(() => {});
  }, [leaderboardScope]);

  const handleSelectModule = (mod: Module) => {
    setSelectedModule(mod);
    const sKey = normalizeSubjectKey(mod.subject);
    setSubjectFilter(sKey);
    if (mod.id <= 0) {
      setModuleActivities(allActivities.filter(a => normalizeSubjectKey(a.subject) === sKey));
    }
  };

  const handleOpenActivity = async (act: Activity, isUnlocked: boolean) => {
    if (!isUnlocked) return;

    if (act.activityType === 'LESSON') {
      navigate(`/student/lesson/${act.id}`);
    } else if (act.activityType === 'QUIZ') {
      try {
        const questions = await moduleService.getQuizQuestions(act.id);
        setActiveQuiz({ activity: act, questions });
        setQuizAnswers({});
        setQuizResult(null);
      } catch (e) {
        alert('Could not load quiz questions.');
      }
    } else {
      setActiveGamePreview(act);
    }
  };

  const handleSubmitQuiz = async () => {
    if (!activeQuiz || !profile?.id) return;

    setSubmittingQuiz(true);
    const questions = activeQuiz.questions;
    let correctCount = 0;

    questions.forEach((q) => {
      if (quizAnswers[q.id!] === q.correctAnswer) {
        correctCount++;
      }
    });

    const total = questions.length;
    const score = total > 0 ? Math.round((correctCount / total) * 100) : 100;
    const passed = score >= 50;
    const act = activeQuiz.activity;

    setQuizResult({ score, passed, correct: correctCount, total });

    const actId = Number(act.id);
    const existingProg = progressMap[actId];
    const bestScore = Math.max(existingProg?.bestScore || 0, score);
    await offlineLearningService.recordActivityProgress({
      studentId: Number(profile.id), activityId: actId, score, completed: passed,
      xpReward: Number(act.xpReward || 20),
      actionType: passed ? 'COMPLETE_ACTIVITY' : 'UPDATE_PROGRESS'
    });
    setProgressMap((prev) => ({
      ...prev,
      [actId]: { completed: passed || Boolean(existingProg?.completed), score, bestScore, synced: false }
    }));
    setPendingXp(await offlineXpRepository.getPendingXp(Number(profile.id)));

    setSubmittingQuiz(false);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-sky-600"></div>
      </div>
    );
  }

  const renderActivityBadge = (act: Activity) => {
    if (act.activityType === 'LESSON') {
      return (
        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300">
          📚 LESSON
        </span>
      );
    }
    if (act.activityType === 'QUIZ') {
      return (
        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-purple-100 text-purple-800 dark:bg-purple-900/40 dark:text-purple-300">
          ❓ QUIZ
        </span>
      );
    }
    const gameType = act.gameType || act.activityType || 'GAME';
    const gameTypeLabels: Record<string, { label: string; color: string }> = {
      MATCH_THE_FOLLOWING: { label: '🧩 MATCH', color: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300' },
      TRUE_FALSE: { label: '⚖️ TRUE / FALSE', color: 'bg-teal-100 text-teal-800 dark:bg-teal-900/40 dark:text-teal-300' },
      FILL_IN_THE_BLANK: { label: '✍️ FILL BLANK', color: 'bg-indigo-100 text-indigo-800 dark:bg-indigo-900/40 dark:text-indigo-300' },
      FLASH_CARDS: { label: '🎴 FLASH CARDS', color: 'bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300' },
      WORD_SCRAMBLE: { label: '🔤 WORD SCRAMBLE', color: 'bg-orange-100 text-orange-800 dark:bg-orange-900/40 dark:text-orange-300' },
      SHOOT_THE_ANSWER: { label: '🎯 SHOOT ANSWER', color: 'bg-rose-100 text-rose-800 dark:bg-rose-900/40 dark:text-rose-300' },
      BALLOON_POP: { label: '🎈 BALLOON POP', color: 'bg-pink-100 text-pink-800 dark:bg-pink-900/40 dark:text-pink-300' },
      TREASURE_HUNT: { label: '🏴‍☠️ TREASURE HUNT', color: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/40 dark:text-yellow-300' },
    };
    const info = gameTypeLabels[gameType] || { label: `🎮 ${gameType}`, color: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300' };
    return (
      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${info.color}`}>
        {info.label}
      </span>
    );
  };

  const currentXp = Number(profile?.xp || 0) + pendingXp;
  const currentLevel = gamification?.level || profile?.level || 1;
  const currentStreak = gamification?.currentStreak || 0;
  const highestStreak = gamification?.highestStreak || 0;
  const coins = gamification?.coins || 0;

  const filteredModules = modules.filter((mod) => {
    if (mod.status && mod.status !== 'PUBLISHED') return false;
    if (subjectFilter !== 'ALL' && normalizeSubjectKey(mod.subject) !== normalizeSubjectKey(subjectFilter)) return false;
    if (difficultyFilter !== 'ALL' && mod.difficultyLevel !== difficultyFilter) return false;
    return true;
  });

  const sourceActivities = (() => {
    if (selectedModule && selectedModule.id > 0) {
      return moduleActivities.filter((activity) => activity.moduleId === selectedModule.id);
    }
    if (subjectFilter === 'ALL') {
      return allActivities;
    }
    const subjectMatched = allActivities.filter(a => normalizeSubjectKey(a.subject) === normalizeSubjectKey(subjectFilter));
    if (subjectMatched.length > 0) {
      return subjectMatched;
    }
    return moduleActivities.length > 0 ? moduleActivities : allActivities;
  })();

  const filteredActivities = sourceActivities.filter((act) => {
    if (act.status && act.status !== 'PUBLISHED') return false;
    if (typeFilter === 'LESSON' && act.activityType !== 'LESSON') return false;
    if (typeFilter === 'QUIZ' && act.activityType !== 'QUIZ') return false;
    if (typeFilter === 'GAME' && (act.activityType === 'LESSON' || act.activityType === 'QUIZ')) return false;

    if (subjectFilter !== 'ALL') {
      if (normalizeSubjectKey(act.subject) !== normalizeSubjectKey(subjectFilter)) return false;
    }
    return true;
  });

  const isSubjectTab = ['MATHEMATICS', 'SCIENCE', 'ENGLISH', 'SOCIAL_SCIENCE', 'TAMIL', 'ALL_SUBJECTS'].includes(activeTab);

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      {!isOnline && (
        <div role="status" className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-semibold text-amber-800 dark:border-amber-800 dark:bg-amber-950/40 dark:text-amber-200">
          Offline Mode — cached lessons, quizzes, and progress are available on this device.
        </div>
      )}
      {/* 1. CONSTANT PERSISTENT HEADER (ONLINE MODE & SYNC ALWAYS VISIBLE) */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
        <div>
          <h1 className="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
            <GraduationCap className="w-6 h-6 text-sky-600" />
            <span>Student Learning Portal</span>
          </h1>
          <p className="text-xs text-gray-500 dark:text-gray-400">Classroom: {profile?.classroomName} • EduQuest Demo School</p>
        </div>
        <div className="flex items-center gap-3">
          <StreakWidget currentStreak={currentStreak} highestStreak={highestStreak} />
          <CoinWalletBadge coins={coins} />
        </div>
      </div>

      {/* 2. MODULAR VIEW CONTENT BASED ON ACTIVE NAVIGATION TAB */}

      {/* VIEW A: SUBJECTS & LEARNING PATHS */}
      {isSubjectTab && (
        <div className="space-y-6">
          {/* Continue Learning Top Card */}
          <ContinueLearningCard onContinue={(lessonId) => navigate(`/student/lesson/${lessonId}`)} />

          {/* Subject Banner & Multi-Filter Bar */}
          <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex flex-wrap items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <span className="text-2xl">
                {SUBJECT_CONFIG[activeTab]?.icon || '🌟'}
              </span>
              <div>
                <h2 className="text-base font-bold text-gray-900 dark:text-white">
                  {activeTab === 'ALL_SUBJECTS' ? 'All Subjects Modules' : SUBJECT_CONFIG[activeTab]?.label || activeTab}
                </h2>
                <p className="text-xs text-gray-500">
                  {filteredActivities.length} {filteredActivities.length === 1 ? 'activity' : 'activities'} available for step-by-step learning
                </p>
              </div>
            </div>

            {/* Activity Filters */}
            <div className="flex flex-wrap items-center gap-2.5 ml-auto">
              <select
                value={typeFilter}
                onChange={(e) => setTypeFilter(e.target.value as any)}
                className="text-xs font-semibold px-3 py-1.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/60 text-gray-800 dark:text-gray-200"
              >
                <option value="ALL">All Types (Lessons, Quizzes & Games)</option>
                <option value="LESSON">📚 Lessons Only</option>
                <option value="QUIZ">❓ Quizzes Only</option>
                <option value="GAME">🎮 Games Only</option>
              </select>

              <select
                value={difficultyFilter}
                onChange={(e) => setDifficultyFilter(e.target.value)}
                className="text-xs font-semibold px-3 py-1.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/60 text-gray-800 dark:text-gray-200"
              >
                <option value="ALL">All Difficulties</option>
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
              </select>
            </div>
          </div>

          {/* Subject Modules Selector */}
          {filteredModules.length > 0 && (
            <div className="space-y-3">
              <h3 className="text-sm font-bold text-gray-700 dark:text-gray-300 flex items-center gap-2">
                <BookOpen className="w-4 h-4 text-sky-600" />
                <span>Available Subject Modules</span>
              </h3>
              <div className="flex space-x-3 overflow-x-auto pb-2 scrollbar-none">
                {filteredModules.map((mod) => {
                  const sKey = normalizeSubjectKey(mod.subject);
                  const cfg = SUBJECT_CONFIG[sKey] || { label: mod.subject, icon: '📖', badgeColor: 'bg-sky-100 text-sky-800' };
                  const isSelected = selectedModule?.id === mod.id;
                  return (
                    <button
                      key={mod.id}
                      onClick={() => handleSelectModule(mod)}
                      className={`flex-shrink-0 px-4 py-3 rounded-2xl border transition-all text-left w-60 ${
                        isSelected
                          ? 'bg-sky-600 text-white border-sky-600 shadow-md'
                          : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200 border-gray-200 dark:border-gray-700 hover:border-sky-300'
                      }`}
                    >
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                        isSelected ? 'bg-white/20 text-white' : cfg.badgeColor
                      }`}>
                        {cfg.icon} {cfg.label}
                      </span>
                      <h4 className="text-sm font-bold mt-2 line-clamp-1">{mod.title}</h4>
                      <div className="flex items-center space-x-2 text-xs mt-1 opacity-80">
                        <Clock className="w-3 h-3" />
                        <span>{mod.estimatedMinutes || 30} mins</span>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {selectedModule && selectedModule.id > 0 && (
            <section className="rounded-2xl border border-sky-200 bg-sky-50 p-5 dark:border-sky-900/60 dark:bg-sky-950/30" aria-labelledby="topic-reading-heading">
              <div className="flex items-center gap-2">
                <BookOpen className="h-5 w-5 text-sky-600 dark:text-sky-400" />
                <h3 id="topic-reading-heading" className="text-sm font-bold text-gray-900 dark:text-white">Read first: {selectedModule.title}</h3>
              </div>
              <p className="mt-3 whitespace-pre-line text-sm leading-relaxed text-gray-700 dark:text-gray-200">
                {selectedModule.description?.trim() || 'Your teacher has not added a topic summary yet.'}
              </p>
              <p className="mt-3 text-xs font-semibold text-sky-700 dark:text-sky-300">When you are ready, try the practice activities for this topic below.</p>
            </section>
          )}

          {/* Sequential Activities Learning Path */}
          {filteredActivities.length === 0 ? (
            <div className="bg-white dark:bg-gray-800 p-8 rounded-2xl border border-gray-200 dark:border-gray-700 text-center space-y-3">
              <div className="p-3 bg-amber-50 dark:bg-amber-950/40 text-amber-600 rounded-full w-12 h-12 flex items-center justify-center mx-auto">
                <Clock className="w-6 h-6" />
              </div>
              <p className="text-sm font-bold text-gray-700 dark:text-gray-300">
                {selectedModule && selectedModule.id > 0
                  ? `No practice activities have been added to ${selectedModule.title} yet.`
                  : `No activities published for ${activeTab === 'ALL_SUBJECTS' ? 'this filter' : SUBJECT_CONFIG[activeTab]?.label || activeTab} yet.`}
              </p>
              <p className="text-xs text-gray-500 max-w-sm mx-auto">
                Your classroom teacher can attach lessons, quizzes, and games to a topic module for practice.
              </p>
            </div>
          ) : (
            <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
              <div className="border-b border-gray-100 dark:border-gray-700 pb-3">
                <div className="flex items-center justify-between">
                  <span className="px-3 py-1 rounded-full text-xs font-bold bg-indigo-100 text-indigo-800 dark:bg-indigo-900/40 dark:text-indigo-300">
                    {selectedModule ? selectedModule.title : 'Learning Path'}
                  </span>
                  <span className="text-xs text-gray-400">Topic Practice</span>
                </div>
              </div>

              <div className="space-y-3 pt-1">
                {filteredActivities.map((act, index) => {
                  const actId = Number(act.id);
                  const prog = progressMap[actId];
                  const isCompleted = Boolean(prog?.completed);

                  let isUnlocked = index === 0 || !act.prerequisiteActivityId || isCompleted;
                  if (!isUnlocked && index > 0) {
                    const prevActivity = filteredActivities[index - 1];
                    const prevId = Number(prevActivity?.id);
                    isUnlocked = Boolean(progressMap[prevId]?.completed);
                  }

                  let statusBadge = 'LOCKED';
                  if (isCompleted) {
                    statusBadge = 'COMPLETED';
                  } else if (isUnlocked) {
                    const prevAct = filteredActivities[index - 1];
                    const prevId = Number(prevAct?.id);
                    statusBadge = index === 0 || Boolean(progressMap[prevId]?.completed) ? 'CURRENT' : 'NOT_STARTED';
                  }

                  return (
                    <div
                      key={act.id}
                      className={`p-4 rounded-2xl border transition-all flex items-center justify-between ${
                        statusBadge === 'LOCKED'
                          ? 'bg-gray-50 dark:bg-gray-800/40 border-gray-200 dark:border-gray-700 opacity-60'
                          : statusBadge === 'COMPLETED'
                          ? 'bg-emerald-50/50 dark:bg-emerald-950/20 border-emerald-200 dark:border-emerald-800/40'
                          : statusBadge === 'CURRENT'
                          ? 'bg-sky-50 dark:bg-sky-950/20 border-sky-300 dark:border-sky-700 shadow-sm'
                          : 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700'
                      }`}
                    >
                      <div className="flex items-center space-x-3.5">
                        <div className={`p-3 rounded-xl ${
                          statusBadge === 'LOCKED'
                            ? 'bg-gray-200 dark:bg-gray-700 text-gray-400'
                            : statusBadge === 'COMPLETED'
                            ? 'bg-emerald-100 dark:bg-emerald-900/40 text-emerald-600'
                            : statusBadge === 'CURRENT'
                            ? 'bg-sky-100 dark:bg-sky-900/40 text-sky-600'
                            : 'bg-gray-100 dark:bg-gray-700 text-gray-500'
                        }`}>
                          {statusBadge === 'LOCKED' ? (
                            <Lock className="w-5 h-5" />
                          ) : statusBadge === 'COMPLETED' ? (
                            <CheckCircle className="w-5 h-5" />
                          ) : statusBadge === 'CURRENT' ? (
                            <Play className="w-5 h-5 fill-current" />
                          ) : (
                            <Circle className="w-5 h-5" />
                          )}
                        </div>

                        <div>
                          <div className="flex flex-wrap items-center gap-2">
                            <span className="text-xs font-bold text-gray-400">Step {index + 1}</span>
                            {renderActivityBadge(act)}
                            <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                              statusBadge === 'COMPLETED'
                                ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300'
                                : statusBadge === 'CURRENT'
                                ? 'bg-sky-100 text-sky-800 dark:bg-sky-950 dark:text-sky-300'
                                : statusBadge === 'LOCKED'
                                ? 'bg-gray-200 text-gray-600 dark:bg-gray-700 dark:text-gray-400'
                                : 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300'
                            }`}>
                              {statusBadge === 'COMPLETED' ? '✓ Completed' : statusBadge === 'CURRENT' ? '▶ Current' : statusBadge === 'LOCKED' ? '🔒 Locked' : '○ Not Started'}
                            </span>
                            <span className="text-[10px] font-bold text-amber-500">+{act.xpReward || 10} XP</span>
                          </div>
                          <h4 className="text-sm font-bold text-gray-900 dark:text-white mt-1">{act.title}</h4>
                          {act.description && (
                            <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5 line-clamp-1">{act.description}</p>
                          )}
                        </div>
                      </div>

                      <button
                        onClick={() => handleOpenActivity(act, isUnlocked)}
                        disabled={statusBadge === 'LOCKED'}
                        className={`px-4 py-2 text-xs font-bold rounded-xl flex items-center space-x-1.5 transition-all ${
                          statusBadge === 'LOCKED'
                            ? 'bg-gray-200 text-gray-400 cursor-not-allowed dark:bg-gray-700 dark:text-gray-500'
                            : statusBadge === 'COMPLETED'
                            ? 'bg-emerald-600 text-white hover:bg-emerald-700 shadow-sm'
                            : 'bg-sky-600 text-white hover:bg-sky-700 dark:bg-sky-500 dark:hover:bg-sky-600 shadow-sm'
                        }`}
                      >
                        {statusBadge === 'LOCKED' ? (
                          <span>Locked</span>
                        ) : statusBadge === 'COMPLETED' ? (
                          <><span>Replay</span><Play className="w-3.5 h-3.5 ml-1 fill-current" /></>
                        ) : (
                          <>
                            <Play className="w-3.5 h-3.5 fill-current" />
                            <span>Start</span>
                          </>
                        )}
                      </button>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      )}

      {/* VIEW B: LEADERBOARD VIEW */}
      {activeTab === 'LEADERBOARD' && (
        <div className="space-y-6">
          <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-6">
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-gray-100 dark:border-gray-700">
              <div>
                <h2 className="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                  <Trophy className="w-6 h-6 text-amber-500" />
                  <span>Student Leaderboard & Rankings</span>
                </h2>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                  See how you rank among your classmates and school peers based on total XP earned!
                </p>
              </div>

              {/* Scope Switcher Toggle */}
              <div className="flex bg-gray-100 dark:bg-gray-700/50 p-1 rounded-xl text-xs font-bold w-full sm:w-64">
                <button
                  onClick={() => setLeaderboardScope('CLASSROOM')}
                  className={`flex-1 py-2 rounded-lg transition-all ${
                    leaderboardScope === 'CLASSROOM'
                      ? 'bg-white dark:bg-gray-800 text-sky-600 dark:text-sky-400 shadow-sm font-bold'
                      : 'text-gray-500 hover:text-gray-900 dark:hover:text-gray-200'
                  }`}
                >
                  My Class (6-A)
                </button>
                <button
                  onClick={() => setLeaderboardScope('SCHOOL')}
                  className={`flex-1 py-2 rounded-lg transition-all ${
                    leaderboardScope === 'SCHOOL'
                      ? 'bg-white dark:bg-gray-800 text-sky-600 dark:text-sky-400 shadow-sm font-bold'
                      : 'text-gray-500 hover:text-gray-900 dark:hover:text-gray-200'
                  }`}
                >
                  School Wide
                </button>
              </div>
            </div>

            {/* Leaderboard Table / Cards */}
            <div className="space-y-3">
              {leaderboard.length === 0 ? (
                <div className="p-8 text-center text-gray-500 text-xs">
                  No leaderboard records found. Complete activities to earn XP!
                </div>
              ) : (
                leaderboard.map((entry) => {
                  const isCurrentStudent = entry.studentId === profile?.id;
                  return (
                    <div
                      key={entry.studentId}
                      className={`flex items-center justify-between p-4 rounded-2xl border transition-all ${
                        isCurrentStudent
                          ? 'bg-sky-50 dark:bg-sky-950/40 border-sky-300 dark:border-sky-700 shadow-sm'
                          : 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700 hover:border-gray-300'
                      }`}
                    >
                      <div className="flex items-center space-x-4">
                        <span className={`w-9 h-9 rounded-xl flex items-center justify-center font-extrabold text-sm ${
                          entry.rank === 1
                            ? 'bg-amber-400 text-white shadow-sm'
                            : entry.rank === 2
                            ? 'bg-gray-300 text-gray-800'
                            : entry.rank === 3
                            ? 'bg-amber-600 text-white'
                            : 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300'
                        }`}>
                          {entry.rank === 1 ? '🥇 1' : entry.rank === 2 ? '🥈 2' : entry.rank === 3 ? '🥉 3' : entry.rank}
                        </span>

                        <div>
                          <div className="flex items-center gap-2">
                            <h4 className="font-bold text-sm text-gray-900 dark:text-white">{entry.studentName}</h4>
                            {isCurrentStudent && (
                              <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-sky-600 text-white">
                                You
                              </span>
                            )}
                          </div>
                          <p className="text-xs text-gray-400">Class {entry.classroomName} • Level {entry.level}</p>
                        </div>
                      </div>

                      <div className="text-right">
                        <span className="text-base font-extrabold text-amber-500">{entry.xp} XP</span>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>
        </div>
      )}

      {/* VIEW C: STUDENT PROFILE VIEW (AT THE LAST TAB) */}
      {activeTab === 'PROFILE' && (
        <div className="space-y-6">
          {/* Student Profile Info Banner & Level Progress Bar */}
          <div className="bg-blue-600 text-white p-6 rounded-2xl shadow-md flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
            <div className="flex items-center gap-4">
              <div className="p-4 bg-white/10 rounded-2xl backdrop-blur-sm">
                <UserCheck className="w-10 h-10" />
              </div>
              <div>
                <h2 className="text-2xl font-bold tracking-tight">{profile?.fullName}</h2>
                <p className="text-sky-100 text-sm mt-0.5">
                  Class {profile?.classroomName} • Parent: {profile?.parentFullName}
                </p>
                <div className="flex items-center gap-3 mt-2 text-xs text-sky-200">
                  <span className="bg-white/20 px-2.5 py-0.5 rounded-full font-semibold">
                    Classroom 6-A
                  </span>
                  <span>EduQuest Student</span>
                </div>
              </div>
            </div>

            <div className="w-full md:w-80">
              <LevelProgressBar level={currentLevel} xp={currentXp} levelProgress={gamification?.levelProgress} />
            </div>
          </div>

          {/* Interactive 6-Stage Learning Journey Map */}
          <JourneyMap stages={journeyStages} />

          {/* Daily Missions Widget */}
          <DailyMissionsWidget missions={dailyMissions} onRewardClaimed={loadGamificationData} />

          {/* Badges & Achievements Gallery */}
          <BadgeGallery unlockedBadges={gamification?.badges || []} />
        </div>
      )}

      {/* 4. MODALS (SHARED ACROSS ALL VIEWS) */}

      {/* Interactive 4-Option Quiz Runner Modal */}
      {activeQuiz && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-2xl w-full p-6 shadow-xl border border-gray-200 dark:border-gray-700 space-y-6 max-h-[85vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-gray-100 dark:border-gray-700">
              <div className="flex items-center space-x-2">
                <HelpCircle className="w-5 h-5 text-purple-600" />
                <h3 className="text-lg font-bold text-gray-900 dark:text-white">{activeQuiz.activity.title}</h3>
              </div>
              <button onClick={() => setActiveQuiz(null)} className="p-1 text-gray-400 hover:text-gray-600 rounded-lg">
                <X className="w-5 h-5" />
              </button>
            </div>

            {quizResult ? (
              <div className="text-center py-6 space-y-4">
                <div className={`w-16 h-16 mx-auto rounded-full flex items-center justify-center ${
                  quizResult.passed ? 'bg-emerald-100 text-emerald-600' : 'bg-amber-100 text-amber-600'
                }`}>
                  {quizResult.passed ? <CheckCircle className="w-10 h-10" /> : <HelpCircle className="w-10 h-10" />}
                </div>

                <div>
                  <h4 className="text-xl font-bold text-gray-900 dark:text-white">
                    {quizResult.passed ? 'Quiz Passed!' : 'Try Again! (Score < 50%)'}
                  </h4>
                  <p className="text-sm text-gray-500 mt-1">
                    Score: <span className="font-bold text-sky-600">{quizResult.score}%</span> ({quizResult.correct} / {quizResult.total} correct)
                  </p>
                </div>

                {quizResult.passed ? (
                  <p className="text-xs text-emerald-600 dark:text-emerald-400 font-bold">
                    🎉 Earned +{activeQuiz.activity.xpReward || 20} XP! Next step in module unlocked.
                  </p>
                ) : (
                  <p className="text-xs text-amber-600 dark:text-amber-400">
                    A minimum score of 50% is required to complete the quiz and earn XP.
                  </p>
                )}

                <div className="pt-4 flex justify-center space-x-3">
                  <button
                    onClick={() => { setQuizResult(null); setQuizAnswers({}); }}
                    className="px-4 py-2 text-xs font-bold text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-xl"
                  >
                    Re-take Quiz
                  </button>
                  <button
                    onClick={() => setActiveQuiz(null)}
                    className="px-5 py-2 text-xs font-bold text-white bg-sky-600 hover:bg-sky-700 rounded-xl"
                  >
                    Close
                  </button>
                </div>
              </div>
            ) : (
              <div className="space-y-6">
                {activeQuiz.questions.map((q, idx) => (
                  <div key={q.id} className="space-y-2 border-b border-gray-100 dark:border-gray-700 pb-4">
                    <p className="text-sm font-bold text-gray-900 dark:text-white">
                      Q{idx + 1}. {q.questionText}
                    </p>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
                      {['A', 'B', 'C', 'D'].map((optKey) => {
                        const optText = optKey === 'A' ? q.optionA : optKey === 'B' ? q.optionB : optKey === 'C' ? q.optionC : q.optionD;
                        const isSelected = quizAnswers[q.id!] === optKey;

                        return (
                          <button
                            key={optKey}
                            type="button"
                            onClick={() => setQuizAnswers((prev) => ({ ...prev, [q.id!]: optKey }))}
                            className={`p-3 rounded-xl text-left border transition-all ${
                              isSelected
                                ? 'bg-purple-100 text-purple-900 border-purple-500 font-bold dark:bg-purple-900/40 dark:text-purple-200'
                                : 'bg-gray-50 dark:bg-gray-800/60 text-gray-700 dark:text-gray-300 border-gray-200 dark:border-gray-700 hover:border-purple-300'
                            }`}
                          >
                            <span className="font-bold mr-1.5">{optKey}.</span> {optText}
                          </button>
                        );
                      })}
                    </div>
                  </div>
                ))}

                <div className="flex items-center justify-between pt-2">
                  <span className="text-xs text-gray-400">Passing Score: 50%</span>
                  <button
                    onClick={handleSubmitQuiz}
                    disabled={submittingQuiz || Object.keys(quizAnswers).length < activeQuiz.questions.length}
                    className={`px-6 py-2.5 text-xs font-bold text-white rounded-xl shadow-sm transition-all ${
                      submittingQuiz || Object.keys(quizAnswers).length < activeQuiz.questions.length
                        ? 'bg-gray-300 cursor-not-allowed dark:bg-gray-700'
                        : 'bg-purple-600 hover:bg-purple-700'
                    }`}
                  >
                    {submittingQuiz ? 'Submitting...' : 'Submit Quiz'}
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Mini-Game Modal */}
      {activeGamePreview && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-black/70 backdrop-blur-md">
          <div className="w-full max-w-3xl sm:max-w-4xl relative my-auto max-h-[92vh] flex flex-col">
            <button
              onClick={() => {
                setActiveGamePreview(null);
                loadData();
              }}
              className="absolute -top-3 -right-3 z-20 p-2 bg-slate-800 text-white rounded-full shadow-xl border border-slate-600 hover:bg-slate-700 transition-transform hover:scale-110 cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>
            <div className="max-h-[88vh] overflow-y-auto rounded-3xl shadow-2xl">
              <MiniGamePlayer
                activityId={activeGamePreview.id}
                activityTitle={activeGamePreview.title}
                onComplete={(res) => {
                  if (activeGamePreview) {
                    handleGameCompleted(activeGamePreview.id, res);
                  }
                }}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
