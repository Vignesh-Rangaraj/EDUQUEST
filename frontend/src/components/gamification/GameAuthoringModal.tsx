import React, { useState, useEffect } from 'react';
import { Activity, Subject, ActivityType, Module } from '../../types';
import { moduleService } from '../../services/moduleService';
import { MiniGamePlayer } from './MiniGamePlayer';
import { X, Play, Save, Send, Plus, Trash2, HelpCircle, Layers, Gamepad2 } from 'lucide-react';

interface GameAuthoringModalProps {
  activity?: Activity | null;
  modules: Module[];
  classroomId?: number;
  onSave: (
    activityPayload: any,
    jsonConfig?: string,
    publishImmediately?: boolean,
    lessonContent?: string,
    quizQuestions?: QuizDraft[]
  ) => Promise<void>;
  onClose: () => void;
}

interface QuizDraft {
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctAnswer: string;
  explanation: string;
  displayOrder?: number;
}

const emptyQuizDraft = (): QuizDraft => ({
  questionText: '', optionA: '', optionB: '', optionC: '', optionD: '', correctAnswer: 'A', explanation: ''
});

export const GameAuthoringModal: React.FC<GameAuthoringModalProps> = ({
  activity,
  modules,
  classroomId,
  onSave,
  onClose
}) => {
  const [title, setTitle] = useState(activity?.title || '');
  const [description, setDescription] = useState(activity?.description || '');
  const [lessonContent, setLessonContent] = useState('');
  const [quizDrafts, setQuizDrafts] = useState<QuizDraft[]>([emptyQuizDraft()]);
  const [existingQuizQuestionCount, setExistingQuizQuestionCount] = useState(0);
  const [loadingExistingQuizQuestions, setLoadingExistingQuizQuestions] = useState(false);
  const [subject, setSubject] = useState<Subject>(activity?.subject || 'MATHEMATICS');
  const [activityCategory, setActivityCategory] = useState<'LESSON' | 'QUIZ' | 'GAME'>(
    activity?.activityType === 'LESSON' ? 'LESSON' : activity?.activityType === 'QUIZ' ? 'QUIZ' : 'GAME'
  );
  const [resolvedGameType, setResolvedGameType] = useState<string>(
    activity?.activityType && activity.activityType !== 'LESSON' && activity.activityType !== 'QUIZ'
      ? activity.activityType
      : 'MATCH_THE_FOLLOWING'
  );
  const [selectedModuleId, setSelectedModuleId] = useState<number | ''>(activity?.moduleId || (modules.length > 0 ? modules[0].id : ''));
  const [xpReward, setXpReward] = useState<number>(activity?.xpReward || 25);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Preview Mode Toggle
  const [isPreviewing, setIsPreviewing] = useState(false);

  // Dynamic Form States for 8 Game Types
  const [pairs, setPairs] = useState<{ left: string; right: string }[]>([
    { left: '1/2', right: 'Half' },
    { left: '1/4', right: 'Quarter' }
  ]);
  const [tfQuestions, setTfQuestions] = useState<{ questionText: string; correctAnswer: string }[]>([
    { questionText: 'Is 14 an even number?', correctAnswer: 'true' }
  ]);
  const [fillQuestions, setFillQuestions] = useState<{ questionText: string; correctAnswer: string }[]>([
    { questionText: '2, 4, 6, __, 10', correctAnswer: '8' }
  ]);
  const [cards, setCards] = useState<{ front: string; back: string }[]>([
    { front: '3 Sided Polygon', back: 'Triangle' }
  ]);
  const [scrambleWords, setScrambleWords] = useState<{ scrambled: string; target: string }[]>([
    { scrambled: 'ELGNAT', target: 'ANGLE' }
  ]);
  const [mcqQuestions, setMcqQuestions] = useState<{ questionText: string; correctAnswer: string; optA: string; optB: string; optC: string; optD: string }[]>([
    { questionText: '50% of 200 = ?', correctAnswer: '100', optA: '100', optB: '50', optC: '150', optD: '75' }
  ]);
  const [stages, setStages] = useState<{ clue: string; correctAnswer: string }[]>([
    { clue: 'Solve x + 5 = 10', correctAnswer: '5' },
    { clue: 'Solve 2x = 12', correctAnswer: '6' }
  ]);

  // Load and prepopulate existing activity content, description, and game configuration
  useEffect(() => {
    if (!activity) return;

    setTitle(activity.title || '');
    setDescription(activity.description || '');
    setSubject(activity.subject || 'MATHEMATICS');
    
    const cat = activity.activityType === 'LESSON' ? 'LESSON' : activity.activityType === 'QUIZ' ? 'QUIZ' : 'GAME';
    setActivityCategory(cat);

    const gType = activity.gameType || (activity.activityType !== 'LESSON' && activity.activityType !== 'QUIZ' ? activity.activityType : 'MATCH_THE_FOLLOWING');
    setResolvedGameType(gType);

    setSelectedModuleId(activity.moduleId || (modules.length > 0 ? modules[0].id : ''));
    setXpReward(activity.xpReward || 25);

    const loadExistingConfig = async () => {
      try {
        let jsonStr = activity.activityMetadataJson;
        if (!jsonStr && activity.id) {
          const gc = await moduleService.getGameConfig(activity.id);
          if (gc && gc.jsonConfiguration) {
            jsonStr = gc.jsonConfiguration;
          }
        }

        if (jsonStr) {
          const parsed = JSON.parse(jsonStr);

          if (parsed.gameType) {
            setResolvedGameType(parsed.gameType);
          }

          if (parsed.pairs && Array.isArray(parsed.pairs) && parsed.pairs.length > 0) {
            setPairs(parsed.pairs.map((p: any) => ({
              left: p.left || p.prompt || '',
              right: p.right || p.correctAnswer || ''
            })));
          }

          if (parsed.questions && Array.isArray(parsed.questions) && parsed.questions.length > 0) {
            const targetType = parsed.gameType || activity.activityType;

            if (targetType === 'TRUE_FALSE') {
              setTfQuestions(parsed.questions.map((q: any) => ({
                questionText: q.questionText || q.prompt || '',
                correctAnswer: String(q.correctAnswer).toLowerCase() === 'false' ? 'false' : 'true'
              })));
            } else if (targetType === 'FILL_IN_THE_BLANK') {
              setFillQuestions(parsed.questions.map((q: any) => ({
                questionText: q.questionText || q.prompt || '',
                correctAnswer: q.correctAnswer || ''
              })));
            } else if (targetType === 'SHOOT_THE_ANSWER' || targetType === 'BALLOON_POP') {
              setMcqQuestions(parsed.questions.map((q: any) => {
                const opts = q.options || [];
                return {
                  questionText: q.questionText || q.prompt || '',
                  correctAnswer: q.correctAnswer || '',
                  optA: opts[0] || '',
                  optB: opts[1] || '',
                  optC: opts[2] || '',
                  optD: opts[3] || ''
                };
              }));
            }
          }

          if (parsed.cards && Array.isArray(parsed.cards) && parsed.cards.length > 0) {
            setCards(parsed.cards.map((c: any) => ({
              front: c.front || c.prompt || '',
              back: c.back || c.correctAnswer || ''
            })));
          }

          if (parsed.words && Array.isArray(parsed.words) && parsed.words.length > 0) {
            setScrambleWords(parsed.words.map((w: any) => ({
              scrambled: w.scrambled || w.word || w.prompt || '',
              target: w.target || w.correctAnswer || ''
            })));
          }

          if (parsed.stages && Array.isArray(parsed.stages) && parsed.stages.length > 0) {
            setStages(parsed.stages.map((s: any) => ({
              clue: s.clue || s.prompt || '',
              correctAnswer: s.correctAnswer || ''
            })));
          }
        }
      } catch (err) {
        console.error('Failed to load existing game configuration:', err);
      }
    };

    loadExistingConfig();
  }, [activity]);

  useEffect(() => {
    if (!activity?.id) return;
    if (activityCategory === 'LESSON') {
      moduleService.getLessonContent(activity.id)
        .then((content) => setLessonContent(content.content || ''))
        .catch(() => setLessonContent(''));
    } else if (activityCategory === 'QUIZ') {
      setLoadingExistingQuizQuestions(true);
      moduleService.getQuizQuestions(activity.id)
        .then((questions) => setExistingQuizQuestionCount(questions.length))
        .catch(() => setExistingQuizQuestionCount(0))
        .finally(() => setLoadingExistingQuizQuestions(false));
    } else {
      setLoadingExistingQuizQuestions(false);
    }
  }, [activity?.id, activityCategory]);

  const generateJsonConfig = (): string => {
    switch (resolvedGameType) {
      case 'MATCH_THE_FOLLOWING':
        return JSON.stringify({ gameType: 'MATCH_THE_FOLLOWING', pairs });
      case 'TRUE_FALSE':
        return JSON.stringify({ gameType: 'TRUE_FALSE', questions: tfQuestions });
      case 'FILL_IN_THE_BLANK':
        return JSON.stringify({ gameType: 'FILL_IN_THE_BLANK', questions: fillQuestions });
      case 'FLASH_CARDS':
        return JSON.stringify({ gameType: 'FLASH_CARDS', cards });
      case 'WORD_SCRAMBLE':
        return JSON.stringify({ gameType: 'WORD_SCRAMBLE', words: scrambleWords });
      case 'SHOOT_THE_ANSWER':
      case 'BALLOON_POP':
        return JSON.stringify({
          gameType: resolvedGameType,
          questions: mcqQuestions.map((q) => ({
            questionText: q.questionText,
            correctAnswer: q.correctAnswer,
            options: [q.optA, q.optB, q.optC, q.optD].filter(Boolean)
          }))
        });
      case 'TREASURE_HUNT':
        return JSON.stringify({ gameType: 'TREASURE_HUNT', stages });
      default:
        return JSON.stringify({ gameType: resolvedGameType, pairs });
    }
  };

  const handleSave = async (publishImmediately: boolean) => {
    if (!title.trim()) {
      setError('Please enter an activity title.');
      return;
    }

    if (activityCategory === 'QUIZ' && loadingExistingQuizQuestions) {
      setError('Loading the saved quiz questions. Please wait a moment and try again.');
      return;
    }

    if (activityCategory === 'LESSON' && publishImmediately && !lessonContent.trim()) {
      setError('Add the lesson reading content before publishing it.');
      return;
    }

    const completedQuizDrafts = quizDrafts.filter((question) =>
      question.questionText.trim() && question.optionA.trim() && question.optionB.trim()
      && question.optionC.trim() && question.optionD.trim()
    );
    const hasPartialQuizDraft = quizDrafts.some((question) => {
      const values = [question.questionText, question.optionA, question.optionB, question.optionC, question.optionD];
      return values.some((value) => value.trim()) && values.some((value) => !value.trim());
    });
    if (activityCategory === 'QUIZ' && hasPartialQuizDraft) {
      setError('Complete every quiz question and all four answer choices, or remove the unfinished question.');
      return;
    }
    if (activityCategory === 'QUIZ' && publishImmediately && existingQuizQuestionCount + completedQuizDrafts.length === 0) {
      setError('Add at least one question before publishing a quiz.');
      return;
    }

    setSubmitting(true);
    setError(null);
    try {
      const finalActivityType = activityCategory === 'GAME' ? resolvedGameType : activityCategory;
      const activityPayload = {
        title,
        description,
        subject,
        activityType: finalActivityType,
        moduleId: selectedModuleId ? Number(selectedModuleId) : undefined,
        assignedClassroomId: classroomId,
        xpReward,
        status: publishImmediately ? 'PUBLISHED' : 'DRAFT'
      };

      const jsonConfig = activityCategory === 'GAME' ? generateJsonConfig() : undefined;
      const orderedQuizDrafts = completedQuizDrafts.map((question, index) => ({
        ...question,
        displayOrder: existingQuizQuestionCount + index + 1
      }));
      await onSave(
        activityPayload,
        jsonConfig,
        publishImmediately,
        activityCategory === 'LESSON' ? lessonContent : undefined,
        activityCategory === 'QUIZ' ? orderedQuizDrafts : undefined
      );
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to save activity.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-black/60 backdrop-blur-sm">
      <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-3xl sm:max-w-4xl w-full p-6 shadow-2xl border border-gray-200 dark:border-gray-700 flex flex-col max-h-[90vh]">
        {/* Modal Header */}
        <div className="flex items-center justify-between pb-3 border-b border-gray-100 dark:border-gray-700 flex-shrink-0">
          <div className="flex items-center gap-2">
            <Gamepad2 className="w-6 h-6 text-emerald-600" />
            <h2 className="text-xl font-bold text-gray-900 dark:text-white">
              {activity?.id
                ? `Edit ${activityCategory === 'LESSON' ? 'Lesson' : activityCategory === 'QUIZ' ? 'Quiz' : 'Practice Game'}`
                : `Create ${activityCategory === 'LESSON' ? 'Lesson' : activityCategory === 'QUIZ' ? 'Quiz' : 'Practice Game'}`}
            </h2>
          </div>
          <button onClick={onClose} className="p-1.5 text-gray-400 hover:text-gray-600 rounded-lg">
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && (
          <div className="p-3 my-2 text-xs rounded-xl bg-red-50 text-red-700 border border-red-200 flex-shrink-0">
            {error}
          </div>
        )}

        {/* Live Preview Mode Toggle */}
        {isPreviewing ? (
          <div className="flex-1 overflow-y-auto py-4 space-y-4 pr-1">
            <div className="flex items-center justify-between bg-emerald-50 dark:bg-emerald-950/40 p-3 rounded-xl">
              <span className="text-xs font-bold text-emerald-800 dark:text-emerald-300">
                🎮 Teacher Preview Mode (Testing as Student)
              </span>
              <button
                onClick={() => setIsPreviewing(false)}
                className="px-3 py-1 bg-white text-gray-700 hover:bg-gray-100 rounded-lg text-xs font-semibold shadow-sm"
              >
                Back to Form Editor
              </button>
            </div>
            <MiniGamePlayer
              activityId={9999}
              activityTitle={title || 'Teacher Game Preview'}
              activityType={resolvedGameType}
              onComplete={(res: any) => alert(`Preview Complete! Performance score: ${res?.score || 100}%`)}
            />
          </div>
        ) : (
          <form onSubmit={(e) => { e.preventDefault(); handleSave(false); }} className="flex-1 flex flex-col min-h-0 pt-4">
            <div className="flex-1 overflow-y-auto space-y-4 pr-1 scrollbar-thin">
            {/* Step 1: Category Selector */}
            <div>
              <label className="block text-xs font-bold text-gray-500 uppercase mb-1.5">Activity Category</label>
              <div className="grid grid-cols-3 gap-3">
                {(['LESSON', 'QUIZ', 'GAME'] as const).map((cat) => (
                  <button
                    type="button"
                    key={cat}
                    onClick={() => setActivityCategory(cat)}
                    className={`py-2.5 px-3 rounded-xl text-xs font-bold transition-all border ${
                      activityCategory === cat
                        ? 'bg-emerald-600 text-white border-emerald-600 shadow-sm'
                        : 'bg-gray-50 dark:bg-gray-700 text-gray-700 dark:text-gray-300 border-gray-200 dark:border-gray-600'
                    }`}
                  >
                    {cat === 'LESSON' ? '📖 Lesson · Read' : cat === 'QUIZ' ? '❓ Quiz · Score' : '🎮 Game · Practice'}
                  </button>
                ))}
              </div>
              <p className="mt-2 rounded-lg bg-sky-50 px-3 py-2 text-xs leading-relaxed text-sky-800 dark:bg-sky-950/40 dark:text-sky-200" role="status">
                {activityCategory === 'LESSON' && 'Lesson: a student reads an explanation. Add the reading content below; it is not a scored quiz.'}
                {activityCategory === 'QUIZ' && 'Quiz: a student answers scored multiple-choice questions. Add the question set below.'}
                {activityCategory === 'GAME' && 'Game: interactive practice such as matching, flashcards, or a challenge.'}
              </p>
            </div>

            {/* Basic Info */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 mb-1">{activityCategory === 'LESSON' ? 'Lesson Title' : activityCategory === 'QUIZ' ? 'Quiz Title' : 'Game Title'}</label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder={activityCategory === 'LESSON' ? 'e.g. Forms of Energy' : activityCategory === 'QUIZ' ? 'e.g. Energy Check' : 'e.g. Energy Matching Challenge'}
                  className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-sm focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 mb-1">Subject</label>
                <select
                  value={subject}
                  onChange={(e) => setSubject(e.target.value as Subject)}
                  className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-sm focus:ring-2 focus:ring-emerald-500"
                >
                  <option value="MATHEMATICS">MATHEMATICS</option>
                  <option value="SCIENCE">SCIENCE</option>
                  <option value="ENGLISH">ENGLISH</option>
                  <option value="SOCIAL_SCIENCE">SOCIAL SCIENCE</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-600 dark:text-gray-300 mb-1">{activityCategory === 'LESSON' ? 'Learning Objective' : activityCategory === 'QUIZ' ? 'Quiz Instructions' : 'Practice Description'}</label>
              <textarea
                rows={2}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder={activityCategory === 'LESSON' ? 'What will students learn from this reading?' : activityCategory === 'QUIZ' ? 'Tell students what this quiz checks...' : 'Describe the skill this game practices...'}
                className="w-full px-3.5 py-2 border border-gray-300 dark:border-gray-600 rounded-xl text-sm focus:ring-2 focus:ring-emerald-500"
              />
            </div>

            <div className="rounded-xl border border-gray-200 p-3 dark:border-gray-700">
              <label htmlFor="activity-topic-module" className="block text-xs font-semibold text-gray-700 dark:text-gray-200">Topic module</label>
              <p className="mb-2 mt-1 text-[11px] text-gray-500 dark:text-gray-400">Attach this lesson or practice activity to a topic so students find it in that module’s learning path.</p>
              <select
                id="activity-topic-module"
                value={selectedModuleId}
                onChange={(event) => setSelectedModuleId(event.target.value ? Number(event.target.value) : '')}
                className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900 dark:border-gray-600 dark:bg-gray-700 dark:text-white"
              >
                <option value="">Standalone activity</option>
                {modules.map((module) => <option key={module.id} value={module.id}>{module.title} · {module.subject}</option>)}
              </select>
              {modules.length === 0 && <p className="mt-2 text-[11px] text-amber-700 dark:text-amber-300">Create a module first to group this with a topic summary.</p>}
            </div>

            {activityCategory === 'LESSON' && (
              <div className="rounded-2xl border border-sky-200 bg-sky-50/70 p-4 dark:border-sky-900/60 dark:bg-sky-950/20 space-y-2">
                <div>
                  <h3 className="text-sm font-bold text-sky-900 dark:text-sky-200">Lesson reading content</h3>
                  <p className="mt-1 text-xs text-sky-800 dark:text-sky-300">Students read this explanation before moving on to the module’s practice activities.</p>
                </div>
                <textarea
                  rows={7}
                  value={lessonContent}
                  onChange={(event) => setLessonContent(event.target.value)}
                  placeholder="Explain the concept in clear, student-friendly language..."
                  className="w-full rounded-xl border border-sky-200 bg-white px-3.5 py-3 text-sm leading-relaxed text-gray-900 focus:ring-2 focus:ring-sky-500 dark:border-sky-800 dark:bg-gray-800 dark:text-white"
                  aria-label="Lesson reading content"
                />
                {!activity?.id && <p className="text-[11px] text-gray-500 dark:text-gray-400">Publishing requires lesson content. You can save a draft and finish it later.</p>}
              </div>
            )}

            {activityCategory === 'QUIZ' && (
              <div className="rounded-2xl border border-amber-200 bg-amber-50/60 p-4 dark:border-amber-900/60 dark:bg-amber-950/20 space-y-4">
                <div>
                  <h3 className="text-sm font-bold text-amber-900 dark:text-amber-200">Quiz questions</h3>
                  <p className="mt-1 text-xs text-amber-800 dark:text-amber-300">Students choose one answer for each question. Select the correct option to score the quiz.</p>
                  {existingQuizQuestionCount > 0 && (
                    <p className="mt-2 text-xs font-semibold text-gray-600 dark:text-gray-300">{existingQuizQuestionCount} saved question{existingQuizQuestionCount === 1 ? '' : 's'} will be kept; new questions below will be added.</p>
                  )}
                </div>
                {quizDrafts.map((question, index) => (
                  <fieldset key={index} className="rounded-xl border border-amber-200 bg-white p-3 dark:border-amber-900/60 dark:bg-gray-800 space-y-2">
                    <legend className="px-1 text-xs font-bold text-gray-700 dark:text-gray-200">Question {existingQuizQuestionCount + index + 1}</legend>
                    <input
                      value={question.questionText}
                      onChange={(event) => setQuizDrafts((items) => items.map((item, i) => i === index ? { ...item, questionText: event.target.value } : item))}
                      placeholder="Ask a question about the lesson topic"
                      className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                      aria-label={`Question ${index + 1} prompt`}
                    />
                    <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
                      {(['A', 'B', 'C', 'D'] as const).map((letter) => {
                        const key = `option${letter}` as 'optionA' | 'optionB' | 'optionC' | 'optionD';
                        return (
                          <label key={letter} className="flex items-center gap-2 rounded-lg border border-gray-200 px-2.5 dark:border-gray-700">
                            <input type="radio" name={`correct-${index}`} checked={question.correctAnswer === letter} onChange={() => setQuizDrafts((items) => items.map((item, i) => i === index ? { ...item, correctAnswer: letter } : item))} aria-label={`Option ${letter} is correct`} />
                            <span className="text-xs font-bold text-gray-500">{letter}</span>
                            <input
                              value={question[key]}
                              onChange={(event) => setQuizDrafts((items) => items.map((item, i) => i === index ? { ...item, [key]: event.target.value } : item))}
                              placeholder={`Option ${letter}`}
                              className="min-w-0 flex-1 border-0 bg-transparent px-1 py-2 text-sm text-gray-900 outline-none dark:text-white"
                              aria-label={`Question ${index + 1}, option ${letter}`}
                            />
                          </label>
                        );
                      })}
                    </div>
                    <input
                      value={question.explanation}
                      onChange={(event) => setQuizDrafts((items) => items.map((item, i) => i === index ? { ...item, explanation: event.target.value } : item))}
                      placeholder="Answer explanation (optional)"
                      className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm dark:border-gray-600 dark:bg-gray-700 dark:text-white"
                      aria-label={`Question ${index + 1} explanation`}
                    />
                    {quizDrafts.length > 1 && (
                      <button type="button" onClick={() => setQuizDrafts((items) => items.filter((_, i) => i !== index))} className="text-xs font-semibold text-red-600 hover:text-red-700">Remove question</button>
                    )}
                  </fieldset>
                ))}
                <button type="button" onClick={() => setQuizDrafts((items) => [...items, emptyQuizDraft()])} className="inline-flex items-center gap-1 text-xs font-bold text-amber-800 hover:text-amber-900 dark:text-amber-300">
                  <Plus className="h-4 w-4" /> Add question
                </button>
              </div>
            )}

            {/* If GAME selected: Game Type Dropdown */}
            {activityCategory === 'GAME' && (
              <div className="p-4 bg-emerald-50/50 dark:bg-emerald-950/30 rounded-2xl border border-emerald-100 dark:border-emerald-800/40 space-y-4">
                <div>
                  <label className="block text-xs font-bold text-emerald-800 dark:text-emerald-300 uppercase mb-1">
                    Select Interactive Game Type
                  </label>
                  <select
                    value={resolvedGameType}
                    onChange={(e) => setResolvedGameType(e.target.value)}
                    className="w-full px-3.5 py-2 border border-emerald-300 dark:border-emerald-700 rounded-xl text-sm font-semibold bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-emerald-500"
                  >
                    <option value="MATCH_THE_FOLLOWING">🧩 MATCH_THE_FOLLOWING</option>
                    <option value="TRUE_FALSE">✅ TRUE_FALSE</option>
                    <option value="FILL_IN_THE_BLANK">✏️ FILL_IN_THE_BLANK</option>
                    <option value="FLASH_CARDS">🎴 FLASH_CARDS</option>
                    <option value="WORD_SCRAMBLE">🔤 WORD_SCRAMBLE</option>
                    <option value="SHOOT_THE_ANSWER">🎯 SHOOT_THE_ANSWER</option>
                    <option value="BALLOON_POP">🎈 BALLOON_POP</option>
                    <option value="TREASURE_HUNT">🗺️ TREASURE_HUNT</option>
                  </select>
                </div>

                {/* Form-Based Editors per Game Type */}
                {resolvedGameType === 'MATCH_THE_FOLLOWING' && (
                  <div className="space-y-3">
                    <label className="block text-xs font-bold text-gray-700 dark:text-gray-300">Matching Pairs</label>
                    {pairs.map((p, idx) => (
                      <div key={idx} className="flex gap-2 items-center">
                        <input
                          type="text"
                          placeholder="Left Item"
                          value={p.left}
                          onChange={(e) => {
                            const newPairs = [...pairs];
                            newPairs[idx].left = e.target.value;
                            setPairs(newPairs);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs"
                        />
                        <span>→</span>
                        <input
                          type="text"
                          placeholder="Right Item"
                          value={p.right}
                          onChange={(e) => {
                            const newPairs = [...pairs];
                            newPairs[idx].right = e.target.value;
                            setPairs(newPairs);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs"
                        />
                        <button type="button" onClick={() => setPairs(pairs.filter((_, i) => i !== idx))} className="text-red-500 p-1">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={() => setPairs([...pairs, { left: '', right: '' }])}
                      className="text-xs font-bold text-emerald-600 flex items-center gap-1"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add Matching Pair
                    </button>
                  </div>
                )}

                {resolvedGameType === 'TRUE_FALSE' && (
                  <div className="space-y-3">
                    <label className="block text-xs font-bold text-gray-700 dark:text-gray-300">True / False Statements</label>
                    {tfQuestions.map((q, idx) => (
                      <div key={idx} className="flex gap-2 items-center">
                        <input
                          type="text"
                          placeholder="Statement prompt"
                          value={q.questionText}
                          onChange={(e) => {
                            const newQ = [...tfQuestions];
                            newQ[idx].questionText = e.target.value;
                            setTfQuestions(newQ);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs"
                        />
                        <select
                          value={q.correctAnswer}
                          onChange={(e) => {
                            const newQ = [...tfQuestions];
                            newQ[idx].correctAnswer = e.target.value;
                            setTfQuestions(newQ);
                          }}
                          className="px-2 py-1.5 border border-gray-300 rounded-lg text-xs font-bold"
                        >
                          <option value="true">True</option>
                          <option value="false">False</option>
                        </select>
                        <button type="button" onClick={() => setTfQuestions(tfQuestions.filter((_, i) => i !== idx))} className="text-red-500 p-1">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={() => setTfQuestions([...tfQuestions, { questionText: '', correctAnswer: 'true' }])}
                      className="text-xs font-bold text-emerald-600 flex items-center gap-1"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add T/F Statement
                    </button>
                  </div>
                )}

                {resolvedGameType === 'FILL_IN_THE_BLANK' && (
                  <div className="space-y-3">
                    <label className="block text-xs font-bold text-gray-700 dark:text-gray-300">Fill In The Blank Questions</label>
                    {fillQuestions.map((q, idx) => (
                      <div key={idx} className="flex gap-2 items-center">
                        <input
                          type="text"
                          placeholder="Question with __ for blank"
                          value={q.questionText}
                          onChange={(e) => {
                            const newQ = [...fillQuestions];
                            newQ[idx].questionText = e.target.value;
                            setFillQuestions(newQ);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs"
                        />
                        <input
                          type="text"
                          placeholder="Correct Fill Word"
                          value={q.correctAnswer}
                          onChange={(e) => {
                            const newQ = [...fillQuestions];
                            newQ[idx].correctAnswer = e.target.value;
                            setFillQuestions(newQ);
                          }}
                          className="w-36 px-3 py-1.5 border border-gray-300 rounded-lg text-xs font-bold"
                        />
                        <button type="button" onClick={() => setFillQuestions(fillQuestions.filter((_, i) => i !== idx))} className="text-red-500 p-1">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={() => setFillQuestions([...fillQuestions, { questionText: '', correctAnswer: '' }])}
                      className="text-xs font-bold text-emerald-600 flex items-center gap-1"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add Fill Question
                    </button>
                  </div>
                )}

                {resolvedGameType === 'FLASH_CARDS' && (
                  <div className="space-y-3">
                    <label className="block text-xs font-bold text-gray-700 dark:text-gray-300">Flash Cards (Front & Back)</label>
                    {cards.map((c, idx) => (
                      <div key={idx} className="flex gap-2 items-center">
                        <input
                          type="text"
                          placeholder="Front Side Prompt"
                          value={c.front}
                          onChange={(e) => {
                            const newCards = [...cards];
                            newCards[idx].front = e.target.value;
                            setCards(newCards);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs"
                        />
                        <span>↔</span>
                        <input
                          type="text"
                          placeholder="Back Side Answer"
                          value={c.back}
                          onChange={(e) => {
                            const newCards = [...cards];
                            newCards[idx].back = e.target.value;
                            setCards(newCards);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs"
                        />
                        <button type="button" onClick={() => setCards(cards.filter((_, i) => i !== idx))} className="text-red-500 p-1">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={() => setCards([...cards, { front: '', back: '' }])}
                      className="text-xs font-bold text-emerald-600 flex items-center gap-1"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add Flashcard
                    </button>
                  </div>
                )}

                {resolvedGameType === 'WORD_SCRAMBLE' && (
                  <div className="space-y-3">
                    <label className="block text-xs font-bold text-gray-700 dark:text-gray-300">Word Scramble Pairs</label>
                    {scrambleWords.map((w, idx) => (
                      <div key={idx} className="flex gap-2 items-center">
                        <input
                          type="text"
                          placeholder="Scrambled Letters (e.g. ELGNAT)"
                          value={w.scrambled}
                          onChange={(e) => {
                            const newW = [...scrambleWords];
                            newW[idx].scrambled = e.target.value;
                            setScrambleWords(newW);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs uppercase"
                        />
                        <span>→</span>
                        <input
                          type="text"
                          placeholder="Target Word (e.g. ANGLE)"
                          value={w.target}
                          onChange={(e) => {
                            const newW = [...scrambleWords];
                            newW[idx].target = e.target.value;
                            setScrambleWords(newW);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs uppercase font-bold"
                        />
                        <button type="button" onClick={() => setScrambleWords(scrambleWords.filter((_, i) => i !== idx))} className="text-red-500 p-1">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={() => setScrambleWords([...scrambleWords, { scrambled: '', target: '' }])}
                      className="text-xs font-bold text-emerald-600 flex items-center gap-1"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add Word Scramble
                    </button>
                  </div>
                )}

                {(resolvedGameType === 'SHOOT_THE_ANSWER' || resolvedGameType === 'BALLOON_POP') && (
                  <div className="space-y-4">
                    <label className="block text-xs font-bold text-gray-700 dark:text-gray-300">Target Questions & Distractors</label>
                    {mcqQuestions.map((q, idx) => (
                      <div key={idx} className="p-3 bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 space-y-2">
                        <div className="flex gap-2 items-center">
                          <input
                            type="text"
                            placeholder="Question Prompt"
                            value={q.questionText}
                            onChange={(e) => {
                              const newQ = [...mcqQuestions];
                              newQ[idx].questionText = e.target.value;
                              setMcqQuestions(newQ);
                            }}
                            className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs font-semibold"
                          />
                          <button type="button" onClick={() => setMcqQuestions(mcqQuestions.filter((_, i) => i !== idx))} className="text-red-500 p-1">
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                          <input
                            type="text"
                            placeholder="Correct Answer"
                            value={q.correctAnswer}
                            onChange={(e) => {
                              const newQ = [...mcqQuestions];
                              newQ[idx].correctAnswer = e.target.value;
                              setMcqQuestions(newQ);
                            }}
                            className="px-3 py-1 border border-emerald-400 bg-emerald-50/50 rounded-lg text-xs font-bold text-emerald-800"
                          />
                          <input
                            type="text"
                            placeholder="Wrong Option 1"
                            value={q.optA}
                            onChange={(e) => {
                              const newQ = [...mcqQuestions];
                              newQ[idx].optA = e.target.value;
                              setMcqQuestions(newQ);
                            }}
                            className="px-3 py-1 border border-gray-300 rounded-lg text-xs"
                          />
                          <input
                            type="text"
                            placeholder="Wrong Option 2"
                            value={q.optB}
                            onChange={(e) => {
                              const newQ = [...mcqQuestions];
                              newQ[idx].optB = e.target.value;
                              setMcqQuestions(newQ);
                            }}
                            className="px-3 py-1 border border-gray-300 rounded-lg text-xs"
                          />
                          <input
                            type="text"
                            placeholder="Wrong Option 3"
                            value={q.optC}
                            onChange={(e) => {
                              const newQ = [...mcqQuestions];
                              newQ[idx].optC = e.target.value;
                              setMcqQuestions(newQ);
                            }}
                            className="px-3 py-1 border border-gray-300 rounded-lg text-xs"
                          />
                        </div>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={() => setMcqQuestions([...mcqQuestions, { questionText: '', correctAnswer: '', optA: '', optB: '', optC: '', optD: '' }])}
                      className="text-xs font-bold text-emerald-600 flex items-center gap-1"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add Target Question
                    </button>
                  </div>
                )}

                {resolvedGameType === 'TREASURE_HUNT' && (
                  <div className="space-y-3">
                    <label className="block text-xs font-bold text-gray-700 dark:text-gray-300">Treasure Hunt Journey Stages</label>
                    {stages.map((st, idx) => (
                      <div key={idx} className="flex gap-2 items-center">
                        <span className="text-xs font-bold text-amber-600">Stage {idx + 1}</span>
                        <input
                          type="text"
                          placeholder="Stage Clue Prompt"
                          value={st.clue}
                          onChange={(e) => {
                            const newSt = [...stages];
                            newSt[idx].clue = e.target.value;
                            setStages(newSt);
                          }}
                          className="flex-1 px-3 py-1.5 border border-gray-300 rounded-lg text-xs"
                        />
                        <input
                          type="text"
                          placeholder="Correct Answer"
                          value={st.correctAnswer}
                          onChange={(e) => {
                            const newSt = [...stages];
                            newSt[idx].correctAnswer = e.target.value;
                            setStages(newSt);
                          }}
                          className="w-32 px-3 py-1.5 border border-gray-300 rounded-lg text-xs font-bold text-amber-800"
                        />
                        <button type="button" onClick={() => setStages(stages.filter((_, i) => i !== idx))} className="text-red-500 p-1">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={() => setStages([...stages, { clue: '', correctAnswer: '' }])}
                      className="text-xs font-bold text-amber-600 flex items-center gap-1"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add Stage Clue
                    </button>
                  </div>
                )}
              </div>
            )}
            </div>

            {/* Action Bar */}
            <div className="flex items-center justify-between pt-4 border-t border-gray-100 dark:border-gray-700 flex-shrink-0 mt-2">
              {activityCategory === 'GAME' ? (
                <button
                  type="button"
                  onClick={() => setIsPreviewing(true)}
                  className="px-4 py-2 bg-purple-100 dark:bg-purple-950 text-purple-700 dark:text-purple-300 hover:bg-purple-200 rounded-xl text-xs font-bold flex items-center gap-1.5 transition-colors"
                >
                  <Play className="w-4 h-4" />
                  Preview Game
                </button>
              ) : <div />}

              <div className="flex items-center space-x-3">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 text-xs font-semibold text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-xl transition-colors"
                >
                  Cancel
                </button>

                <button
                  type="button"
                  disabled={submitting}
                  onClick={() => handleSave(false)}
                  className="px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 hover:bg-gray-300 rounded-xl text-xs font-bold flex items-center gap-1.5 transition-colors"
                >
                  <Save className="w-4 h-4" />
                  Save Draft
                </button>

                <button
                  type="button"
                  disabled={submitting}
                  onClick={() => handleSave(true)}
                  className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 transition-colors shadow-sm"
                >
                  <Send className="w-4 h-4" />
                  {submitting ? 'Publishing...' : `Publish ${activityCategory === 'LESSON' ? 'Lesson' : activityCategory === 'QUIZ' ? 'Quiz' : 'Game'}`}
                </button>
              </div>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
