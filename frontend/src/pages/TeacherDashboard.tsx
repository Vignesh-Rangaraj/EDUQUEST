import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { teacherService } from '../services/teacherService';
import { activityService } from '../services/activityService';
import { moduleService } from '../services/moduleService';
import { gamificationService } from '../services/gamificationService';
import { ChallengeEditModal } from '../components/gamification/ChallengeEditModal';
import { GameAuthoringModal } from '../components/gamification/GameAuthoringModal';
import { ClassroomAnalytics } from '../components/teacher/ClassroomAnalytics';
import {
  Teacher,
  Student,
  Activity,
  Subject,
  ActivityType,
  CreateActivityPayload,
  Module,
  DifficultyLevel,
  LessonContent,
  QuizQuestion,
  TeacherChallengeItem
} from '../types';
import {
  GraduationCap,
  Users,
  School,
  CheckCircle,
  Calendar,
  Plus,
  BookOpen,
  Send,
  Archive,
  Edit3,
  X,
  Layers,
  HelpCircle,
  AlertTriangle,
  FileText,
  Trophy,
  Trash2,
  RotateCcw,
  Award
} from 'lucide-react';

export const TeacherDashboard: React.FC = () => {
  const [searchParams] = useSearchParams();
  const tabFromUrl = searchParams.get('tab');

  const [profile, setProfile] = useState<Teacher | null>(null);
  const [students, setStudents] = useState<Student[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [modules, setModules] = useState<Module[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Tab state: 'MODULES' | 'ACTIVITIES' | 'ROSTER' | 'ANALYTICS' | 'CHALLENGES'
  const [activeTab, setActiveTab] = useState<'MODULES' | 'ACTIVITIES' | 'ROSTER' | 'ANALYTICS' | 'CHALLENGES'>('MODULES');

  useEffect(() => {
    if (tabFromUrl && ['MODULES', 'ACTIVITIES', 'ROSTER', 'ANALYTICS', 'CHALLENGES'].includes(tabFromUrl)) {
      setActiveTab(tabFromUrl as any);
    }
  }, [tabFromUrl]);

  // Challenge state
  const [challenges, setChallenges] = useState<TeacherChallengeItem[]>([]);
  const [challengeStats, setChallengeStats] = useState<any>(null);
  const [showChallengeModal, setShowChallengeModal] = useState(false);
  const [editingChallenge, setEditingChallenge] = useState<TeacherChallengeItem | null>(null);

  // Activity Modal / Form state
  const [showCreateActivityModal, setShowCreateActivityModal] = useState(false);
  const [editingActivityId, setEditingActivityId] = useState<number | null>(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [subject, setSubject] = useState<Subject>('MATHEMATICS');
  const [activityType, setActivityType] = useState<ActivityType>('LESSON');
  const [selectedModuleId, setSelectedModuleId] = useState<number | ''>('');
  const [xpReward, setXpReward] = useState<number>(10);
  const [submitting, setSubmitting] = useState(false);

  // Module Modal state
  const [showCreateModuleModal, setShowCreateModuleModal] = useState(false);
  const [moduleTitle, setModuleTitle] = useState('');
  const [moduleDesc, setModuleDesc] = useState('');
  const [moduleSubject, setModuleSubject] = useState<Subject>('MATHEMATICS');
  const [difficultyLevel, setDifficultyLevel] = useState<DifficultyLevel>('BEGINNER');
  const [estimatedMinutes, setEstimatedMinutes] = useState<number>(30);

  // Content Authoring Modal (Lesson / Quiz)
  const [showContentModal, setShowContentModal] = useState(false);
  const [contentActivity, setContentActivity] = useState<Activity | null>(null);
  const [lessonContentText, setLessonContentText] = useState('');
  const [quizQuestionText, setQuizQuestionText] = useState('');
  const [optA, setOptA] = useState('');
  const [optB, setOptB] = useState('');
  const [optC, setOptC] = useState('');
  const [optD, setOptD] = useState('');
  const [correctAns, setCorrectAns] = useState('A');
  const [explanation, setExplanation] = useState('');

  // Analytics State
  const [analytics, setAnalytics] = useState<any>(null);

  // Activity Filters State
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'PUBLISHED' | 'DRAFT' | 'ARCHIVED'>('ALL');
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'LESSON' | 'QUIZ' | 'GAME'>('ALL');
  const [subjectFilter, setSubjectFilter] = useState<string>('ALL');

  // Game Authoring Wizard Modal state
  const [showGameAuthoringModal, setShowGameAuthoringModal] = useState(false);
  const [editingActivity, setEditingActivity] = useState<Activity | null>(null);

  useEffect(() => {
    loadTeacherData();
  }, []);

  const loadTeacherData = async () => {
    setLoading(true);
    try {
      const [pData, sData, actData, modData, chalData, chalStats, analyticsData] = await Promise.all([
        teacherService.getProfile(),
        teacherService.getAssignedStudents(),
        activityService.getTeacherActivities(),
        moduleService.getTeacherModules().catch(() => []),
        gamificationService.getMyTeacherChallenges().catch(() => []),
        gamificationService.getTeacherChallengeStats().catch(() => null),
        teacherService.getAnalytics().catch(() => null)
      ]);
      setProfile(pData);
      setStudents(sData);
      setActivities(actData);
      setModules(modData);
      setChallenges(chalData);
      setChallengeStats(chalStats);
      setAnalytics(analyticsData);
    } catch (err) {
      console.error('Failed to load teacher data', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenCreateGameActivity = () => {
    setEditingActivity(null);
    setShowGameAuthoringModal(true);
  };

  const handleOpenEditGameActivity = (act: Activity) => {
    setEditingActivity(act);
    setShowGameAuthoringModal(true);
  };

  const handleSaveActivityWithGameConfig = async (
    activityPayload: any,
    jsonConfig?: string,
    publishImmediately?: boolean,
    lessonContent?: string,
    quizQuestions?: Array<{
      questionText: string;
      optionA: string;
      optionB: string;
      optionC: string;
      optionD: string;
      correctAnswer: string;
      explanation: string;
      displayOrder?: number;
    }>
  ) => {
    try {
      let savedActivity: Activity;
      if (editingActivity?.id) {
        savedActivity = await activityService.updateActivity(editingActivity.id, activityPayload);
      } else {
        savedActivity = await activityService.createActivity(activityPayload);
      }

      if (jsonConfig && savedActivity.id) {
        await moduleService.saveGameConfig(savedActivity.id, jsonConfig);
      }

      if (lessonContent?.trim() && savedActivity.id && savedActivity.activityType === 'LESSON') {
        await moduleService.saveLessonContent(savedActivity.id, lessonContent.trim(), 10);
      }

      if (quizQuestions?.length && savedActivity.id && savedActivity.activityType === 'QUIZ') {
        for (const question of quizQuestions) {
          await moduleService.saveQuizQuestion(savedActivity.id, {
            questionText: question.questionText.trim(),
            optionA: question.optionA.trim(),
            optionB: question.optionB.trim(),
            optionC: question.optionC.trim(),
            optionD: question.optionD.trim(),
            correctAnswer: question.correctAnswer,
            explanation: question.explanation.trim(),
            displayOrder: question.displayOrder
          });
        }
      }

      if (publishImmediately && savedActivity.id) {
        await activityService.publishActivity(savedActivity.id);
      }

      setSuccessMessage(`Activity "${savedActivity.title}" saved successfully!`);
      setShowGameAuthoringModal(false);
      const [updatedActs, updatedMods, updatedAnalytics] = await Promise.all([
        activityService.getTeacherActivities(),
        moduleService.getTeacherModules().catch(() => []),
        teacherService.getAnalytics().catch(() => null)
      ]);
      setActivities(updatedActs);
      setModules(updatedMods);
      setAnalytics(updatedAnalytics);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Error saving activity.');
      throw err;
    }
  };

  const handleDeleteActivity = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this activity?')) return;
    try {
      await activityService.deleteActivity(id);
      setSuccessMessage('Activity deleted successfully.');
      const [updatedActs, updatedMods, updatedAnalytics] = await Promise.all([
        activityService.getTeacherActivities(),
        moduleService.getTeacherModules().catch(() => []),
        teacherService.getAnalytics().catch(() => null)
      ]);
      setActivities(updatedActs);
      setModules(updatedMods);
      setAnalytics(updatedAnalytics);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to delete activity.');
    }
  };

  const handleOpenCreateChallenge = () => {
    setEditingChallenge(null);
    setShowChallengeModal(true);
  };

  const handleOpenEditChallenge = (c: TeacherChallengeItem) => {
    setEditingChallenge(c);
    setShowChallengeModal(true);
  };

  const handleSaveChallenge = async (payload: Partial<TeacherChallengeItem>) => {
    setErrorMessage(null);
    try {
      if (payload.id) {
        await gamificationService.updateTeacherChallenge(payload.id, payload);
        setSuccessMessage('Challenge updated successfully!');
      } else {
        await gamificationService.createTeacherChallenge(payload);
        setSuccessMessage('Challenge created successfully!');
      }
      setShowChallengeModal(false);
      const [updatedChal, updatedStats] = await Promise.all([
        gamificationService.getMyTeacherChallenges(),
        gamificationService.getTeacherChallengeStats()
      ]);
      setChallenges(updatedChal);
      setChallengeStats(updatedStats);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to save challenge');
      throw err;
    }
  };

  const handleArchiveChallengeItem = async (id: number) => {
    try {
      await gamificationService.archiveTeacherChallenge(id);
      setSuccessMessage('Challenge archived successfully!');
      const [updatedChal, updatedStats] = await Promise.all([
        gamificationService.getMyTeacherChallenges(),
        gamificationService.getTeacherChallengeStats()
      ]);
      setChallenges(updatedChal);
      setChallengeStats(updatedStats);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to archive challenge.');
    }
  };

  const handleRestoreChallengeItem = async (id: number) => {
    try {
      await gamificationService.restoreTeacherChallenge(id);
      setSuccessMessage('Challenge restored successfully!');
      const [updatedChal, updatedStats] = await Promise.all([
        gamificationService.getMyTeacherChallenges(),
        gamificationService.getTeacherChallengeStats()
      ]);
      setChallenges(updatedChal);
      setChallengeStats(updatedStats);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to restore challenge.');
    }
  };

  const handleDeleteChallengeItem = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this challenge?')) return;
    try {
      const res = await gamificationService.deleteTeacherChallenge(id);
      setSuccessMessage(res.message || 'Challenge deleted successfully!');
      const [updatedChal, updatedStats] = await Promise.all([
        gamificationService.getMyTeacherChallenges(),
        gamificationService.getTeacherChallengeStats()
      ]);
      setChallenges(updatedChal);
      setChallengeStats(updatedStats);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to delete challenge.');
    }
  };

  const handleOpenCreateModuleModal = () => {
    setModuleTitle('');
    setModuleDesc('');
    setModuleSubject('MATHEMATICS');
    setDifficultyLevel('BEGINNER');
    setEstimatedMinutes(30);
    setShowCreateModuleModal(true);
  };

  const handleCreateModule = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!moduleTitle.trim()) return;
    setSubmitting(true);
    setErrorMessage(null);
    try {
      await moduleService.createModule({
        title: moduleTitle,
        description: moduleDesc,
        subject: moduleSubject,
        difficultyLevel,
        estimatedMinutes,
        classroomId: profile?.classroomId || undefined
      });
      setSuccessMessage(`Module "${moduleTitle}" created successfully!`);
      setShowCreateModuleModal(false);
      const updatedMods = await moduleService.getTeacherModules();
      setModules(updatedMods);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to create module.');
    } finally {
      setSubmitting(false);
    }
  };

  const handlePublishModule = async (modId: number, modTitle: string) => {
    setErrorMessage(null);
    setSuccessMessage(null);
    try {
      await moduleService.publishModule(modId);
      setSuccessMessage(`Module "${modTitle}" published successfully!`);
      const updatedMods = await moduleService.getTeacherModules();
      setModules(updatedMods);
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Add a topic description or at least one activity before publishing this module.';
      setErrorMessage(msg);
    }
  };

  const handleArchiveModule = async (modId: number) => {
    try {
      await moduleService.archiveModule(modId);
      const updatedMods = await moduleService.getTeacherModules();
      setModules(updatedMods);
    } catch (err: any) {
      setErrorMessage('Failed to archive module.');
    }
  };

  const handleOpenCreateActivityModal = () => {
    setEditingActivityId(null);
    setTitle('');
    setDescription('');
    setSubject('MATHEMATICS');
    setActivityType('LESSON');
    setSelectedModuleId(modules.length > 0 ? modules[0].id : '');
    setXpReward(10);
    setShowCreateActivityModal(true);
  };

  const handleOpenEditActivityModal = (act: Activity) => {
    setEditingActivityId(act.id);
    setTitle(act.title);
    setDescription(act.description);
    setSubject(act.subject);
    setActivityType(act.activityType);
    setSelectedModuleId(act.moduleId || '');
    setXpReward(act.xpReward || 10);
    setShowCreateActivityModal(true);
  };

  const handleSaveActivity = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    setSubmitting(true);
    setErrorMessage(null);
    try {
      const payload: CreateActivityPayload = {
        title,
        description,
        subject,
        activityType,
        assignedClassroomId: profile?.classroomId || undefined
      };

      if (editingActivityId) {
        await activityService.updateActivity(editingActivityId, payload);
      } else {
        await activityService.createActivity({
          ...payload,
          moduleId: selectedModuleId ? Number(selectedModuleId) : undefined,
          xpReward
        } as any);
      }

      setShowCreateActivityModal(false);
      const [updatedActs, updatedMods] = await Promise.all([
        activityService.getTeacherActivities(),
        moduleService.getTeacherModules().catch(() => [])
      ]);
      setActivities(updatedActs);
      setModules(updatedMods);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Error saving activity');
    } finally {
      setSubmitting(false);
    }
  };

  const handlePublishActivity = async (id: number) => {
    try {
      await activityService.publishActivity(id);
      const updatedActs = await activityService.getTeacherActivities();
      setActivities(updatedActs);
    } catch (err) {
      console.error('Error publishing activity', err);
    }
  };

  const handleArchiveActivity = async (id: number) => {
    try {
      await activityService.archiveActivity(id);
      const [updatedActs, updatedMods, updatedAnalytics] = await Promise.all([
        activityService.getTeacherActivities(),
        moduleService.getTeacherModules().catch(() => []),
        teacherService.getAnalytics().catch(() => null)
      ]);
      setActivities(updatedActs);
      setModules(updatedMods);
      setAnalytics(updatedAnalytics);
    } catch (err) {
      console.error('Error archiving activity', err);
    }
  };

  const handleRestoreActivity = async (id: number) => {
    try {
      await activityService.restoreActivity(id);
      setSuccessMessage('Activity restored to PUBLISHED status.');
      const [updatedActs, updatedMods, updatedAnalytics] = await Promise.all([
        activityService.getTeacherActivities(),
        moduleService.getTeacherModules().catch(() => []),
        teacherService.getAnalytics().catch(() => null)
      ]);
      setActivities(updatedActs);
      setModules(updatedMods);
      setAnalytics(updatedAnalytics);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to restore activity.');
    }
  };

  const handleOpenAddContent = async (act: Activity) => {
    setContentActivity(act);
    setLessonContentText('');
    setQuizQuestionText('');
    setOptA('');
    setOptB('');
    setOptC('');
    setOptD('');
    setCorrectAns('A');
    setExplanation('');

    if (act.activityType === 'LESSON') {
      try {
        const lc = await moduleService.getLessonContent(act.id);
        if (lc && lc.content) setLessonContentText(lc.content);
      } catch (err) {
        // no content yet
      }
    }
    setShowContentModal(true);
  };

  const handleSaveContent = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!contentActivity) return;
    setSubmitting(true);
    setErrorMessage(null);
    try {
      if (contentActivity.activityType === 'LESSON') {
        await moduleService.saveLessonContent(contentActivity.id, lessonContentText, 10);
        setSuccessMessage(`Lesson content saved for "${contentActivity.title}"!`);
      } else if (contentActivity.activityType === 'QUIZ') {
        await moduleService.saveQuizQuestion(contentActivity.id, {
          questionText: quizQuestionText,
          optionA: optA,
          optionB: optB,
          optionC: optC,
          optionD: optD,
          correctAnswer: correctAns,
          explanation
        });
        setSuccessMessage(`Quiz question added to "${contentActivity.title}"!`);
      }
      setShowContentModal(false);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to save content.');
    } finally {
      setSubmitting(false);
    }
  };

  const filteredActivities = activities.filter((act) => {
    if (statusFilter !== 'ALL' && act.status !== statusFilter) return false;
    if (typeFilter !== 'ALL') {
      if (typeFilter === 'GAME' && (act.activityType === 'LESSON' || act.activityType === 'QUIZ')) return false;
      if (typeFilter !== 'GAME' && act.activityType !== typeFilter) return false;
    }
    if (subjectFilter !== 'ALL' && act.subject !== subjectFilter) return false;
    return true;
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-emerald-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Teacher Welcome Banner */}
      <div className="bg-gradient-to-r from-emerald-600 to-teal-800 text-white p-6 rounded-2xl shadow-md">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-white/10 rounded-xl backdrop-blur-sm">
              <GraduationCap className="w-8 h-8" />
            </div>
            <div>
              <h1 className="text-2xl font-bold tracking-tight">Welcome, {profile?.fullName}!</h1>
              <p className="text-emerald-100 text-sm mt-0.5">EduQuest Demo School • Teacher Portal</p>
            </div>
          </div>

          <div className="bg-white/10 backdrop-blur-md px-4 py-2 rounded-xl border border-white/20">
            <p className="text-xs text-emerald-100">Assigned Classroom</p>
            <p className="text-lg font-bold">Class {profile?.classroomName || 'Unassigned'}</p>
          </div>
        </div>
      </div>

      {/* Global Alerts */}
      {errorMessage && (
        <div className="p-4 rounded-xl text-sm flex items-center justify-between gap-2 bg-red-50 dark:bg-red-950/40 text-red-700 dark:text-red-300 border border-red-200 dark:border-red-800">
          <div className="flex items-center gap-2">
            <AlertTriangle className="w-5 h-5 flex-shrink-0" />
            <span>{errorMessage}</span>
          </div>
          <button onClick={() => setErrorMessage(null)} className="text-red-500 hover:text-red-700">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {successMessage && (
        <div className="p-4 rounded-xl text-sm flex items-center justify-between gap-2 bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800">
          <div className="flex items-center gap-2">
            <CheckCircle className="w-5 h-5 flex-shrink-0" />
            <span>{successMessage}</span>
          </div>
          <button onClick={() => setSuccessMessage(null)} className="text-emerald-500 hover:text-emerald-700">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Classroom Stats & Analytics Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-sky-100 dark:bg-sky-950 text-sky-600 dark:text-sky-400 rounded-xl">
            <Users className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-semibold text-gray-500 uppercase">Enrolled Students</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">{students.length}</p>
            <p className="text-[10px] text-gray-400">Top: {analytics?.highestPerformingStudentName || 'N/A'}</p>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-purple-100 dark:bg-purple-950 text-purple-600 dark:text-purple-400 rounded-xl">
            <Layers className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-semibold text-gray-500 uppercase">Published / Drafts</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-white">
              {analytics?.totalPublishedActivities ?? activities.filter(a => a.status === 'PUBLISHED').length} Pub
            </p>
            <p className="text-[10px] text-amber-600 font-semibold">{analytics?.totalDraftActivities ?? activities.filter(a => a.status === 'DRAFT').length} Drafts</p>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-emerald-100 dark:bg-emerald-950 text-emerald-600 dark:text-emerald-400 rounded-xl">
            <BookOpen className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-semibold text-gray-500 uppercase">Games / Content</p>
            <p className="text-2xl font-bold text-emerald-600 dark:text-emerald-400">
              {analytics?.totalGamesCount ?? activities.filter(a => a.activityType !== 'LESSON' && a.activityType !== 'QUIZ').length} Games
            </p>
            <p className="text-[10px] text-gray-500 font-semibold">
              {analytics?.totalLessonsCount ?? 0} Lessons • {analytics?.totalQuizzesCount ?? 0} Quizzes
            </p>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-amber-100 dark:bg-amber-950 text-amber-600 dark:text-amber-400 rounded-xl">
            <Award className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-semibold text-gray-500 uppercase">Attempts & Rate</p>
            <p className="text-2xl font-bold text-amber-600 dark:text-amber-400">
              {analytics?.averageCompletionRate ?? 0}%
            </p>
            <p className="text-[10px] text-gray-500 font-semibold">Total Attempts: {analytics?.totalAttempts ?? 0}</p>
          </div>
        </div>
      </div>

      {/* TAB 1: MODULES MANAGEMENT */}
      {activeTab === 'MODULES' && (
        <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <Layers className="w-5 h-5 text-emerald-600" />
              General Education Authoring
            </h2>
            <button
              onClick={handleOpenCreateModuleModal}
              className="flex items-center space-x-1.5 px-4 py-2 bg-emerald-600 text-white rounded-xl text-xs font-bold hover:bg-emerald-700 transition-colors shadow-sm"
            >
              <Plus className="w-4 h-4" />
              <span>Create Module</span>
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-700 text-xs font-semibold text-gray-500 uppercase">
                  <th className="py-3 px-4">Module Title</th>
                  <th className="py-3 px-4">Subject</th>
                  <th className="py-3 px-4">Difficulty</th>
                  <th className="py-3 px-4">Duration</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-700/50 text-sm">
                {modules.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="py-12 text-center">
                      <div className="flex flex-col items-center justify-center space-y-3">
                        <div className="p-3 bg-emerald-50 dark:bg-emerald-950/40 text-emerald-600 rounded-full">
                          <Layers className="w-8 h-8" />
                        </div>
                        <p className="text-sm font-bold text-gray-700 dark:text-gray-300">
                          No curriculum modules created yet.
                        </p>
                        <p className="text-xs text-gray-500 max-w-sm">
                          Start structuring your learning content by creating your first curriculum module.
                        </p>
                        <button
                          onClick={handleOpenCreateModuleModal}
                          className="mt-2 inline-flex items-center gap-1.5 px-4 py-2 bg-emerald-600 text-white rounded-xl text-xs font-bold hover:bg-emerald-700 transition-colors shadow-sm"
                        >
                          <Plus className="w-4 h-4" />
                          <span>Create Module</span>
                        </button>
                      </div>
                    </td>
                  </tr>
                ) : (
                  modules.map((m) => (
                    <tr key={m.id} className="hover:bg-gray-50 dark:hover:bg-gray-700/50">
                      <td className="py-3.5 px-4 font-semibold text-gray-900 dark:text-white">
                        <div>{m.title}</div>
                        <div className="text-xs text-gray-400 font-normal line-clamp-1">{m.description}</div>
                      </td>
                      <td className="py-3.5 px-4">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-sky-100 text-sky-800 dark:bg-sky-900/40 dark:text-sky-300">
                          {m.subject}
                        </span>
                      </td>
                      <td className="py-3.5 px-4">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300">
                          {m.difficultyLevel}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-xs text-gray-500">
                        {m.estimatedMinutes || 30} mins
                      </td>
                      <td className="py-3.5 px-4">
                        <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold ${
                          m.status === 'PUBLISHED'
                            ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300'
                            : m.status === 'ARCHIVED'
                            ? 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400'
                            : 'bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300'
                        }`}>
                          {m.status}
                        </span>
                      </td>
                      <td className="py-3.5 px-4">
                        <div className="flex items-center space-x-2">
                          {m.status !== 'PUBLISHED' && (
                            <button
                              onClick={() => handlePublishModule(m.id, m.title)}
                              className="flex items-center gap-1 px-3 py-1 bg-emerald-50 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300 hover:bg-emerald-100 rounded-lg text-xs font-bold transition-colors"
                              title="Publish this topic and its linked practice activities"
                            >
                              <Send className="w-3.5 h-3.5" />
                              <span>Publish</span>
                            </button>
                          )}
                          {m.status !== 'ARCHIVED' && (
                            <button
                              onClick={() => handleArchiveModule(m.id)}
                              className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950 rounded-lg"
                              title="Archive Module"
                            >
                              <Archive className="w-4 h-4" />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 2: ACTIVITIES MANAGEMENT */}
      {activeTab === 'ACTIVITIES' && (
        <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <BookOpen className="w-5 h-5 text-emerald-600" />
              My Activity Management
            </h2>
            <button
              onClick={handleOpenCreateGameActivity}
              className="flex items-center space-x-1.5 px-4 py-2 bg-emerald-600 text-white rounded-xl text-xs font-bold hover:bg-emerald-700 transition-colors shadow-sm"
            >
              <Plus className="w-4 h-4" />
              <span>Create Activity / Game</span>
            </button>
          </div>

          {/* Filters Bar */}
          <div className="flex flex-wrap items-center gap-3 p-3 bg-gray-50 dark:bg-gray-700/50 rounded-xl border border-gray-100 dark:border-gray-700">
            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-gray-500 uppercase">Status:</span>
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value as any)}
                className="text-xs font-semibold px-2.5 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200"
              >
                <option value="ALL">All Statuses</option>
                <option value="PUBLISHED">Published</option>
                <option value="DRAFT">Draft</option>
                <option value="ARCHIVED">Archived</option>
              </select>
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-gray-500 uppercase">Type:</span>
              <select
                value={typeFilter}
                onChange={(e) => setTypeFilter(e.target.value as any)}
                className="text-xs font-semibold px-2.5 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200"
              >
                <option value="ALL">All Types</option>
                <option value="LESSON">Lesson</option>
                <option value="QUIZ">Quiz</option>
                <option value="GAME">Game</option>
              </select>
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-gray-500 uppercase">Subject:</span>
              <select
                value={subjectFilter}
                onChange={(e) => setSubjectFilter(e.target.value)}
                className="text-xs font-semibold px-2.5 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-200"
              >
                <option value="ALL">All Subjects</option>
                <option value="MATHEMATICS">Mathematics</option>
                <option value="SCIENCE">Science</option>
                <option value="ENGLISH">English</option>
                <option value="SOCIAL_SCIENCE">Social Science</option>
              </select>
            </div>

            <div className="ml-auto text-xs text-gray-500 font-semibold">
              Showing {filteredActivities.length} of {activities.length} activities
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-700 text-xs font-semibold text-gray-500 uppercase">
                  <th className="py-3 px-4">Title</th>
                  <th className="py-3 px-4">Subject</th>
                  <th className="py-3 px-4">Type</th>
                  <th className="py-3 px-4">XP Reward</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Attempts</th>
                  <th className="py-3 px-4">Completion %</th>
                  <th className="py-3 px-4">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-700/50 text-sm">
                {filteredActivities.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="py-12 text-center">
                      <div className="flex flex-col items-center justify-center space-y-3">
                        <div className="p-3 bg-purple-50 dark:bg-purple-950/40 text-purple-600 rounded-full">
                          <BookOpen className="w-8 h-8" />
                        </div>
                        <p className="text-sm font-bold text-gray-700 dark:text-gray-300">
                          No activities created yet.
                        </p>
                        <p className="text-xs text-gray-500 max-w-sm">
                          Author lessons, quizzes, or interactive mini-games for your students.
                        </p>
                        <button
                          onClick={handleOpenCreateGameActivity}
                          className="mt-2 inline-flex items-center gap-1.5 px-4 py-2 bg-emerald-600 text-white rounded-xl text-xs font-bold hover:bg-emerald-700 transition-colors shadow-sm"
                        >
                          <Plus className="w-4 h-4" />
                          <span>Create Activity / Game</span>
                        </button>
                      </div>
                    </td>
                  </tr>
                ) : (
                  filteredActivities.map((act) => (
                    <tr key={act.id} className="hover:bg-gray-50 dark:hover:bg-gray-700/50">
                      <td className="py-3.5 px-4 font-semibold text-gray-900 dark:text-white">
                        <div>{act.title}</div>
                        <div className="text-xs text-gray-400 font-normal line-clamp-1">{act.description}</div>
                      </td>
                      <td className="py-3.5 px-4">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-sky-100 text-sky-800 dark:bg-sky-900/40 dark:text-sky-300">
                          {act.subject}
                        </span>
                      </td>
                      <td className="py-3.5 px-4">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800 dark:bg-purple-900/40 dark:text-purple-300">
                          {act.activityType}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 font-bold text-amber-600 dark:text-amber-400">
                        +{act.xpReward || 10} XP
                      </td>
                      <td className="py-3.5 px-4">
                        <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold ${
                          act.status === 'PUBLISHED'
                            ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300'
                            : act.status === 'ARCHIVED'
                            ? 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400'
                            : 'bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300'
                        }`}>
                          {act.status}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-xs font-medium text-gray-600 dark:text-gray-300">
                        {act.attemptsCount ?? 0}
                      </td>
                      <td className="py-3.5 px-4 text-xs font-medium text-emerald-600 dark:text-emerald-400">
                        {act.completionRate ?? (act.completed ? 100 : 0)}%
                      </td>
                      <td className="py-3.5 px-4">
                        <div className="flex items-center space-x-2">
                          <button
                            onClick={() => handleOpenAddContent(act)}
                            className="p-1.5 text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-950 rounded-lg flex items-center gap-1 text-xs font-semibold"
                            title="Add/Edit Content (Lesson HTML or Quiz Questions)"
                          >
                            <FileText className="w-4 h-4" />
                            <span>Content</span>
                          </button>
                          <button
                            onClick={() => handleOpenEditGameActivity(act)}
                            className="p-1.5 text-gray-500 hover:text-sky-600 hover:bg-sky-50 dark:hover:bg-gray-700 rounded-lg"
                            title="Edit Activity & Game Config"
                          >
                            <Edit3 className="w-4 h-4" />
                          </button>
                          {act.status !== 'PUBLISHED' && (
                            <button
                              onClick={() => handlePublishActivity(act.id)}
                              className="p-1.5 text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-950 rounded-lg"
                              title="Publish Activity"
                            >
                              <Send className="w-4 h-4" />
                            </button>
                          )}
                          {act.status === 'ARCHIVED' ? (
                            <button
                              onClick={() => handleRestoreActivity(act.id)}
                              className="p-1.5 text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-950 rounded-lg"
                              title="Restore Activity"
                            >
                              <RotateCcw className="w-4 h-4" />
                            </button>
                          ) : (
                            <button
                              onClick={() => handleArchiveActivity(act.id)}
                              className="p-1.5 text-gray-400 hover:text-amber-600 hover:bg-amber-50 dark:hover:bg-amber-950 rounded-lg"
                              title="Archive Activity"
                            >
                              <Archive className="w-4 h-4" />
                            </button>
                          )}
                          <button
                            onClick={() => handleDeleteActivity(act.id)}
                            className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950 rounded-lg"
                            title="Delete Activity"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: STUDENT ROSTER TABLE */}
      {activeTab === 'ROSTER' && (
        <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <Users className="w-5 h-5 text-emerald-600" />
              Classroom Enrollment List ({profile?.classroomName})
            </h2>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-700 text-xs font-semibold text-gray-500 uppercase">
                  <th className="py-3 px-4">Student ID</th>
                  <th className="py-3 px-4">Full Name</th>
                  <th className="py-3 px-4">Username</th>
                  <th className="py-3 px-4">Level & XP</th>
                  <th className="py-3 px-4">Parent / Guardian</th>
                  <th className="py-3 px-4">Enrolled Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-700/50 text-sm">
                {students.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="py-8 text-center text-gray-400">
                      No students currently assigned to your classroom.
                    </td>
                  </tr>
                ) : (
                  students.map((s) => (
                    <tr key={s.id} className="hover:bg-gray-50 dark:hover:bg-gray-700/50">
                      <td className="py-3.5 px-4 font-mono text-xs text-gray-500">#{s.id}</td>
                      <td className="py-3.5 px-4 font-semibold text-gray-900 dark:text-white">{s.fullName}</td>
                      <td className="py-3.5 px-4 text-gray-500">{s.username}</td>
                      <td className="py-3.5 px-4">
                        <span className="px-2 py-0.5 text-xs font-bold bg-amber-100 dark:bg-amber-900/40 text-amber-800 dark:text-amber-300 rounded-full">
                          Lvl {s.level || 1} • {s.xp || 0} XP
                        </span>
                      </td>
                      <td className="py-3.5 px-4 font-medium text-emerald-600 dark:text-emerald-400">
                        {s.parentFullName || 'Unlinked'}
                      </td>
                      <td className="py-3.5 px-4 text-xs text-gray-400 flex items-center gap-1">
                        <Calendar className="w-3.5 h-3.5" />
                        {new Date(s.createdAt).toLocaleDateString()}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'ANALYTICS' && <ClassroomAnalytics />}

      {/* TAB 5: CLASSROOM CHALLENGES MANAGEMENT */}
      {activeTab === 'CHALLENGES' && (
        <div className="space-y-6">
          {/* Challenge Stats */}
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            <div className="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-3">
              <div className="p-2.5 bg-amber-100 dark:bg-amber-950 text-amber-600 rounded-lg">
                <Trophy className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs font-medium text-gray-500 uppercase">Total Challenges</p>
                <p className="text-xl font-bold text-gray-900 dark:text-white">{challengeStats?.totalChallenges ?? challenges.length}</p>
              </div>
            </div>

            <div className="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-3">
              <div className="p-2.5 bg-emerald-100 dark:bg-emerald-950 text-emerald-600 rounded-lg">
                <CheckCircle className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs font-medium text-gray-500 uppercase">Active Challenges</p>
                <p className="text-xl font-bold text-emerald-600">{challengeStats?.activeChallenges ?? challenges.filter(c => !c.archived).length}</p>
              </div>
            </div>

            <div className="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-3">
              <div className="p-2.5 bg-gray-100 dark:bg-gray-700 text-gray-600 rounded-lg">
                <Archive className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs font-medium text-gray-500 uppercase">Archived</p>
                <p className="text-xl font-bold text-gray-700 dark:text-gray-300">{challengeStats?.archivedChallenges ?? challenges.filter(c => c.archived).length}</p>
              </div>
            </div>

            <div className="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-3">
              <div className="p-2.5 bg-indigo-100 dark:bg-indigo-950 text-indigo-600 rounded-lg">
                <Award className="w-5 h-5" />
              </div>
              <div>
                <p className="text-xs font-medium text-gray-500 uppercase">Completed Entries</p>
                <p className="text-xl font-bold text-indigo-600">{challengeStats?.completedCount ?? 0}</p>
              </div>
            </div>
          </div>

          <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <Trophy className="w-5 h-5 text-amber-500" />
                Classroom Gamification Challenges
              </h2>
              <button
                onClick={handleOpenCreateChallenge}
                className="flex items-center space-x-1.5 px-4 py-2 bg-amber-500 hover:bg-amber-600 text-white rounded-xl text-xs font-bold transition-colors shadow-sm"
              >
                <Plus className="w-4 h-4" />
                <span>Create Challenge</span>
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-gray-200 dark:border-gray-700 text-xs font-semibold text-gray-500 uppercase">
                    <th className="py-3 px-4">Challenge Name</th>
                    <th className="py-3 px-4">Target Type & Goal</th>
                    <th className="py-3 px-4">Rewards</th>
                    <th className="py-3 px-4">Duration</th>
                    <th className="py-3 px-4">Status</th>
                    <th className="py-3 px-4">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 dark:divide-gray-700/50 text-sm">
                  {challenges.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="py-12 text-center">
                        <div className="flex flex-col items-center justify-center space-y-3">
                          <div className="p-3 bg-amber-50 dark:bg-amber-950/40 text-amber-600 rounded-full">
                            <Trophy className="w-8 h-8" />
                          </div>
                          <p className="text-sm font-bold text-gray-700 dark:text-gray-300">
                            No classroom challenges available.
                          </p>
                          <p className="text-xs text-gray-500 max-w-sm">
                            Boost student engagement by creating custom classroom challenges with rewards.
                          </p>
                          <button
                            onClick={handleOpenCreateChallenge}
                            className="mt-2 inline-flex items-center gap-1.5 px-4 py-2 bg-amber-500 hover:bg-amber-600 text-white rounded-xl text-xs font-bold transition-colors shadow-sm"
                          >
                            <Plus className="w-4 h-4" />
                            <span>Create Challenge</span>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ) : (
                    challenges.map((c) => (
                      <tr key={c.id} className="hover:bg-gray-50 dark:hover:bg-gray-700/50">
                        <td className="py-3.5 px-4 font-semibold text-gray-900 dark:text-white">
                          <div>{c.title}</div>
                          <div className="text-xs text-gray-500 font-normal">{c.description}</div>
                        </td>
                        <td className="py-3.5 px-4 text-xs font-mono">
                          <span className="px-2 py-1 rounded-md bg-blue-50 dark:bg-blue-950 text-blue-700 dark:text-blue-300 font-semibold">
                            {c.targetType}: {c.targetValue}
                          </span>
                        </td>
                        <td className="py-3.5 px-4 text-xs">
                          <span className="font-bold text-amber-600">{c.xpReward} XP</span>
                          <span className="mx-1 text-gray-400">•</span>
                          <span className="font-bold text-yellow-600">{c.coinReward} Coins</span>
                          {c.badgeRewardCode && (
                            <span className="ml-1 px-1.5 py-0.5 rounded bg-purple-100 dark:bg-purple-900/40 text-purple-700 dark:text-purple-300 font-semibold text-[10px]">
                              🏷️ {c.badgeRewardCode}
                            </span>
                          )}
                        </td>
                        <td className="py-3.5 px-4 text-xs text-gray-500">
                          {c.startDate} to {c.endDate}
                        </td>
                        <td className="py-3.5 px-4">
                          {c.archived ? (
                            <span className="px-2 py-0.5 text-xs font-bold bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300 rounded-full">
                              Archived
                            </span>
                          ) : (
                            <span className="px-2 py-0.5 text-xs font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 rounded-full">
                              Active
                            </span>
                          )}
                        </td>
                        <td className="py-3.5 px-4">
                          <div className="flex items-center space-x-2">
                            <button
                              onClick={() => handleOpenEditChallenge(c)}
                              className="p-1.5 text-gray-500 hover:text-amber-600 hover:bg-amber-50 dark:hover:bg-gray-700 rounded-lg"
                              title="Edit Challenge"
                            >
                              <Edit3 className="w-4 h-4" />
                            </button>
                            {c.archived ? (
                              <button
                                onClick={() => handleRestoreChallengeItem(c.id)}
                                className="p-1.5 text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-950 rounded-lg"
                                title="Restore Challenge"
                              >
                                <RotateCcw className="w-4 h-4" />
                              </button>
                            ) : (
                              <button
                                onClick={() => handleArchiveChallengeItem(c.id)}
                                className="p-1.5 text-gray-400 hover:text-amber-600 hover:bg-amber-50 dark:hover:bg-amber-950 rounded-lg"
                                title="Archive Challenge"
                              >
                                <Archive className="w-4 h-4" />
                              </button>
                            )}
                            <button
                              onClick={() => handleDeleteChallengeItem(c.id)}
                              className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950 rounded-lg"
                              title="Delete Challenge"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* CREATE MODULE MODAL */}
      {showCreateModuleModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-lg w-full p-6 shadow-xl border border-gray-200 dark:border-gray-700 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-gray-100 dark:border-gray-700">
              <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <Layers className="w-5 h-5 text-emerald-600" />
                Create Curriculum Module
              </h3>
              <button
                onClick={() => setShowCreateModuleModal(false)}
                className="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateModule} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                  Topic Title
                </label>
                <input
                  type="text"
                  required
                  value={moduleTitle}
                  onChange={(e) => setModuleTitle(e.target.value)}
                  placeholder="e.g. Energy"
                  className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                  Topic Summary / Reading Overview
                </label>
                <textarea
                  rows={3}
                  value={moduleDesc}
                  onChange={(e) => setModuleDesc(e.target.value)}
                  placeholder="Introduce the topic for students to read before trying its practice activities..."
                  className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                    Subject
                  </label>
                  <select
                    value={moduleSubject}
                    onChange={(e) => setModuleSubject(e.target.value as Subject)}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  >
                    <option value="MATHEMATICS">MATHEMATICS</option>
                    <option value="SCIENCE">SCIENCE</option>
                    <option value="ENGLISH">ENGLISH</option>
                    <option value="SOCIAL_SCIENCE">SOCIAL_SCIENCE</option>
                    <option value="TAMIL">TAMIL</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                    Difficulty
                  </label>
                  <select
                    value={difficultyLevel}
                    onChange={(e) => setDifficultyLevel(e.target.value as DifficultyLevel)}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  >
                    <option value="BEGINNER">BEGINNER</option>
                    <option value="INTERMEDIATE">INTERMEDIATE</option>
                    <option value="ADVANCED">ADVANCED</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                    Duration (Mins)
                  </label>
                  <input
                    type="number"
                    min={5}
                    max={300}
                    value={estimatedMinutes}
                    onChange={(e) => setEstimatedMinutes(Number(e.target.value))}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end space-x-3 pt-4 border-t border-gray-100 dark:border-gray-700">
                <button
                  type="button"
                  onClick={() => setShowCreateModuleModal(false)}
                  className="px-4 py-2 text-xs font-semibold text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-xl transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-5 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl transition-colors shadow-sm"
                >
                  {submitting ? 'Creating...' : 'Create Module'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CREATE / EDIT ACTIVITY MODAL */}
      {showCreateActivityModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-black/60 backdrop-blur-sm">
          <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-gray-200 dark:border-gray-700 flex flex-col max-h-[90vh]">
            <div className="flex items-center justify-between pb-3 border-b border-gray-100 dark:border-gray-700 flex-shrink-0">
              <h3 className="text-lg font-bold text-gray-900 dark:text-white">
                {editingActivityId ? 'Edit Activity' : 'Create New Activity'}
              </h3>
              <button
                onClick={() => setShowCreateActivityModal(false)}
                className="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveActivity} className="flex-1 flex flex-col min-h-0 pt-3">
              <div className="flex-1 overflow-y-auto space-y-4 pr-1 scrollbar-thin">
                <div>
                  <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                    Title
                  </label>
                  <input
                    type="text"
                    required
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="e.g. Fractions Basics"
                    className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                    Description
                  </label>
                  <textarea
                    rows={3}
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    placeholder="Activity overview and learning goals..."
                    className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                {modules.length > 0 && (
                  <div>
                    <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                      Attach to Topic Module (optional)
                    </label>
                    <select
                      value={selectedModuleId}
                      onChange={(e) => setSelectedModuleId(e.target.value ? Number(e.target.value) : '')}
                      className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    >
                      <option value="">Unassigned</option>
                      {modules.map((m) => (
                        <option key={m.id} value={m.id}>
                          {m.title} ({m.subject})
                        </option>
                      ))}
                    </select>
                  </div>
                )}

                <div className="grid grid-cols-3 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                      Subject
                    </label>
                    <select
                      value={subject}
                      onChange={(e) => setSubject(e.target.value as Subject)}
                      className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    >
                      <option value="MATHEMATICS">MATHEMATICS</option>
                      <option value="SCIENCE">SCIENCE</option>
                      <option value="ENGLISH">ENGLISH</option>
                      <option value="SOCIAL_SCIENCE">SOCIAL_SCIENCE</option>
                      <option value="TAMIL">TAMIL</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                      Activity Type
                    </label>
                    <select
                      value={activityType}
                      onChange={(e) => setActivityType(e.target.value as ActivityType)}
                      className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    >
                      <option value="LESSON">LESSON</option>
                      <option value="QUIZ">QUIZ</option>
                      <option value="MATCH_THE_FOLLOWING">MATCH THE FOLLOWING (GAME)</option>
                      <option value="SHOOT_THE_ANSWER">SHOOT THE ANSWER (GAME)</option>
                      <option value="BALLOON_POP">BALLOON POP (GAME)</option>
                      <option value="TREASURE_HUNT">TREASURE HUNT (GAME)</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                      XP Reward
                    </label>
                    <input
                      type="number"
                      min={5}
                      max={100}
                      value={xpReward}
                      onChange={(e) => setXpReward(Number(e.target.value))}
                      className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-xs focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    />
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-end space-x-3 pt-4 border-t border-gray-100 dark:border-gray-700 flex-shrink-0 mt-2">
                <button
                  type="button"
                  onClick={() => setShowCreateActivityModal(false)}
                  className="px-4 py-2 text-xs font-semibold text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-xl transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-5 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl transition-colors shadow-sm"
                >
                  {submitting ? 'Saving...' : editingActivityId ? 'Update Activity' : 'Create Activity'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CONTENT AUTHORING MODAL (LESSON OR QUIZ QUESTION) */}
      {showContentModal && contentActivity && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-xl w-full p-6 shadow-xl border border-gray-200 dark:border-gray-700 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-gray-100 dark:border-gray-700">
              <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <FileText className="w-5 h-5 text-emerald-600" />
                Author Content: {contentActivity.title} ({contentActivity.activityType})
              </h3>
              <button
                onClick={() => setShowContentModal(false)}
                className="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveContent} className="space-y-4">
              {contentActivity.activityType === 'LESSON' && (
                <div>
                  <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                    Lesson Explanation / Markdown Content
                  </label>
                  <textarea
                    rows={6}
                    required
                    value={lessonContentText}
                    onChange={(e) => setLessonContentText(e.target.value)}
                    placeholder="<p>Detailed educational content for this lesson...</p>"
                    className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-sm font-mono focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              )}

              {contentActivity.activityType === 'QUIZ' && (
                <div className="space-y-3">
                  <div>
                    <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                      Question Prompt
                    </label>
                    <input
                      type="text"
                      required
                      value={quizQuestionText}
                      onChange={(e) => setQuizQuestionText(e.target.value)}
                      placeholder="e.g. What is 3/4 + 2/4?"
                      className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white text-xs"
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      required
                      placeholder="Option A"
                      value={optA}
                      onChange={(e) => setOptA(e.target.value)}
                      className="px-3 py-1.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs"
                    />
                    <input
                      type="text"
                      required
                      placeholder="Option B"
                      value={optB}
                      onChange={(e) => setOptB(e.target.value)}
                      className="px-3 py-1.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs"
                    />
                    <input
                      type="text"
                      required
                      placeholder="Option C"
                      value={optC}
                      onChange={(e) => setOptC(e.target.value)}
                      className="px-3 py-1.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs"
                    />
                    <input
                      type="text"
                      required
                      placeholder="Option D"
                      value={optD}
                      onChange={(e) => setOptD(e.target.value)}
                      className="px-3 py-1.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs"
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-2">
                    <div>
                      <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                        Correct Answer
                      </label>
                      <select
                        value={correctAns}
                        onChange={(e) => setCorrectAns(e.target.value)}
                        className="w-full px-3 py-1.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs"
                      >
                        <option value="A">Option A</option>
                        <option value="B">Option B</option>
                        <option value="C">Option C</option>
                        <option value="D">Option D</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase mb-1">
                        Explanation (Optional)
                      </label>
                      <input
                        type="text"
                        value={explanation}
                        onChange={(e) => setExplanation(e.target.value)}
                        placeholder="Why this answer is correct..."
                        className="w-full px-3 py-1.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-700 text-xs"
                      />
                    </div>
                  </div>
                </div>
              )}

              <div className="flex items-center justify-end space-x-3 pt-4 border-t border-gray-100 dark:border-gray-700">
                <button
                  type="button"
                  onClick={() => setShowContentModal(false)}
                  className="px-4 py-2 text-xs font-semibold text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-xl transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-5 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl transition-colors shadow-sm"
                >
                  {submitting ? 'Saving...' : 'Save Content'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* EDIT/CREATE CHALLENGE MODAL */}
      {showChallengeModal && (
        <ChallengeEditModal
          challenge={editingChallenge}
          classroomId={profile?.classroomId || 1}
          onSave={handleSaveChallenge}
          onClose={() => setShowChallengeModal(false)}
        />
      )}
      {/* EDIT/CREATE GAME & ACTIVITY AUTHORING WIZARD MODAL */}
      {showGameAuthoringModal && (
        <GameAuthoringModal
          activity={editingActivity}
          modules={modules}
          classroomId={profile?.classroomId || undefined}
          onSave={handleSaveActivityWithGameConfig}
          onClose={() => setShowGameAuthoringModal(false)}
        />
      )}
    </div>
  );
};
