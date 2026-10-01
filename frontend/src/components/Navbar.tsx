import React from 'react';
import { useAuth } from '../context/AuthContext';
import { ThemeToggle } from './ThemeToggle';
import { NetworkStatusBadge } from './NetworkStatusBadge';
import { LogOut, BookOpen, User as UserIcon } from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <header className="w-full h-16 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-4 sm:px-6 flex items-center justify-between sticky top-0 z-40 shadow-sm">
      <div className="flex items-center gap-3">
        <div className="bg-sky-600 text-white p-2 rounded-xl flex items-center justify-center shadow-sm flex-shrink-0">
          <BookOpen className="w-5 h-5" />
        </div>
        <div className="flex flex-col justify-center">
          <h1 className="font-bold text-base sm:text-lg text-gray-900 dark:text-white tracking-tight leading-tight">
            EduQuest
          </h1>
          <p className="text-[11px] text-gray-500 dark:text-gray-400 font-medium leading-none mt-0.5">
            Offline-First Learning Platform
          </p>
        </div>
      </div>

      <div className="flex items-center gap-3 sm:gap-4">
        <NetworkStatusBadge />
        <ThemeToggle />

        {user && (
          <div className="flex items-center gap-3 pl-3 sm:pl-4 border-l border-gray-200 dark:border-gray-700">
            <div className="w-9 h-9 rounded-full bg-sky-100 dark:bg-sky-900/50 text-sky-700 dark:text-sky-300 font-semibold flex items-center justify-center border border-sky-200 dark:border-sky-700 flex-shrink-0">
              <UserIcon className="w-5 h-5" />
            </div>
            <div className="hidden sm:block">
              <p className="text-sm font-semibold text-gray-900 dark:text-white leading-none">{user.fullName}</p>
              <span className="inline-block mt-1 px-2 py-0.5 text-[10px] font-bold tracking-wide uppercase bg-sky-100 dark:bg-sky-900/60 text-sky-800 dark:text-sky-200 rounded-full">
                {user.role.replace('_', ' ')}
              </span>
            </div>
            <button
              onClick={logout}
              className="ml-1 sm:ml-2 p-2 rounded-lg text-gray-500 hover:text-red-600 dark:text-gray-400 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-950/40 transition-colors flex-shrink-0"
              title="Sign Out"
            >
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        )}
      </div>
    </header>
  );
};
