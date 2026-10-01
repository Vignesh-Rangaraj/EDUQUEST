import React, { useState, useEffect } from 'react';
import { gameEngineService } from '../../services/gameEngineService';
import { MatchTheFollowingGame } from './MatchTheFollowingGame';
import { FlashCardGame } from './FlashCardGame';
import { WordScrambleGame } from './WordScrambleGame';
import { ShootTheAnswerGame } from './ShootTheAnswerGame';
import { BalloonPopGame } from './BalloonPopGame';
import { TreasureHuntGame } from './TreasureHuntGame';

interface MiniGamePlayerProps {
  activityId: number;
  activityTitle: string;
  activityType?: string;
  onComplete?: (result: any) => void;
}

export const MiniGamePlayer: React.FC<MiniGamePlayerProps> = ({
  activityId,
  activityTitle,
  activityType: propActivityType,
  onComplete
}) => {
  const [config, setConfig] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userAnswers, setUserAnswers] = useState<Record<number, string>>({});
  const [submitted, setSubmitted] = useState(false);
  const [gameResult, setGameResult] = useState<any>(null);

  useEffect(() => {
    const fetchConfig = async () => {
      setLoading(true);
      try {
        const data = await gameEngineService.getGameConfig(activityId);
        let parsed = data;
        if (data && data.jsonConfiguration) {
          parsed = typeof data.jsonConfiguration === 'string' ? JSON.parse(data.jsonConfiguration) : data.jsonConfiguration;
        }
        setConfig(parsed);
      } catch (err: any) {
        const gameType = propActivityType || 'MATCH_THE_FOLLOWING';
        setConfig({
          gameType,
          instructions: `Complete the ${gameType.replace(/_/g, ' ')} challenge:`,
          questions: [
            { prompt: 'The Sun is a star.', correctAnswer: 'True', options: ['True', 'False'] },
            { prompt: 'Humans can breathe underwater without equipment.', correctAnswer: 'False', options: ['True', 'False'] },
            { prompt: 'Plants produce oxygen during photosynthesis.', correctAnswer: 'True', options: ['True', 'False'] }
          ]
        });
      } finally {
        setLoading(false);
      }
    };
    fetchConfig();
  }, [activityId, propActivityType]);

  if (loading) {
    return (
      <div className="p-8 text-center text-sm font-black text-indigo-400 bg-slate-950 rounded-3xl border border-indigo-500/30 max-w-2xl mx-auto shadow-2xl flex items-center justify-center gap-3">
        <span className="w-5 h-5 border-2 border-indigo-400 border-t-transparent rounded-full animate-spin"></span>
        Initializing Game Engine...
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8 text-center text-sm font-black text-red-400 bg-slate-950 rounded-3xl border border-red-500/30 max-w-2xl mx-auto shadow-2xl">
        {error}
      </div>
    );
  }

  const resolvedGameType = config?.gameType || propActivityType || 'MATCH_THE_FOLLOWING';

  // Dedicated component routing for specialized game engines
  switch (resolvedGameType) {
    case 'MATCH_THE_FOLLOWING':
      return <MatchTheFollowingGame activityId={activityId} activityTitle={activityTitle} config={config} onComplete={onComplete} />;
    case 'FLASH_CARDS':
      return <FlashCardGame activityId={activityId} activityTitle={activityTitle} config={config} onComplete={onComplete} />;
    case 'WORD_SCRAMBLE':
      return <WordScrambleGame activityId={activityId} activityTitle={activityTitle} config={config} onComplete={onComplete} />;
    case 'SHOOT_THE_ANSWER':
      return <ShootTheAnswerGame activityId={activityId} activityTitle={activityTitle} config={config} onComplete={onComplete} />;
    case 'BALLOON_POP':
      return <BalloonPopGame activityId={activityId} activityTitle={activityTitle} config={config} onComplete={onComplete} />;
    case 'TREASURE_HUNT':
      return <TreasureHuntGame activityId={activityId} activityTitle={activityTitle} config={config} onComplete={onComplete} />;
  }

  // Generic Arcade Renderer for TRUE_FALSE and FILL_IN_THE_BLANK
  const questions = config?.questions || [];

  const isAllAnswered = resolvedGameType === 'TRUE_FALSE'
    ? questions.length > 0 && Object.keys(userAnswers).length === questions.length
    : questions.length > 0 && questions.every((_: any, idx: number) => (userAnswers[idx] || '').trim().length > 0);

  const handleSelectAnswer = (index: number, val: string) => {
    if (submitted) return;
    setUserAnswers((prev) => ({ ...prev, [index]: val }));
  };

  const handleSubmit = async () => {
    if (!isAllAnswered) return;
    setSubmitted(true);
    try {
      const result = await gameEngineService.submitGameAnswers(activityId, userAnswers);
      setGameResult(result);
      if (onComplete) onComplete(result);
    } catch (e) {
      let correct = 0;
      questions.forEach((q: any, idx: number) => {
        if (q.correctAnswer && (userAnswers[idx] || '').trim().toLowerCase() === q.correctAnswer.trim().toLowerCase()) {
          correct++;
        }
      });
      const score = Math.round((correct / (questions.length || 1)) * 100);
      const passed = score >= 70;
      const fallbackRes = {
        success: passed,
        passed,
        correctCount: correct,
        totalQuestions: questions.length,
        scorePercent: score,
        xpEarned: passed ? 50 : 0,
        coinsEarned: passed ? 10 : 0
      };
      setGameResult(fallbackRes);
      if (onComplete) onComplete(fallbackRes);
    }
  };

  const handleRetry = () => {
    setUserAnswers({});
    setSubmitted(false);
    setGameResult(null);
  };

  return (
    <div className="bg-gradient-to-b from-slate-950 via-indigo-950 to-slate-900 text-white rounded-3xl shadow-2xl border-2 border-indigo-500/40 p-6 sm:p-8 max-w-3xl mx-auto select-none">
      {/* Header Bar */}
      <div className="flex flex-wrap items-center justify-between pb-4 border-b border-indigo-800/80 mb-6 gap-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-indigo-500/20 border border-indigo-400 flex items-center justify-center text-xl shadow-inner">
            🎮
          </div>
          <div>
            <span className="text-[10px] font-black uppercase tracking-widest text-indigo-400 block">
              {resolvedGameType.replace(/_/g, ' ')}
            </span>
            <h3 className="font-black text-lg text-white">{activityTitle}</h3>
          </div>
        </div>
        <div className="text-right">
          <span className="text-xs text-gray-400 block font-semibold text-[10px] uppercase">Reward Target</span>
          <span className="font-black text-sm text-indigo-300">+50 XP | +10 🪙</span>
        </div>
      </div>

      <p className="text-xs text-indigo-300 font-semibold mb-6 bg-indigo-950/60 p-3 rounded-xl border border-indigo-700/50">
        💡 {config?.instructions || 'Complete the challenge below:'}
      </p>

      <div className="space-y-4 mb-8">
        {questions.map((q: any, idx: number) => {
          const userVal = userAnswers[idx] || '';
          const isCorrect = submitted && userVal.trim().toLowerCase() === (q.correctAnswer || '').trim().toLowerCase();
          const isWrong = submitted && userVal && !isCorrect;

          return (
            <div key={idx} className={`p-4 sm:p-5 rounded-2xl border-2 transition-all shadow-md ${
              submitted
                ? (isCorrect ? 'border-emerald-500 bg-emerald-950/80 text-emerald-200' : 'border-red-500 bg-red-950/80 text-red-200')
                : 'border-slate-800 bg-slate-900/80 text-gray-200'
            }`}>
              <div className="flex justify-between items-center mb-3">
                <p className="font-black text-sm text-white">
                  {idx + 1}. {q.prompt || q.questionText || q.word}
                </p>
                {submitted && (
                  <span className={`text-[10px] font-black px-3 py-1 rounded-full border ${
                    isCorrect ? 'bg-emerald-900/90 text-emerald-200 border-emerald-500' : 'bg-red-900/90 text-red-200 border-red-500'
                  }`}>
                    {isCorrect ? 'Correct ✅' : `Expected: ${q.correctAnswer} ❌`}
                  </span>
                )}
              </div>

              {resolvedGameType === 'TRUE_FALSE' ? (
                <div className="flex gap-3">
                  {['True', 'False'].map((opt) => (
                    <button
                      key={opt}
                      onClick={() => handleSelectAnswer(idx, opt)}
                      disabled={submitted}
                      className={`flex-1 py-3 px-4 rounded-xl font-black text-xs transition-all shadow-md cursor-pointer ${
                        userAnswers[idx] === opt
                          ? 'bg-gradient-to-r from-indigo-500 to-purple-600 text-white border-2 border-indigo-300 scale-[1.02]'
                          : 'bg-slate-800 border border-slate-700 text-gray-300 hover:bg-slate-700'
                      }`}
                    >
                      {opt}
                    </button>
                  ))}
                </div>
              ) : (
                <input
                  type="text"
                  placeholder="Type answer here..."
                  value={userAnswers[idx] || ''}
                  onChange={(e) => handleSelectAnswer(idx, e.target.value)}
                  disabled={submitted}
                  className="w-full px-4 py-3 bg-slate-900 border border-slate-700 rounded-xl text-sm font-bold text-white focus:border-indigo-500 focus:outline-none"
                />
              )}
            </div>
          );
        })}
      </div>

      {!submitted ? (
        <button
          onClick={handleSubmit}
          disabled={!isAllAnswered}
          className={`w-full py-4 font-black rounded-2xl shadow-xl uppercase tracking-wider text-sm transition-all ${
            isAllAnswered
              ? 'bg-gradient-to-r from-indigo-500 via-purple-600 to-indigo-600 text-white hover:brightness-110 cursor-pointer transform hover:scale-[1.01]'
              : 'bg-slate-900 text-slate-600 border border-slate-800 cursor-not-allowed'
          }`}
        >
          {isAllAnswered ? 'Submit Challenge 🚀' : 'Answer All Questions To Enable Submit'}
        </button>
      ) : (
        <div className={`p-6 rounded-2xl border text-center animate-fade-in ${
          (gameResult?.scorePercent || 0) >= 70 ? 'bg-emerald-950/80 border-emerald-500/60' : 'bg-red-950/80 border-red-500/60'
        }`}>
          {(gameResult?.scorePercent || 0) >= 70 ? (
            <>
              <h4 className="font-black text-xl text-emerald-300 mb-1">🎉 Challenge Completed!</h4>
              <p className="text-sm font-semibold text-emerald-200">
                Score: {gameResult?.scorePercent || 100}% ({gameResult?.correctCount || questions.length}/{questions.length} Correct)
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
                Score: {gameResult?.scorePercent || 0}% ({gameResult?.correctCount || 0}/{questions.length} Correct). Minimum passing score is 70%.
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
