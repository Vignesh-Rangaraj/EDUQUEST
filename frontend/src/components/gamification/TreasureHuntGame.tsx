import React, { useState } from 'react';
import { gameEngineService } from '../../services/gameEngineService';

interface TreasureStage {
  stageIndex: number;
  clue: string;
  question: string;
  options: string[];
  correctAnswer: string;
}

interface TreasureHuntGameProps {
  activityId: number;
  activityTitle: string;
  config?: any;
  onComplete?: (result: any) => void;
}

export const TreasureHuntGame: React.FC<TreasureHuntGameProps> = ({
  activityId,
  activityTitle,
  config,
  onComplete
}) => {
  const rawStages = config?.stages || config?.questions || config?.items || [
    {
      stageIndex: 1,
      clue: '📜 Clue #1: Search in the green leaves of the plant where food is prepared.',
      question: 'Which process occurs in green leaves to make food?',
      options: ['Respiration', 'Photosynthesis', 'Transpiration', 'Evaporation'],
      correctAnswer: 'Photosynthesis'
    },
    {
      stageIndex: 2,
      clue: '🗝️ Clue #2: Follow the water channels flowing from roots up through the stem.',
      question: 'Which plant tissue transports water from roots to leaves?',
      options: ['Phloem', 'Xylem', 'Stomata', 'Epidermis'],
      correctAnswer: 'Xylem'
    },
    {
      stageIndex: 3,
      clue: '🏆 Final Map Key: Unlock the Golden Treasure Chest!',
      question: 'Which gas is released by plants during photosynthesis?',
      options: ['Carbon Dioxide', 'Oxygen', 'Nitrogen', 'Methane'],
      correctAnswer: 'Oxygen'
    }
  ];

  const stages: TreasureStage[] = rawStages.map((s: any, idx: number) => {
    const clue = (s.clue || s.prompt || s.question || s.questionText || `Clue #${idx + 1}`).toString();
    const question = (s.question || s.prompt || s.clue || s.questionText || 'Answer the clue to proceed:').toString();
    const correctAnswer = (s.correctAnswer || s.target || s.answer || '').toString();
    let options: string[] = Array.isArray(s.options) ? s.options.map((o: any) => o.toString()) : [];
    if (correctAnswer && options.length > 0 && !options.some(o => o.trim().toLowerCase() === correctAnswer.trim().toLowerCase())) {
      options.push(correctAnswer);
    }
    return {
      stageIndex: idx + 1,
      clue,
      question,
      options,
      correctAnswer
    };
  });

  const [currentStageIndex, setCurrentStageIndex] = useState(0);
  const [userAnswers, setUserAnswers] = useState<Record<number, string>>({});
  const [selectedOption, setSelectedOption] = useState<string>('');
  const [completed, setCompleted] = useState(false);
  const [chestOpened, setChestOpened] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [stageError, setStageError] = useState<string | null>(null);

  const currentStage = stages[currentStageIndex];

  const handleNextStage = () => {
    if (!selectedOption) return;
    setStageError(null);

    const isCorrect = selectedOption.trim().toLowerCase() === currentStage.correctAnswer.trim().toLowerCase();
    const newAnswers = { ...userAnswers, [currentStageIndex]: selectedOption };
    setUserAnswers(newAnswers);

    if (!isCorrect) {
      setStageError(`❌ Incorrect! "${selectedOption}" is not the right answer for this clue. Try again!`);
      return;
    }

    setSelectedOption('');
    if (currentStageIndex + 1 < stages.length) {
      setCurrentStageIndex((prev) => prev + 1);
    } else {
      finishHunt(newAnswers);
    }
  };

  const finishHunt = async (answers: Record<number, string>) => {
    setCompleted(true);
    let correct = 0;
    stages.forEach((s, idx) => {
      if ((answers[idx] || '').trim().toLowerCase() === s.correctAnswer.trim().toLowerCase()) {
        correct++;
      }
    });

    const scorePercent = Math.round((correct / stages.length) * 100);
    const passed = scorePercent >= 70;
    const xpEarned = passed ? Math.round((scorePercent / 100) * 60) : 0;
    const coinsEarned = passed ? (scorePercent >= 80 ? 15 : 8) : 0;

    try {
      const res = await gameEngineService.submitGameAnswers(activityId, {
        gameType: 'TREASURE_HUNT',
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
        gameType: 'TREASURE_HUNT',
        correctCount: correct,
        totalQuestions: stages.length,
        scorePercent,
        xpEarned,
        coinsEarned
      };
      setResult(fallback);
      if (onComplete) onComplete(fallback);
    }
  };

  if (completed && result) {
    return (
      <div className="bg-gradient-to-b from-amber-950 via-yellow-950 to-slate-950 text-white p-8 sm:p-10 rounded-3xl shadow-2xl border-2 border-amber-500/50 max-w-2xl mx-auto text-center space-y-6">
        <div className="relative inline-block">
          <button
            onClick={() => setChestOpened(true)}
            className={`w-32 h-32 mx-auto rounded-3xl bg-gradient-to-br from-amber-400 via-yellow-500 to-amber-600 flex items-center justify-center text-6xl border-4 border-amber-200 shadow-2xl transition-all duration-500 ${
              chestOpened ? 'scale-110 rotate-3 ring-8 ring-yellow-400/50' : 'animate-bounce cursor-pointer hover:scale-105'
            }`}
          >
            {chestOpened ? '💎' : '🎁'}
          </button>
          {!chestOpened && (
            <div className="absolute -top-3 left-1/2 transform -translate-x-1/2 bg-yellow-400 text-slate-950 text-[10px] font-black px-3 py-1 rounded-full shadow-lg border border-white animate-pulse">
              CLICK TO OPEN CHEST!
            </div>
          )}
        </div>

        <div>
          <span className="text-xs font-black uppercase tracking-widest text-amber-300 bg-amber-950/80 px-4 py-1.5 rounded-full border border-amber-600 shadow-md">
            🏴‍☠️ Treasure Hunt Master
          </span>
          <h3 className="text-2xl sm:text-3xl font-black text-amber-200 mt-3">{activityTitle}</h3>
          <p className="text-xs text-amber-200/80 mt-1 font-medium">
            {chestOpened ? '✨ Golden Treasure Chest Unlocked! All rewards claimed.' : 'Click the Treasure Chest above to unlock your loot!'}
          </p>
        </div>

        <div className="grid grid-cols-2 gap-4 bg-amber-950/70 p-5 rounded-2xl border border-amber-700/60 shadow-inner">
          <div>
            <p className="text-[10px] uppercase text-amber-300 font-extrabold tracking-wider">Clues Solved</p>
            <p className="text-3xl font-black text-emerald-400 mt-1">{result.correctCount} / {result.totalQuestions}</p>
          </div>
          <div>
            <p className="text-[10px] uppercase text-amber-300 font-extrabold tracking-wider">Map Accuracy</p>
            <p className="text-3xl font-black text-amber-300 mt-1">{result.scorePercent}%</p>
          </div>
        </div>

        <div className="flex flex-wrap justify-center gap-4 text-sm font-bold pt-2">
          <span className="bg-gradient-to-r from-amber-600 to-yellow-600 border border-amber-300 text-white px-5 py-2.5 rounded-full shadow-lg flex items-center gap-2 font-black">
            <span>⚡</span> +{result.xpEarned} XP
          </span>
          <span className="bg-gradient-to-r from-yellow-400 to-amber-500 border border-yellow-200 text-slate-950 px-5 py-2.5 rounded-full shadow-lg flex items-center gap-2 font-black">
            <span>🪙</span> +{result.coinsEarned} Gold Coins
          </span>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-b from-amber-950 via-stone-900 to-slate-950 text-white p-6 sm:p-8 rounded-3xl border-2 border-amber-500/50 shadow-2xl max-w-3xl mx-auto select-none">
      {/* Header Bar */}
      <div className="flex flex-wrap items-center justify-between pb-4 border-b border-amber-800/80 mb-5 gap-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-amber-500/20 border border-amber-400 flex items-center justify-center text-xl shadow-inner">
            🏴‍☠️
          </div>
          <div>
            <span className="text-[10px] font-black uppercase tracking-widest text-amber-400 block">
              Pirate Treasure Map Quest
            </span>
            <h3 className="font-black text-lg text-amber-100">{activityTitle}</h3>
          </div>
        </div>
        <div className="text-right text-xs">
          <span className="text-amber-300/80 block font-semibold text-[10px] uppercase">Waypoint Progress</span>
          <span className="font-black text-amber-400 text-sm">Step {currentStageIndex + 1} of {stages.length}</span>
        </div>
      </div>

      {/* Interactive Map Stepper Trail */}
      <div className="relative flex items-center justify-between mb-6 bg-amber-950/50 p-4 rounded-2xl border border-amber-700/60 shadow-inner">
        <div className="absolute left-8 right-8 top-1/2 h-1 bg-amber-900/60 -translate-y-1/2 z-0"></div>
        {stages.map((st, idx) => {
          const isActive = idx === currentStageIndex;
          const isPassed = idx < currentStageIndex;

          return (
            <div key={idx} className="relative z-10 flex flex-col items-center">
              <div
                className={`w-10 h-10 rounded-full flex items-center justify-center font-black text-xs transition-all duration-300 shadow-xl ${
                  isActive
                    ? 'bg-amber-400 text-slate-950 ring-4 ring-yellow-400/50 scale-110 animate-pulse'
                    : isPassed
                    ? 'bg-emerald-500 text-white border-2 border-emerald-300'
                    : 'bg-stone-800 text-stone-500 border border-stone-700'
                }`}
              >
                {isPassed ? '✓' : st.stageIndex}
              </div>
              <span className={`text-[10px] font-bold mt-1.5 ${isActive ? 'text-amber-300' : 'text-stone-400'}`}>
                Stage {st.stageIndex}
              </span>
            </div>
          );
        })}
      </div>

      {/* Weathered Scroll Clue Card */}
      <div className="bg-gradient-to-r from-amber-900/80 to-yellow-950/80 p-5 rounded-2xl border border-amber-600/60 mb-5 text-amber-100 shadow-xl relative overflow-hidden">
        <div className="absolute -right-4 -bottom-4 text-6xl opacity-10 pointer-events-none">📜</div>
        <p className="text-[11px] font-extrabold text-amber-400 uppercase tracking-widest mb-1 flex items-center gap-1.5">
          <span>📜</span> Map Clue #{currentStageIndex + 1}
        </p>
        <p className="text-base font-semibold italic text-amber-50">{currentStage.clue}</p>
      </div>

      {/* Question & Options */}
      <div className="bg-stone-900/90 p-6 rounded-2xl border border-amber-700/50 space-y-4 mb-6 shadow-lg">
        <h4 className="font-black text-base sm:text-lg text-amber-100 flex items-center gap-2">
          <span>❓</span> {currentStage.question}
        </h4>

        {stageError && (
          <div className="p-3 text-xs font-bold rounded-xl bg-red-950/80 border border-red-700 text-red-200 animate-bounce">
            {stageError}
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
          {currentStage.options.map((opt) => {
            const isSelected = selectedOption === opt;
            return (
              <button
                key={opt}
                onClick={() => setSelectedOption(opt)}
                className={`p-4 rounded-xl border-2 text-left text-xs sm:text-sm font-black transition-all cursor-pointer ${
                  isSelected
                    ? 'bg-gradient-to-r from-amber-400 to-yellow-500 text-slate-950 border-yellow-200 shadow-xl scale-[1.02]'
                    : 'bg-stone-800/90 border-stone-700 text-stone-200 hover:bg-stone-700 hover:border-amber-500/60'
                }`}
              >
                <span>{opt}</span>
              </button>
            );
          })}
        </div>
      </div>

      <button
        onClick={handleNextStage}
        disabled={!selectedOption}
        className={`w-full py-4 rounded-2xl font-black text-sm uppercase tracking-wider transition-all shadow-xl ${
          selectedOption
            ? 'bg-gradient-to-r from-amber-400 via-yellow-500 to-amber-500 text-slate-950 hover:brightness-110 cursor-pointer transform hover:scale-[1.01]'
            : 'bg-stone-800 text-stone-500 cursor-not-allowed border border-stone-700'
        }`}
      >
        {currentStageIndex + 1 < stages.length ? 'Unlock Next Stage Clue 🗝️' : 'Unlock Treasure Chest 🎁'}
      </button>
    </div>
  );
};
