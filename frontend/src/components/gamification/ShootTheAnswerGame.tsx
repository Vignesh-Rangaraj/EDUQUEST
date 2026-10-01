import React, { useState, useEffect } from 'react';
import { gameEngineService } from '../../services/gameEngineService';

interface TargetOption {
  text: string;
  isCorrect: boolean;
  top: number;
  left: number;
}

interface Question {
  prompt: string;
  options: string[];
  correctAnswer: string;
}

interface ShootTheAnswerGameProps {
  activityId: number;
  activityTitle: string;
  config?: any;
  onComplete?: (result: any) => void;
}

export const ShootTheAnswerGame: React.FC<ShootTheAnswerGameProps> = ({
  activityId,
  activityTitle,
  config,
  onComplete
}) => {
  const rawQuestions = config?.questions || config?.items || [
    { prompt: 'What is 5 + 5?', options: ['8', '10', '12', '14'], correctAnswer: '10' },
    { prompt: 'Which gas do plants absorb during photosynthesis?', options: ['Oxygen', 'Carbon Dioxide', 'Nitrogen', 'Helium'], correctAnswer: 'Carbon Dioxide' },
    { prompt: 'What is 12 ÷ 4?', options: ['2', '3', '4', '6'], correctAnswer: '3' }
  ];

  const questions: Question[] = rawQuestions.map((q: any) => {
    const prompt = q.questionText || q.prompt || q.question || q.clue || 'Question';
    const correctAnswer = (q.correctAnswer || q.answer || '').toString();
    let options: string[] = Array.isArray(q.options) ? q.options.map((o: any) => o.toString()) : [];
    if (correctAnswer && !options.some(o => o.trim().toLowerCase() === correctAnswer.trim().toLowerCase())) {
      options.push(correctAnswer);
    }
    if (options.length === 0) {
      options = [correctAnswer || 'Option 1', 'Option 2', 'Option 3', 'Option 4'];
    }
    return { prompt, correctAnswer, options };
  });

  const [currentIndex, setCurrentIndex] = useState(0);
  const [userAnswers, setUserAnswers] = useState<Record<number, string>>({});
  const [targets, setTargets] = useState<TargetOption[]>([]);
  const [hitIndex, setHitIndex] = useState<number | null>(null);
  const [completed, setCompleted] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [shotsFired, setShotsFired] = useState(0);

  const currentQuestion = questions[currentIndex];

  useEffect(() => {
    if (!currentQuestion) return;
    const positions = [
      { top: 25, left: 20 },
      { top: 25, left: 70 },
      { top: 65, left: 30 },
      { top: 65, left: 75 }
    ].sort(() => Math.random() - 0.5);

    const generated = currentQuestion.options.map((opt, idx) => ({
      text: opt,
      isCorrect: opt.trim().toLowerCase() === currentQuestion.correctAnswer.trim().toLowerCase(),
      top: positions[idx % positions.length].top,
      left: positions[idx % positions.length].left
    }));
    setTargets(generated);
    setHitIndex(null);
  }, [currentIndex]);

  const handleShootTarget = (targetIdx: number, selectedText: string) => {
    if (hitIndex !== null || completed) return;
    setHitIndex(targetIdx);
    setShotsFired((prev) => prev + 1);

    const newAnswers = { ...userAnswers, [currentIndex]: selectedText };
    setUserAnswers(newAnswers);

    setTimeout(() => {
      if (currentIndex + 1 < questions.length) {
        setCurrentIndex((prev) => prev + 1);
      } else {
        finishGame(newAnswers);
      }
    }, 900);
  };

  const finishGame = async (answers: Record<number, string>) => {
    setCompleted(true);
    let correct = 0;
    questions.forEach((q, idx) => {
      if ((answers[idx] || '').trim().toLowerCase() === q.correctAnswer.trim().toLowerCase()) {
        correct++;
      }
    });

    const scorePercent = Math.round((correct / questions.length) * 100);
    const passed = scorePercent >= 70;
    const xpEarned = passed ? Math.round((scorePercent / 100) * 50) : 0;
    const coinsEarned = passed ? (scorePercent >= 80 ? 10 : 5) : 0;

    try {
      const res = await gameEngineService.submitGameAnswers(activityId, {
        gameType: 'SHOOT_THE_ANSWER',
        answers,
        scorePercent,
        xpEarned,
        coinsEarned
      });
      setResult(res);
      if (onComplete) onComplete(res);
    } catch (e) {
      const fallback = {
        success: passed,
        passed,
        gameType: 'SHOOT_THE_ANSWER',
        correctCount: correct,
        totalQuestions: questions.length,
        scorePercent,
        xpEarned,
        coinsEarned
      };
      setResult(fallback);
      if (onComplete) onComplete(fallback);
    }
  };

  const handleRetry = () => {
    setCurrentIndex(0);
    setUserAnswers({});
    setHitIndex(null);
    setCompleted(false);
    setResult(null);
    setShotsFired(0);
  };

  if (completed && result) {
    const accuracy = shotsFired > 0 ? Math.round((result.correctCount / shotsFired) * 100) : 100;
    const isPassed = (result.scorePercent || 0) >= 70 && result.success !== false;

    return (
      <div className="bg-gradient-to-b from-slate-950 via-indigo-950 to-slate-900 text-white p-8 sm:p-10 rounded-3xl shadow-2xl border-2 border-indigo-500/40 max-w-2xl mx-auto text-center space-y-6">
        <div className="relative inline-block">
          <div className="w-24 h-24 mx-auto rounded-full bg-indigo-600/30 border-2 border-indigo-400 flex items-center justify-center text-5xl shadow-xl animate-bounce">
            {isPassed ? '🎯' : '💥'}
          </div>
          {isPassed && (
            <div className="absolute -top-2 -right-2 bg-emerald-400 text-slate-950 font-black text-xs px-2.5 py-1 rounded-full shadow-lg border border-emerald-200">
              SHARPSHOOTER!
            </div>
          )}
        </div>

        <div>
          <span className={`text-xs font-black uppercase tracking-widest px-4 py-1.5 rounded-full border ${
            isPassed ? 'text-indigo-300 bg-indigo-950 border-indigo-700 shadow-md' : 'text-red-400 bg-red-950 border-red-800'
          }`}>
            Shoot The Answer • {isPassed ? 'Stage Cleared!' : 'Validation Failed'}
          </span>
          <h3 className="text-2xl sm:text-3xl font-black mt-3 text-white tracking-wide">{activityTitle}</h3>
        </div>

        <div className="grid grid-cols-3 gap-3 bg-slate-900/90 p-5 rounded-2xl border border-indigo-500/30 shadow-inner">
          <div>
            <p className="text-[10px] uppercase text-indigo-300 font-extrabold tracking-wider">Accuracy</p>
            <p className="text-2xl sm:text-3xl font-black text-emerald-400 mt-1">{accuracy}%</p>
          </div>
          <div>
            <p className="text-[10px] uppercase text-indigo-300 font-extrabold tracking-wider">Score</p>
            <p className="text-2xl sm:text-3xl font-black text-sky-400 mt-1">{result.scorePercent}%</p>
          </div>
          <div>
            <p className="text-[10px] uppercase text-indigo-300 font-extrabold tracking-wider">Hits</p>
            <p className="text-2xl sm:text-3xl font-black text-amber-300 mt-1">{result.correctCount}/{result.totalQuestions}</p>
          </div>
        </div>

        {isPassed ? (
          <div className="flex flex-wrap justify-center gap-4 text-sm font-bold pt-2">
            <span className="bg-gradient-to-r from-indigo-600 to-purple-600 border border-indigo-400 text-white px-5 py-2.5 rounded-full shadow-lg flex items-center gap-2">
              <span>⚡</span> +{result.xpEarned} XP
            </span>
            <span className="bg-gradient-to-r from-amber-500 to-yellow-500 border border-amber-200 text-slate-950 px-5 py-2.5 rounded-full shadow-lg flex items-center gap-2 font-black">
              <span>🪙</span> +{result.coinsEarned} Gold Coins
            </span>
          </div>
        ) : (
          <div className="space-y-4 pt-2">
            <p className="text-xs text-red-400 font-medium">Score below minimum 70% threshold. 0 XP / 0 Coins awarded.</p>
            <button
              onClick={handleRetry}
              className="px-8 py-3 bg-gradient-to-r from-red-600 to-pink-600 hover:brightness-110 text-white font-black text-xs uppercase tracking-wider rounded-2xl shadow-xl transition-all cursor-pointer transform hover:scale-105"
            >
              🔄 Try Again
            </button>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-b from-slate-950 via-indigo-950 to-purple-950 text-white p-6 sm:p-8 rounded-3xl border-2 border-indigo-500/40 shadow-2xl max-w-3xl mx-auto select-none overflow-hidden">
      {/* Floating Levitation Animation Keyframes */}
      <style>{`
        @keyframes levitateFloat {
          0%, 100% {
            transform: translate(-50%, -50%) translateY(0px) scale(1);
          }
          50% {
            transform: translate(-50%, -50%) translateY(-14px) scale(1.03);
          }
        }
      `}</style>

      {/* Header Bar */}
      <div className="flex flex-wrap items-center justify-between pb-4 border-b border-indigo-800/80 mb-5 gap-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-indigo-500/20 border border-indigo-400 flex items-center justify-center text-xl shadow-inner">
            🎯
          </div>
          <div>
            <span className="text-[10px] font-black uppercase tracking-widest text-indigo-400 block">
              Cyber Target Shooter
            </span>
            <h3 className="font-black text-lg text-white">{activityTitle}</h3>
          </div>
        </div>
        <div className="text-right text-xs">
          <span className="text-gray-400 block font-semibold text-[10px] uppercase">Target Question</span>
          <span className="font-black text-indigo-300 text-sm">{currentIndex + 1} / {questions.length}</span>
        </div>
      </div>

      {/* Question Banner */}
      <div className="bg-gradient-to-r from-indigo-900/90 via-purple-900/90 to-slate-900/90 p-5 rounded-2xl border border-indigo-400/40 text-center mb-6 shadow-xl relative overflow-hidden">
        <p className="text-[11px] text-indigo-300 font-extrabold uppercase tracking-widest mb-1">
          Aim & shoot the correct target bubble
        </p>
        <h4 className="text-xl sm:text-2xl font-black text-white">{currentQuestion.prompt}</h4>
      </div>

      {/* Target Arena Area */}
      <div className="relative w-full h-80 sm:h-96 bg-slate-950/90 rounded-2xl border-2 border-dashed border-indigo-500/40 overflow-hidden shadow-inner flex items-center justify-center">
        {/* Holographic Crosshair Background Grid */}
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none opacity-20">
          <div className="w-64 h-64 rounded-full border-2 border-indigo-400 flex items-center justify-center animate-pulse">
            <div className="w-40 h-40 rounded-full border border-indigo-400 flex items-center justify-center">
              <div className="w-16 h-16 rounded-full border border-indigo-300"></div>
            </div>
          </div>
          <div className="absolute w-full h-0.5 bg-indigo-500/30"></div>
          <div className="absolute h-full w-0.5 bg-indigo-500/30"></div>
        </div>

        {/* Floating Levitating Targets */}
        {targets.map((t, idx) => {
          const isHit = hitIndex === idx;
          const floatDuration = 2.6 + (idx % 3) * 0.4;
          const floatDelay = idx * 0.3;

          return (
            <button
              key={idx}
              onClick={() => handleShootTarget(idx, t.text)}
              disabled={hitIndex !== null}
              style={{
                top: `${t.top}%`,
                left: `${t.left}%`,
                animation: isHit ? undefined : `levitateFloat ${floatDuration}s ease-in-out infinite ${floatDelay}s`
              }}
              className={`absolute transform -translate-x-1/2 -translate-y-1/2 px-6 py-3.5 rounded-full font-black text-xs sm:text-sm transition-all duration-300 shadow-2xl flex items-center space-x-2 border-2 ${
                isHit
                  ? t.isCorrect
                    ? 'bg-emerald-500 border-emerald-300 text-white scale-125 animate-ping z-20'
                    : 'bg-red-600 border-red-400 text-white scale-125 animate-bounce z-20'
                  : 'bg-gradient-to-br from-indigo-600 via-purple-600 to-blue-700 hover:from-indigo-500 hover:to-blue-600 border-indigo-300 text-white hover:scale-110 active:scale-95 cursor-pointer shadow-indigo-500/30 z-10'
              }`}
            >
              <span className="text-base">🎯</span>
              <span className="tracking-wide">{t.text}</span>
            </button>
          );
        })}
      </div>

      <div className="mt-4 flex items-center justify-between text-xs text-indigo-300 font-semibold px-2">
        <span>Click the target matching the correct answer!</span>
        <span>Accuracy: {shotsFired > 0 ? Math.round(((userAnswers[0] ? 1 : 0) / shotsFired) * 100) : 100}%</span>
      </div>
    </div>
  );
};
