import React, { useState, useEffect } from 'react';
import { gameEngineService } from '../../services/gameEngineService';

interface BalloonItem {
  id: number;
  text: string;
  colorGradient: string;
  borderColor: string;
  glowColor: string;
  isCorrect: boolean;
  leftPercent: number;
  speedSec: number;
}

interface Question {
  prompt: string;
  options: string[];
  correctAnswer: string;
}

interface BalloonPopGameProps {
  activityId: number;
  activityTitle: string;
  config?: any;
  onComplete?: (result: any) => void;
}

const BALLOON_PALETTES = [
  {
    colorGradient: 'from-pink-500 via-rose-500 to-rose-600',
    borderColor: 'border-pink-300',
    glowColor: 'shadow-pink-500/50'
  },
  {
    colorGradient: 'from-purple-500 via-violet-600 to-indigo-600',
    borderColor: 'border-purple-300',
    glowColor: 'shadow-purple-500/50'
  },
  {
    colorGradient: 'from-cyan-400 via-sky-500 to-blue-600',
    borderColor: 'border-cyan-200',
    glowColor: 'shadow-cyan-400/50'
  },
  {
    colorGradient: 'from-amber-400 via-orange-500 to-amber-600',
    borderColor: 'border-amber-200',
    glowColor: 'shadow-amber-400/50'
  },
  {
    colorGradient: 'from-emerald-400 via-teal-500 to-emerald-600',
    borderColor: 'border-emerald-200',
    glowColor: 'shadow-emerald-400/50'
  }
];

export const BalloonPopGame: React.FC<BalloonPopGameProps> = ({
  activityId,
  activityTitle,
  config,
  onComplete
}) => {
  const rawQuestions = config?.questions || config?.items || [
    { prompt: 'What is the capital of India?', options: ['Delhi', 'Mumbai', 'Chennai', 'Kolkata'], correctAnswer: 'Delhi' },
    { prompt: 'Which organ pumps blood in human body?', options: ['Brain', 'Heart', 'Lungs', 'Liver'], correctAnswer: 'Heart' },
    { prompt: 'What is 8 × 7?', options: ['48', '54', '56', '64'], correctAnswer: '56' }
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
  const [balloons, setBalloons] = useState<BalloonItem[]>([]);
  const [poppedId, setPoppedId] = useState<number | null>(null);
  const [timeLeft, setTimeLeft] = useState(20);
  const [completed, setCompleted] = useState(false);
  const [result, setResult] = useState<any>(null);

  const currentQuestion = questions[currentIndex];

  useEffect(() => {
    if (!currentQuestion || completed) return;
    setTimeLeft(20);
    setPoppedId(null);

    // Calculate dynamic spacing so balloons don't crowd together
    const totalOptions = currentQuestion.options.length;
    const generated: BalloonItem[] = currentQuestion.options.map((opt, idx) => {
      const palette = BALLOON_PALETTES[idx % BALLOON_PALETTES.length];
      const step = 80 / Math.max(1, totalOptions - 1);
      const leftPercent = Math.min(88, Math.max(12, 10 + idx * step));
      return {
        id: idx,
        text: opt,
        colorGradient: palette.colorGradient,
        borderColor: palette.borderColor,
        glowColor: palette.glowColor,
        isCorrect: opt.trim().toLowerCase() === currentQuestion.correctAnswer.trim().toLowerCase(),
        leftPercent,
        speedSec: 3.5 + (idx % 2) * 0.8
      };
    });
    setBalloons(generated);

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          handleNextQuestion();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [currentIndex, completed]);

  const handlePopBalloon = (balloon: BalloonItem) => {
    if (poppedId !== null || completed) return;
    setPoppedId(balloon.id);

    const newAnswers = { ...userAnswers, [currentIndex]: balloon.text };
    setUserAnswers(newAnswers);

    setTimeout(() => {
      handleNextQuestion(newAnswers);
    }, 700);
  };

  const handleNextQuestion = (latestAnswers?: Record<number, string>) => {
    const answersToUse = latestAnswers || userAnswers;
    if (currentIndex + 1 < questions.length) {
      setCurrentIndex((prev) => prev + 1);
    } else {
      finishGame(answersToUse);
    }
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
        gameType: 'BALLOON_POP',
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
        gameType: 'BALLOON_POP',
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
    setPoppedId(null);
    setCompleted(false);
    setResult(null);
    setTimeLeft(20);
  };

  if (completed && result) {
    const isPassed = (result.scorePercent || 0) >= 70 && result.success !== false;

    return (
      <div className="bg-gradient-to-b from-sky-950 via-indigo-950 to-slate-950 text-white p-8 sm:p-10 rounded-3xl shadow-2xl border-2 border-sky-400/40 max-w-2xl mx-auto text-center space-y-6">
        <div className="relative inline-block">
          <div className="w-24 h-24 mx-auto rounded-full bg-gradient-to-br from-sky-400/30 to-indigo-600/30 border-2 border-sky-300 flex items-center justify-center text-5xl shadow-xl animate-bounce">
            {isPassed ? '🎈' : '💥'}
          </div>
          {isPassed && (
            <div className="absolute -top-2 -right-2 bg-amber-400 text-slate-950 font-black text-xs px-2.5 py-1 rounded-full shadow-lg border border-amber-200 animate-pulse">
              VICTORY!
            </div>
          )}
        </div>

        <div>
          <span className={`text-xs font-black uppercase tracking-widest px-4 py-1.5 rounded-full border ${
            isPassed ? 'text-sky-300 bg-sky-950 border-sky-600 shadow-md' : 'text-red-400 bg-red-950 border-red-800'
          }`}>
            Balloon Pop • {isPassed ? 'Stage Cleared!' : 'Validation Failed'}
          </span>
          <h3 className="text-2xl sm:text-3xl font-black mt-3 text-white tracking-wide">{activityTitle}</h3>
        </div>

        <div className="grid grid-cols-2 gap-4 bg-sky-950/80 p-5 rounded-2xl border border-sky-700/60 shadow-inner">
          <div>
            <p className="text-[10px] uppercase text-sky-300 font-extrabold tracking-wider">Accuracy Score</p>
            <p className="text-3xl sm:text-4xl font-black text-emerald-400 mt-1">{result.scorePercent}%</p>
          </div>
          <div>
            <p className="text-[10px] uppercase text-sky-300 font-extrabold tracking-wider">Balloons Popped</p>
            <p className="text-3xl sm:text-4xl font-black text-amber-300 mt-1">{result.correctCount} / {result.totalQuestions}</p>
          </div>
        </div>

        {isPassed ? (
          <div className="flex flex-wrap justify-center gap-4 text-sm font-bold pt-2">
            <span className="bg-gradient-to-r from-sky-600 to-blue-600 border border-sky-300 text-white px-5 py-2.5 rounded-full shadow-lg flex items-center gap-2">
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
              🔄 Play Again
            </button>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-b from-sky-950 via-indigo-950 to-slate-950 text-white p-6 sm:p-8 rounded-3xl border-2 border-sky-500/40 shadow-2xl max-w-3xl mx-auto select-none overflow-hidden">
      {/* Dynamic Keyframe Styles for Floating & Pop Particle Effects */}
      <style>{`
        @keyframes balloonFloat {
          0%, 100% { transform: translateX(-50%) translateY(0px) rotate(-2deg); }
          50% { transform: translateX(-50%) translateY(-14px) rotate(2deg); }
        }
        @keyframes popBurst {
          0% { transform: scale(1) opacity(1); }
          50% { transform: scale(1.6) opacity(0.8); }
          100% { transform: scale(2.2) opacity(0); }
        }
      `}</style>

      {/* Arcade Game Header */}
      <div className="flex flex-wrap items-center justify-between pb-4 border-b border-sky-800/80 mb-5 gap-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-sky-500/20 border border-sky-400 flex items-center justify-center text-xl shadow-inner">
            🎈
          </div>
          <div>
            <span className="text-[10px] font-black uppercase tracking-widest text-sky-400 block">
              Floating Arcade Challenge
            </span>
            <h3 className="font-black text-lg text-white">{activityTitle}</h3>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="bg-sky-950/90 border border-sky-500/50 px-4 py-1.5 rounded-full text-xs font-black text-sky-300 shadow flex items-center gap-1.5">
            <span>⏳</span> {timeLeft}s
          </div>
          <div className="text-right text-xs">
            <span className="text-gray-400 block font-semibold text-[10px] uppercase">Progress</span>
            <span className="font-black text-sky-300 text-sm">{currentIndex + 1} / {questions.length}</span>
          </div>
        </div>
      </div>

      {/* Question Banner */}
      <div className="bg-gradient-to-r from-sky-900/90 via-indigo-900/90 to-blue-900/90 p-5 rounded-2xl border border-sky-400/40 text-center mb-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-24 h-24 bg-sky-400/10 rounded-full blur-xl pointer-events-none"></div>
        <p className="text-[11px] text-sky-300 font-extrabold uppercase tracking-widest mb-1">
          Pop the balloon containing the correct answer
        </p>
        <h4 className="text-xl sm:text-2xl font-black text-white">{currentQuestion.prompt}</h4>
      </div>

      {/* Floating Balloon Sky Arena */}
      <div className="relative w-full h-80 sm:h-96 bg-gradient-to-b from-sky-950/80 via-indigo-950/70 to-slate-950/90 rounded-2xl border-2 border-dashed border-sky-400/30 overflow-hidden shadow-inner flex items-center justify-center">
        {/* Sky Cloud Graphics */}
        <div className="absolute top-4 left-6 text-3xl opacity-15 pointer-events-none">☁️</div>
        <div className="absolute top-10 right-12 text-4xl opacity-15 pointer-events-none">☁️</div>
        <div className="absolute bottom-6 left-1/3 text-2xl opacity-15 pointer-events-none">☁️</div>

        {balloons.map((b) => {
          const isPopped = poppedId === b.id;
          const floatDuration = b.speedSec;

          return (
            <div
              key={b.id}
              style={{
                left: `${b.leftPercent}%`,
                animation: isPopped ? 'none' : `balloonFloat ${floatDuration}s ease-in-out infinite`
              }}
              className="absolute bottom-6 transform -translate-x-1/2 flex flex-col items-center z-10"
            >
              <button
                onClick={() => handlePopBalloon(b)}
                disabled={poppedId !== null}
                className={`relative group w-24 h-32 sm:w-28 sm:h-36 rounded-[50%_50%_50%_50%/40%_40%_60%_60%] bg-gradient-to-tr ${b.colorGradient} ${b.borderColor} border-2 text-white shadow-2xl flex flex-col items-center justify-center transition-all duration-300 ${b.glowColor} ${
                  isPopped
                    ? b.isCorrect
                      ? 'scale-150 opacity-0 transition-all duration-500'
                      : 'scale-90 bg-red-600 border-red-400'
                    : 'hover:scale-110 active:scale-95 cursor-pointer'
                }`}
              >
                {/* 3D Balloon Gloss Reflection */}
                <div className="absolute top-3 left-4 w-6 h-8 bg-white/30 rounded-full blur-[1px] transform -rotate-45 pointer-events-none"></div>

                <span className="font-black text-xs sm:text-sm text-center px-2 z-10 drop-shadow-md text-white">
                  {b.text}
                </span>

                {/* Knot at bottom of balloon */}
                <div className="absolute -bottom-2 w-3 h-3 bg-indigo-900 border border-white/40 rotate-45 rounded-sm"></div>
              </button>

              {/* String */}
              <div className="w-0.5 h-12 bg-sky-200/50 mt-2 shadow"></div>
            </div>
          );
        })}
      </div>

      {/* Footer Instructions */}
      <div className="mt-4 flex items-center justify-between text-xs text-sky-300 font-semibold px-2">
        <span className="flex items-center gap-1.5">
          <span>🎯</span> Tap balloon to answer!
        </span>
        <span>Question {currentIndex + 1} of {questions.length}</span>
      </div>
    </div>
  );
};
