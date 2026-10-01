import React, { useState, useEffect } from 'react';
import { Wifi, WifiOff, RefreshCw, Clock } from 'lucide-react';
import { networkService } from '../offline/networkService';
import { syncService } from '../offline/syncService';

export const NetworkStatusBadge: React.FC = () => {
  const [isOnline, setIsOnline] = useState<boolean>(networkService.isOnline());
  const [pendingCount, setPendingCount] = useState<number>(0);
  const [lastSyncTime, setLastSyncTime] = useState<string | null>(null);
  const [isSyncing, setIsSyncing] = useState<boolean>(false);

  const refreshSyncData = async () => {
    const count = await syncService.getPendingCount();
    setPendingCount(count);
    const lastSync = await syncService.getLastSyncTime();
    if (lastSync) {
      const date = new Date(lastSync);
      setLastSyncTime(date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }));
    } else {
      setLastSyncTime('Never');
    }
  };

  useEffect(() => {
    const unsubscribeNetwork = networkService.subscribe((online) => {
      setIsOnline(online);
      refreshSyncData();
    });

    const unsubscribeSync = syncService.subscribe(() => {
      refreshSyncData();
    });

    refreshSyncData();

    return () => {
      unsubscribeNetwork();
      unsubscribeSync();
    };
  }, []);

  const handleSyncNow = async () => {
    if (!isOnline || isSyncing) return;
    setIsSyncing(true);
    await syncService.processQueue();
    await refreshSyncData();
    setIsSyncing(false);
  };

  return (
    <div className="flex flex-wrap items-center gap-3 bg-gray-50 dark:bg-gray-800 p-3 rounded-lg border border-gray-200 dark:border-gray-700 shadow-sm text-sm">
      {/* Network Indicator */}
      <div className={`flex items-center space-x-1.5 px-3 py-1 rounded-full text-xs font-semibold ${
        isOnline 
          ? 'bg-green-100 text-green-800 dark:bg-green-900/40 dark:text-green-300' 
          : 'bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300'
      }`}>
        {isOnline ? <Wifi className="w-3.5 h-3.5" /> : <WifiOff className="w-3.5 h-3.5" />}
        <span>{isOnline ? 'Online Mode' : 'Offline Mode'}</span>
      </div>

      {/* Sync Queue Count */}
      <div className="flex items-center space-x-1 text-gray-600 dark:text-gray-300">
        <span className="font-semibold text-gray-800 dark:text-gray-100">{pendingCount}</span>
        <span className="text-xs">pending sync</span>
      </div>

      {/* Last Sync Time */}
      <div className="flex items-center space-x-1 text-xs text-gray-500 dark:text-gray-400">
        <Clock className="w-3.5 h-3.5" />
        <span>Last Sync: {lastSyncTime || 'Never'}</span>
      </div>

      {/* Sync Now Button */}
      <button
        onClick={handleSyncNow}
        disabled={!isOnline || pendingCount === 0 || isSyncing}
        className={`flex items-center space-x-1 px-3 py-1 text-xs font-medium rounded-md transition-colors ${
          !isOnline || pendingCount === 0 || isSyncing
            ? 'bg-gray-200 text-gray-400 cursor-not-allowed dark:bg-gray-700 dark:text-gray-500'
            : 'bg-blue-600 text-white hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600'
        }`}
        title={!isOnline ? 'Network unavailable' : pendingCount === 0 ? 'All changes synced' : 'Sync pending items'}
      >
        <RefreshCw className={`w-3.5 h-3.5 ${isSyncing ? 'animate-spin' : ''}`} />
        <span>{isSyncing ? 'Syncing...' : 'Sync Now'}</span>
      </button>
    </div>
  );
};
