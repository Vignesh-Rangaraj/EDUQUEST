import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { moduleService } from '../../services/moduleService';
import { studentLessonService } from '../../services/studentLessonService';
import { studentService } from '../../services/studentService';
import { offlineProgressRepository } from '../../offline/offlineProgressRepository';
import { offlineLearningService } from '../../offline/offlineLearningService';
import { LessonContentView } from '../../components/student/LessonContentView';
import { Activity, LessonContent } from '../../types';
import {
  ChevronLeft,
  ChevronRight,
  CheckCircle,
  BookOpen,
  FileText,
  Zap,
  ArrowLeft,
  Award
} from 'lucide-react';

export const StudentLessonPlayer: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [lesson, setLesson] = useState<Activity | null>(null);
  const [content, setContent] = useState<LessonContent | null>(null);
  const [moduleLessons, setModuleLessons] = useState<Activity[]>([]);
  const [loading, setLoading] = useState(true);
  const [completing, setCompleting] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [earnedXpMsg, setEarnedXpMsg] = useState<string | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);

  const lessonId = Number(id);

  useEffect(() => {
    if (!lessonId) return;
    loadLessonData();
  }, [lessonId]);

  const loadLessonData = async () => {
    setLoading(true);
    setEarnedXpMsg(null);
    setLoadError(null);
    try {
      const act = await studentLessonService.getLessonDetails(lessonId);
      setLesson(act);
      const profile = await studentService.getProfile().catch(() => null);
      const localProgress = profile ? (await offlineProgressRepository.getStudentProgress(profile.id)).find((p) => p.activityId === lessonId) : undefined;
      setIsCompleted(Boolean(act.completed || localProgress?.completed));

      const lc = await moduleService.getLessonContent(lessonId).catch(() => null);
      setContent(lc);

      if (act.moduleId) {
        const siblings = await moduleService.getModuleActivities(act.moduleId).catch(() => []);
        setModuleLessons(siblings.filter((a) => a.activityType === 'LESSON'));
      }
    } catch (err) {
      console.error('Failed to load lesson details', err);
      setLoadError('Lesson not available offline.');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkComplete = async () => {
    if (!lesson) return;
    setCompleting(true);
    try {
      const profile = await studentService.getProfile();
      const result = await offlineLearningService.recordActivityProgress({
        studentId: profile.id,
        activityId: lesson.id,
        score: 100,
        completed: true,
        xpReward: Number(lesson.xpReward || 10),
        actionType: 'COMPLETE_ACTIVITY'
      });
      setIsCompleted(true);
      setEarnedXpMsg(`🎉 Lesson Completed! Earned +${result.xpEarned} XP!`);
    } catch (err) {
      console.error('Failed to mark lesson completed', err);
    } finally {
      setCompleting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-sky-600"></div>
      </div>
    );
  }

  if (!lesson) {
    return (
      <div className="p-8 text-center space-y-4">
        <p className="text-gray-500">{loadError || 'Lesson not found.'}</p>
        <Link to="/student" className="px-4 py-2 bg-sky-600 text-white rounded-xl text-xs font-bold">
          Back to Student Portal
        </Link>
      </div>
    );
  }

  // Find previous and next lesson indices
  const currentIndex = moduleLessons.findIndex((l) => l.id === lesson.id);
  const prevLesson = currentIndex > 0 ? moduleLessons[currentIndex - 1] : null;
  const nextLesson = currentIndex >= 0 && currentIndex < moduleLessons.length - 1 ? moduleLessons[currentIndex + 1] : null;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* TOP: Header & Breadcrumbs */}
      <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-2">
        <div className="flex items-center justify-between">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center gap-1 text-xs font-bold text-gray-500 hover:text-gray-900 dark:hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Back</span>
          </button>
          <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-sky-100 text-sky-800 dark:bg-sky-950 dark:text-sky-300">
            {lesson.subject}
          </span>
        </div>

        <div className="pt-1">
          <p className="text-xs text-sky-600 dark:text-sky-400 font-bold uppercase tracking-wider">
            {lesson.moduleId ? `Module Lesson` : 'Lesson'}
          </p>
          <h1 className="text-xl font-bold text-gray-900 dark:text-white mt-0.5">{lesson.title}</h1>
        </div>
      </div>

      {earnedXpMsg && (
        <div className="p-4 rounded-xl text-xs font-bold bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Award className="w-5 h-5 text-emerald-500" />
            <span>{earnedXpMsg}</span>
          </div>
        </div>
      )}

      {/* CENTER: Lesson Content Body */}
      <div className="bg-white dark:bg-gray-800 p-6 sm:p-8 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-6 min-h-[350px]">
        <div className="max-w-none text-sm text-gray-800 dark:text-gray-200 leading-relaxed whitespace-pre-line">
          <LessonContentView content={content?.content || lesson.description || 'Lesson content is loading...'} />
        </div>
      </div>

      {/* BOTTOM: Navigation & Complete Controls */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-4 bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
        <Link to={`/ai-tutor?lessonId=${lesson.id}`} className="w-full sm:w-auto px-4 py-2.5 text-xs font-bold rounded-xl flex items-center justify-center gap-1.5 bg-indigo-50 text-indigo-700 dark:bg-indigo-950/50 dark:text-indigo-300">
          Ask AI Tutor about this lesson
        </Link>
        {/* Previous Lesson Button */}
        <button
          onClick={() => prevLesson && navigate(`/student/lesson/${prevLesson.id}`)}
          disabled={!prevLesson}
          className={`w-full sm:w-auto px-4 py-2.5 text-xs font-bold rounded-xl flex items-center justify-center gap-1.5 transition-all ${
            prevLesson
              ? 'bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:text-gray-200 text-gray-700'
              : 'bg-gray-100 dark:bg-gray-800 text-gray-300 dark:text-gray-600 cursor-not-allowed border border-gray-200 dark:border-gray-700'
          }`}
        >
          <ChevronLeft className="w-4 h-4" />
          <span>Previous Lesson</span>
        </button>

        {/* Mark Complete Button */}
        <button
          onClick={handleMarkComplete}
          disabled={completing || isCompleted}
          className={`w-full sm:w-auto px-6 py-2.5 text-xs font-bold rounded-xl flex items-center justify-center gap-2 shadow-sm transition-all ${
            isCompleted
              ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 border border-emerald-300 dark:border-emerald-700 cursor-default'
              : 'bg-sky-600 hover:bg-sky-700 text-white'
          }`}
        >
          <CheckCircle className="w-4 h-4" />
          <span>{isCompleted ? '✓ Completed' : completing ? 'Saving...' : 'Mark Complete (+10 XP)'}</span>
        </button>

        {/* Next Lesson Button */}
        <button
          onClick={() => nextLesson && navigate(`/student/lesson/${nextLesson.id}`)}
          disabled={!nextLesson}
          className={`w-full sm:w-auto px-4 py-2.5 text-xs font-bold rounded-xl flex items-center justify-center gap-1.5 transition-all ${
            nextLesson
              ? 'bg-indigo-600 hover:bg-indigo-700 text-white'
              : 'bg-gray-100 dark:bg-gray-800 text-gray-300 dark:text-gray-600 cursor-not-allowed border border-gray-200 dark:border-gray-700'
          }`}
        >
          <span>Next Lesson</span>
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
