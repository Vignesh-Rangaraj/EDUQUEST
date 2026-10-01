import React, { useState, useEffect } from 'react';
import { gameEngineService } from '../../services/gameEngineService';

interface PairQuestion {
  id?: string;
  prompt: string;
  correctAnswer: string;
  options?: string[];
}

interface MatchTheFollowingGameProps {
  activityId: number;
  activityTitle: string;
  config?: any;
  onComplete?: (result: any) => void;
}

export const MatchTheFollowingGame: React.FC<MatchTheFollowingGameProps> = ({
  activityId,
  activityTitle,
  config,
  onComplete
}) => {
  const rawPairs = config?.pairs || config?.questions || config?.items || [
    { prompt: 'Heart', correctAnswer: 'Pumps Blood' },
    { prompt: 'Lungs', correctAnswer: 'Breathing' },
    { prompt: 'Brain', correctAnswer: 'Controls Body' },
    { prompt: 'Stomach', correctAnswer: 'Digestion' }
  ];

  const questions: PairQuestion[] = rawPairs.map((p: any) => ({
    prompt: (p.left || p.prompt || p.question || p.questionText || 'Item').toString(),
    correctAnswer: (p.right || p.correctAnswer || p.answer || '').toString()
  }));

  const [shuffledAnswers, setShuffledAnswers] = useState<string[]>([]);
  const [selectedLeft, setSelectedLeft] = useState<number | null>(null);
  const [userPairs, setUserPairs] = useState<Record<number, string>>({});
  const [submitted, setSubmitted] = useState(false);
  const [gameResult, setGameResult] = useState<any>(null);

  useEffect(() => {
    const answers = questions.map((q) => q.correctAnswer);
    const shuffled = [...answers].sort(() => Math.random() - 0.5);
    setShuffledAnswers(shuffled);
    setUserPairs({});
    setSubmitted(false);
    setGameResult(null);
  }, [activityId, config]);

  const handleSelectLeft = (leftIdx: number) => {
    if (submitted) return;
    setSelectedLeft(leftIdx);
  };

  const handleSelectRight = (rightAnswer: string) => {
    if (submitted || selectedLeft === null) return;
    setUserPairs((prev) => ({
      ...prev,
      [selectedLeft]: rightAnswer
    }));
    setSelectedLeft(null);
  };

  const handleRemovePair = (leftIdx: number) => {
    if (submitted) return;
    setUserPairs((prev) => {
      const copy = { ...prev };
      delete copy[leftIdx];
      return copy;
    });
  };

  const isAllMatched = questions.length > 0 && Object.keys(userPairs).length === questions.length;

  const handleSubmit = async () => {
    if (!isAllMatched) return;
    setSubmitted(true);

    try {
      const res = await gameEngineService.submitGameAnswers(activityId, userPairs);
      setGameResult(res);
      if (onComplete) onComplete(res);
    } catch (e) {
      let correct = 0;
      questions.forEach((q, idx) => {
        if ((userPairs[idx] || '').trim().toLowerCase() === q.correctAnswer.trim().toLowerCase()) {
          correct++;
        }
      });
      const score = Math.round((correct / questions.length) * 100);
      const passed = score >= 70;
      const fallback = {
        success: passed,
        passed,
        correctCount: correct,
        totalQuestions: questions.length,
        scorePercent: score,
        xpEarned: passed ? 50 : 0,
        coinsEarned: passed ? 10 : 0
      };
      setGameResult(fallback);
      if (onComplete) onComplete(fallback);
    }
  };

  const handleRetry = () => {
    setUserPairs({});
    setSubmitted(false);
    setGameResult(null);
    setSelectedLeft(null);
  };

  return (
    <div className="bg-gradient-to-b from-slate-950 via-indigo-950 to-slate-900 text-white p-6 sm:p-8 rounded-3xl border-2 border-indigo-500/40 shadow-2xl max-w-3xl mx-auto select-none">
      {/* Header Bar */}
      <div className="flex flex-wrap items-center justify-between pb-4 border-b border-indigo-800/80 mb-5 gap-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-indigo-500/20 border border-indigo-400 flex items-center justify-center text-xl shadow-inner">
            🧩
          </div>
          <div>
            <span className="text-[10px] font-black uppercase tracking-widest text-indigo-400 block">
              Cyber Match Matrix
            </span>
            <h3 className="font-black text-lg text-white">{activityTitle}</h3>
          </div>
        </div>
        <div className="text-right text-xs">
          <span className="text-gray-400 block font-semibold text-[10px] uppercase">Matched</span>
          <span className="font-black text-indigo-300 text-sm">
            {Object.keys(userPairs).length} / {questions.length} Pairs
          </span>
        </div>
      </div>

      <p className="text-xs text-indigo-300 mb-6 font-semibold bg-indigo-950/60 p-3 rounded-xl border border-indigo-700/50">
        💡 {config?.instructions || 'Tap an item in Column A, then tap its corresponding match in Column B:'}
      </p>

      {/* Grid Columns */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-6">
        {/* Left Column (Prompts) */}
        <div className="space-y-3">
          <h4 className="text-[11px] font-black uppercase tracking-wider text-indigo-400 mb-2 flex items-center gap-1.5">
            <span>🔹</span> Column A (Concepts)
          </h4>
          {questions.map((q, idx) => {
            const pairedVal = userPairs[idx];
            const isSelected = selectedLeft === idx;
            const isCorrect = submitted && pairedVal?.trim().toLowerCase() === q.correctAnswer.trim().toLowerCase();
            const isWrong = submitted && pairedVal && !isCorrect;

            let borderStyle = 'border-slate-800 bg-slate-900/90 text-gray-200 hover:border-indigo-500/60';
            if (isSelected) borderStyle = 'border-indigo-400 bg-indigo-900/80 text-white ring-4 ring-indigo-500/30 scale-[1.02] shadow-xl';
            if (submitted) {
              if (isCorrect) borderStyle = 'border-emerald-500 bg-emerald-950/80 text-emerald-200 font-bold';
              else if (isWrong) borderStyle = 'border-red-500 bg-red-950/80 text-red-200 font-bold';
            }

            return (
              <div
                key={idx}
                onClick={() => handleSelectLeft(idx)}
                className={`p-4 rounded-2xl border-2 transition-all cursor-pointer flex flex-col justify-between shadow-lg ${borderStyle}`}
              >
                <div className="flex items-center justify-between">
                  <span className="font-black text-xs sm:text-sm">{idx + 1}. {q.prompt}</span>
                  {pairedVal && !submitted && (
                    <button
                      onClick={(e) => { e.stopPropagation(); handleRemovePair(idx); }}
                      className="text-[10px] text-red-400 font-black hover:underline uppercase bg-red-950/60 px-2 py-0.5 rounded border border-red-800"
                    >
                      Clear
                    </button>
                  )}
                </div>
                {pairedVal && (
                  <div className={`mt-2.5 text-xs px-3 py-1.5 rounded-xl border font-black flex items-center gap-1.5 ${
                    submitted ? (isCorrect ? 'bg-emerald-900/80 text-emerald-200 border-emerald-500' : 'bg-red-900/80 text-red-200 border-red-500') : 'bg-indigo-950/80 text-indigo-300 border-indigo-500/50'
                  }`}>
                    <span>➔ {pairedVal}</span>
                    {submitted && (isCorrect ? ' ✅' : ' ❌')}
                  </div>
                )}
              </div>
            );
          })}
        </div>

        {/* Right Column (Shuffled Answers) */}
        <div className="space-y-3">
          <h4 className="text-[11px] font-black uppercase tracking-wider text-indigo-400 mb-2 flex items-center gap-1.5">
            <span>🔸</span> Column B (Matches)
          </h4>
          {shuffledAnswers.map((ans, idx) => {
            const isAlreadyPaired = Object.values(userPairs).includes(ans);
            return (
              <button
                key={idx}
                onClick={() => handleSelectRight(ans)}
                disabled={submitted || isAlreadyPaired || selectedLeft === null}
                className={`w-full p-4 rounded-2xl border-2 text-left font-black text-xs sm:text-sm transition-all shadow-md ${
                  isAlreadyPaired
                    ? 'border-slate-800 bg-slate-900/50 text-slate-600 cursor-not-allowed opacity-50'
                    : selectedLeft !== null
                    ? 'border-indigo-400 bg-indigo-900/40 text-white hover:bg-indigo-600 hover:text-white hover:border-indigo-300 shadow-indigo-500/30 cursor-pointer transform hover:scale-[1.02]'
                    : 'border-slate-800 bg-slate-900/80 text-gray-300 cursor-default'
                }`}
              >
                {ans}
              </button>
            );
          })}
        </div>
      </div>

      {/* Action Buttons */}
      {!submitted ? (
        <button
          onClick={handleSubmit}
          disabled={!isAllMatched}
          className={`w-full py-4 font-black rounded-2xl shadow-xl uppercase tracking-wider text-sm transition-all ${
            isAllMatched
              ? 'bg-gradient-to-r from-indigo-500 via-purple-600 to-indigo-600 text-white hover:brightness-110 cursor-pointer transform hover:scale-[1.01]'
              : 'bg-slate-900 text-slate-600 border border-slate-800 cursor-not-allowed'
          }`}
        >
          {isAllMatched ? 'Submit Matching Challenge 🚀' : 'Match All Pairs To Enable Submit'}
        </button>
      ) : (
        <div className={`p-6 rounded-2xl border text-center animate-fade-in ${
          (gameResult?.scorePercent || 0) >= 70 ? 'bg-emerald-950/80 border-emerald-500/60' : 'bg-red-950/80 border-red-500/60'
        }`}>
          {(gameResult?.scorePercent || 0) >= 70 ? (
            <>
              <h4 className="font-black text-xl text-emerald-300 mb-1">🎉 Excellent Matching Victory!</h4>
              <p className="text-sm font-semibold text-emerald-200">
                Score: {gameResult?.scorePercent}% ({gameResult?.correctCount || questions.length}/{questions.length} Matches Correct)
              </p>
              <div className="mt-4 flex justify-center gap-4 text-xs font-black">
                <span className="bg-emerald-800/80 text-emerald-200 px-4 py-2 rounded-full border border-emerald-500">
                  +{gameResult?.xpEarned || 50} XP
                </span>
                <span className="bg-amber-500/80 text-slate-950 px-4 py-2 rounded-full border border-amber-300">
                  +{gameResult?.coinsEarned || 10} 🪙 Gold
                </span>
              </div>
            </>
          ) : (
            <>
              <h4 className="font-black text-xl text-red-300 mb-1">❌ Validation Failed</h4>
              <p className="text-sm font-semibold text-red-200 mb-3">
                Score: {gameResult?.scorePercent}% ({gameResult?.correctCount || 0}/{questions.length} Correct). Minimum passing score is 70%.
              </p>
              <button
                onClick={handleRetry}
                className="px-6 py-2.5 bg-red-600 hover:bg-red-700 text-white font-black text-xs uppercase tracking-wider rounded-xl shadow cursor-pointer"
              >
                🔄 Try Again
              </button>
            </>
          )}
        </div>
      )}
    </div>
  );
};
