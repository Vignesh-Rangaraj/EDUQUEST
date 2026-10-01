import React, { useState } from 'react';
import { CoinTransaction } from '../../types';
import { gamificationService } from '../../services/gamificationService';

interface CoinWalletBadgeProps {
  coins: number;
}

export const CoinWalletBadge: React.FC<CoinWalletBadgeProps> = ({ coins }) => {
  const [showHistory, setShowHistory] = useState(false);
  const [history, setHistory] = useState<CoinTransaction[]>([]);
  const [loading, setLoading] = useState(false);

  const toggleHistory = async () => {
    if (!showHistory && history.length === 0) {
      setLoading(true);
      try {
        const data = await gamificationService.getCoinHistory();
        setHistory(data);
      } catch (e) {
        console.error("Failed to load coin history", e);
      } finally {
        setLoading(false);
      }
    }
    setShowHistory(!showHistory);
  };

  return (
    <div className="relative inline-block">
      <button
        onClick={toggleHistory}
        className="flex items-center space-x-1.5 bg-gradient-to-r from-amber-400 to-yellow-500 text-amber-950 font-bold px-3 py-1.5 rounded-full shadow hover:brightness-105 transition-all text-sm"
      >
        <span className="text-lg">🪙</span>
        <span>{coins}</span>
        <span className="text-xs uppercase font-extrabold tracking-wider opacity-80">Coins</span>
      </button>

      {showHistory && (
        <div className="absolute right-0 mt-2 w-72 bg-white rounded-xl shadow-2xl border border-gray-200 z-50 p-4 text-gray-800">
          <div className="flex items-center justify-between pb-2 border-b border-gray-100 mb-3">
            <h5 className="font-bold text-sm text-gray-900 flex items-center gap-1.5">
              <span>🪙</span> Coin Transaction History
            </h5>
            <button onClick={() => setShowHistory(false)} className="text-gray-400 hover:text-gray-600 text-xs font-bold">
              ✕
            </button>
          </div>

          {loading ? (
            <p className="text-xs text-gray-500 py-4 text-center">Loading transactions...</p>
          ) : history.length === 0 ? (
            <p className="text-xs text-gray-500 py-4 text-center">No coin transactions yet.</p>
          ) : (
            <div className="max-h-60 overflow-y-auto space-y-2 pr-1">
              {history.map((tx) => (
                <div key={tx.id} className="flex justify-between items-center text-xs p-2 rounded-lg bg-amber-50/50 border border-amber-100">
                  <div>
                    <p className="font-medium text-gray-800">{tx.reason.replace(/_/g, ' ')}</p>
                    <p className="text-[10px] text-gray-400">{new Date(tx.createdAt).toLocaleDateString()}</p>
                  </div>
                  <span className="font-extrabold text-amber-700 bg-amber-200/60 px-2 py-0.5 rounded-full">
                    +{tx.coinsAwarded}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};
