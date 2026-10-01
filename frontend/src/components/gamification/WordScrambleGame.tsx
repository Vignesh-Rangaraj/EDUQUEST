import React, { useState } from 'react';
import { gameEngineService } from '../../services/gameEngineService';

interface ScrambleQuestion {
  id?: string;
  prompt?: string;
  word?: string;
  correctAnswer: string;
  hint?: string;
}

interface WordScrambleGameProps {
  activityId: number;
  activityTitle: string;
  config?: any;
  onComplete?: (result: any) => void;
}

export const WordScrambleGame: React.FC<WordScrambleGameProps> = ({
  activityId,
  activityTitle,
  config,
  onComplete
}) => {
  const rawWords = config?.words || config?.questions || config?.items || [
    { word: 'NEGOXY', correctAnswer: 'OXYGEN', hint: 'Gas essential for breathing' },
    { word: 'TANELP', correctAnswer: 'PLANET', hint: 'Celestial body orbiting a star' },
    { word: 'YTIVARG', correctAnswer: 'GRAVITY', hint: 'Force pulling objects down' },
    { word: 'CLLE', correctAnswer: 'CELL', hint: 'Basic unit of life' }
  ];

  const questions: ScrambleQuestion[] = rawWords.map((w: any) => {
    const correctAnswer = (w.target || w.correctAnswer || w.word || '').toString().toUpperCase().trim();
    let word = (w.scrambled || w.word || w.prompt || '').toString().toUpperCase().trim();
    if (!word || word === correctAnswer) {
      word = correctAnswer.split('').sort(() => Math.random() - 0.5).join('');
    }
    const hint = w.hint || w.prompt || w.questionText || '';
    return { word, correctAnswer, hint, prompt: hint };
  });

  const [currentIndex, setCurrentIndex] = useState(0);
  const [userAnswers, setUserAnswers] = useState<Record<number, string>>({});
  const [showHint, setShowHint] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [gameResult, setGameResult] = useState<any>(null);

  const currentQ = questions[currentIndex];
  const scrambledStr = currentQ.word || currentQ.prompt || currentQ.correctAnswer.split('').sort(() => Math.random() - 0.5).join('');

  const isAllAnswered = questions.length > 0 && questions.every((_, idx) => (userAnswers[idx] || '').trim().length > 0);

  const handleInputChange = (val: string) => {
    if (submitted) return;
    setUserAnswers((prev) => ({
      ...prev,
      [currentIndex]: val.toUpperCase()
    }));
  };

  const handleNextQuestion = () => {
    if (currentIndex + 1 < questions.length) {
      setCurrentIndex((prev) => prev + 1);
      setShowHint(false);
    }
  };

  const handlePrevQuestion = () => {
    if (currentIndex > 0) {
      setCurrentIndex((prev) => prev - 1);
      setShowHint(false);
    }
  };

  const handleSubmit = async () => {
    if (!isAllAnswered) return;
    setSubmitted(true);

    try {
      const res = await gameEngineService.submitGameAnswers(activityId, userAnswers);
      setGameResult(res);
      if (onComplete) onComplete(res);
    } catch (e) {
      let correct = 0;
      questions.forEach((q, idx) => {
        if ((userAnswers[idx] || '').trim().toUpperCase() === q.correctAnswer.trim().toUpperCase()) {
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
    setUserAnswers({});
    setSubmitted(false);
    setGameResult(null);
    setCurrentIndex(0);
    setShowHint(false);
  };

  return (
    <div className="bg-gradient-to-b from-purple-950 via-indigo-950 to-slate-950 text-white p-6 sm:p-8 rounded-3xl border-2 border-purple-500/40 shadow-2xl max-w-3xl mx-auto select-none">
      {/* Header Bar */}
      <div className="flex flex-wrap items-center justify-between pb-4 border-b border-purple-800/80 mb-5 gap-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-purple-500/20 border border-purple-400 flex items-center justify-center text-xl shadow-inner">
            🔤
          </div>
          <div>
            <span className="text-[10px] font-black uppercase tracking-widest text-purple-400 block">
              Arcade Word Unscrambler
            </span>
            <h3 className="font-black text-lg text-white">{activityTitle}</h3>
          </div>
        </div>
        <div className="text-right text-xs">
          <span className="text-gray-400 block font-semibold text-[10px] uppercase">Word Index</span>
          <span className="font-black text-purple-300 text-sm">{currentIndex + 1} / {questions.length}</span>
        </div>
      </div>

      {/* Scrambled Word Console */}
      <div className="bg-gradient-to-r from-purple-900/90 via-indigo-900/90 to-slate-900/90 p-6 rounded-2xl border border-purple-400/40 text-center mb-6 shadow-xl relative overflow-hidden">
        <span className="text-[10px] font-black uppercase tracking-widest text-purple-300 block mb-3">
          Unscramble The Letter Keycaps
        </span>

        <div className="flex justify-center gap-2 sm:gap-3 flex-wrap my-2">
          {scrambledStr.split('').map((char, idx) => (
            <div
              key={idx}
              className="w-11 h-14 sm:w-12 sm:h-16 bg-gradient-to-b from-purple-600 via-indigo-600 to-purple-800 border-2 border-purple-300 rounded-2xl flex items-center justify-center font-black text-2xl text-yellow-300 shadow-xl transform transition-transform hover:-translate-y-1 hover:brightness-110"
            >
              <span className="drop-shadow-md">{char}</span>
            </div>
          ))}
        </div>

        {currentQ.hint && (
          <div className="mt-4 pt-3 border-t border-purple-800/60">
            <button
              onClick={() => setShowHint(!showHint)}
              className="text-xs font-bold text-purple-200 hover:text-yellow-300 underline transition-colors cursor-pointer flex items-center justify-center gap-1.5 mx-auto"
            >
              <span>💡</span> {showHint ? `Hint: ${currentQ.hint}` : 'Click for a Clue Hint'}
            </button>
          </div>
        )}
      </div>

      {/* User Input Slot */}
      <div className="mb-6 space-y-2">
        <label className="block text-xs font-extrabold text-purple-300 uppercase tracking-wider text-center">
          Type Your Answer Below
        </label>
        <input
          type="text"
          placeholder="TYPE UNSCRAMBLED WORD..."
          value={userAnswers[currentIndex] || ''}
          onChange={(e) => handleInputChange(e.target.value)}
          disabled={submitted}
          className="w-full px-6 py-4 bg-slate-900 border-2 border-purple-400/60 rounded-2xl text-center font-black text-xl sm:text-2xl tracking-widest text-yellow-300 uppercase focus:border-purple-300 focus:ring-4 focus:ring-purple-500/30 focus:outline-none shadow-inner"
        />
      </div>

      {/* Navigation Buttons */}
      <div className="flex justify-between items-center gap-4 mb-6">
        <button
          onClick={handlePrevQuestion}
          disabled={currentIndex === 0}
          className="px-5 py-2.5 text-xs font-black rounded-xl border border-purple-700/60 bg-purple-950/60 text-purple-200 hover:bg-purple-900 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
        >
          ⬅️ Prev Word
        </button>
        <span className="text-xs font-bold text-gray-400">
          Answered: {Object.keys(userAnswers).filter(k => userAnswers[Number(k)]?.trim()).length} / {questions.length}
        </span>
        <button
          onClick={handleNextQuestion}
          disabled={currentIndex === questions.length - 1}
          className="px-5 py-2.5 text-xs font-black rounded-xl border border-purple-700/60 bg-purple-950/60 text-purple-200 hover:bg-purple-900 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
        >
          Next Word ➡️
        </button>
      </div>

      {/* Submit / Results */}
      {!submitted ? (
        <button
          onClick={handleSubmit}
          disabled={!isAllAnswered}
          className={`w-full py-4 font-black rounded-2xl shadow-xl uppercase tracking-wider text-sm transition-all ${
            isAllAnswered
              ? 'bg-gradient-to-r from-purple-500 via-indigo-600 to-purple-600 text-white hover:brightness-110 cursor-pointer transform hover:scale-[1.01]'
              : 'bg-slate-900 text-slate-600 border border-slate-800 cursor-not-allowed'
          }`}
        >
          {isAllAnswered ? 'Submit Scramble Challenge 🚀' : 'Unscramble All Words To Enable Submit'}
        </button>
      ) : (
        <div className={`p-6 rounded-2xl border text-center animate-fade-in ${
          (gameResult?.scorePercent || 0) >= 70 ? 'bg-emerald-950/80 border-emerald-500/60' : 'bg-red-950/80 border-red-500/60'
        }`}>
          {(gameResult?.scorePercent || 0) >= 70 ? (
            <>
              <h4 className="font-black text-xl text-emerald-300 mb-1">🎉 Word Master Victory!</h4>
              <p className="text-sm font-semibold text-emerald-200">
                Score: {gameResult?.scorePercent}% ({gameResult?.correctCount || questions.length}/{questions.length} Correct)
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
