import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { ThemeToggle } from '../components/ThemeToggle';
import { BookOpen, Key, UserCheck, AlertCircle, Shield, GraduationCap, User, Users, ArrowLeft } from 'lucide-react';

type RoleType = 'STUDENT' | 'TEACHER' | 'PARENT' | 'SUPER_ADMIN' | null;

interface RoleCardConfig {
  id: 'STUDENT' | 'TEACHER' | 'PARENT' | 'SUPER_ADMIN';
  title: string;
  subtitle: string;
  icon: React.ReactNode;
  bgGradient: string;
  borderColor: string;
  badgeBg: string;
  badgeText: string;
  defaultUser: string;
  defaultPass: string;
}

const ROLE_CARDS: RoleCardConfig[] = [
  {
    id: 'STUDENT',
    title: 'Student',
    subtitle: 'Access gamified lessons, quizzes, badges & leaderboard',
    icon: <User className="w-8 h-8 text-sky-500" />,
    bgGradient: 'hover:border-sky-500 dark:hover:border-sky-400 hover:bg-sky-50/50 dark:hover:bg-sky-950/30',
    borderColor: 'border-sky-200 dark:border-sky-800',
    badgeBg: 'bg-sky-100 text-sky-800 dark:bg-sky-900/60 dark:text-sky-300',
    badgeText: 'Student Portal',
    defaultUser: 'student_6a_1',
    defaultPass: 'password123'
  },
  {
    id: 'TEACHER',
    title: 'Teacher',
    subtitle: 'Manage classrooms, publish lessons & view student analytics',
    icon: <GraduationCap className="w-8 h-8 text-emerald-500" />,
    bgGradient: 'hover:border-emerald-500 dark:hover:border-emerald-400 hover:bg-emerald-50/50 dark:hover:bg-emerald-950/30',
    borderColor: 'border-emerald-200 dark:border-emerald-800',
    badgeBg: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/60 dark:text-emerald-300',
    badgeText: 'Teacher Portal',
    defaultUser: 'teacher_6a',
    defaultPass: 'password123'
  },
  {
    id: 'PARENT',
    title: 'Parent',
    subtitle: 'Track your child\'s completion rates, streaks & performance',
    icon: <Users className="w-8 h-8 text-amber-500" />,
    bgGradient: 'hover:border-amber-500 dark:hover:border-amber-400 hover:bg-amber-50/50 dark:hover:bg-amber-950/30',
    borderColor: 'border-amber-200 dark:border-amber-800',
    badgeBg: 'bg-amber-100 text-amber-800 dark:bg-amber-900/60 dark:text-amber-300',
    badgeText: 'Parent Portal',
    defaultUser: 'parent_6a_1',
    defaultPass: 'password123'
  },
  {
    id: 'SUPER_ADMIN',
    title: 'SuperAdmin',
    subtitle: 'System-wide account management, rosters & database governance',
    icon: <Shield className="w-8 h-8 text-purple-500" />,
    bgGradient: 'hover:border-purple-500 dark:hover:border-purple-400 hover:bg-purple-50/50 dark:hover:bg-purple-950/30',
    borderColor: 'border-purple-200 dark:border-purple-800',
    badgeBg: 'bg-purple-100 text-purple-800 dark:bg-purple-900/60 dark:text-purple-300',
    badgeText: 'SuperAdmin Console',
    defaultUser: 'admin',
    defaultPass: 'admin123'
  }
];

export const LoginPage: React.FC = () => {
  const [selectedRole, setSelectedRole] = useState<RoleType>(null);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSelectRole = (card: RoleCardConfig) => {
    setSelectedRole(card.id);
    setUsername(card.defaultUser);
    setPassword(card.defaultPass);
    setError(null);
  };

  const handleBackToRoleSelection = () => {
    setSelectedRole(null);
    setError(null);
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const data = await authService.login(username, password);
      login(data.token, {
        id: data.userId,
        username: data.username,
        fullName: data.fullName,
        role: data.role,
      });

      if (data.role === 'SUPER_ADMIN') navigate('/admin');
      else if (data.role === 'TEACHER') navigate('/teacher');
      else if (data.role === 'STUDENT') navigate('/student');
      else if (data.role === 'PARENT') navigate('/parent');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid username or password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const selectedConfig = ROLE_CARDS.find((r) => r.id === selectedRole);

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8 transition-colors">
      <div className="absolute top-6 right-6">
        <ThemeToggle />
      </div>

      <div className="sm:mx-auto sm:w-full text-center max-w-xl">
        <div className="mx-auto w-16 h-16 bg-sky-600 rounded-2xl flex items-center justify-center text-white shadow-lg shadow-sky-500/30 mb-4">
          <BookOpen className="w-10 h-10" />
        </div>
        <h2 className="text-3xl font-extrabold text-gray-900 dark:text-white tracking-tight">EduQuest</h2>
        <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">Offline-First Gamified Learning Platform</p>
      </div>

      <div className={`mt-8 sm:mx-auto w-full transition-all duration-300 ${selectedRole === null ? 'max-w-6xl' : 'max-w-md'}`}>
        {selectedRole === null ? (
          /* STEP 1: 4 ROLE CARDS IN A SINGLE ROW FROM LEFT TO RIGHT */
          <div className="bg-white dark:bg-gray-800 py-8 px-6 sm:px-10 shadow-xl rounded-2xl border border-gray-100 dark:border-gray-700 space-y-6">
            <div className="text-center">
              <h3 className="text-xl font-extrabold text-gray-900 dark:text-white">Select Your Role</h3>
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                Choose how you want to sign in to EduQuest
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
              {ROLE_CARDS.map((card) => (
                <button
                  key={card.id}
                  onClick={() => handleSelectRole(card)}
                  className={`p-6 rounded-2xl border-2 text-left transition-all duration-200 shadow-sm flex flex-col justify-between cursor-pointer group hover:scale-[1.02] ${card.borderColor} ${card.bgGradient}`}
                >
                  <div className="flex items-center justify-between mb-5">
                    <div className="p-3 rounded-xl bg-gray-50 dark:bg-gray-700/80 group-hover:scale-110 transition-transform">
                      {card.icon}
                    </div>
                    <span className={`text-[10px] font-extrabold uppercase tracking-wider px-2.5 py-1 rounded-full ${card.badgeBg}`}>
                      {card.title}
                    </span>
                  </div>

                  <div>
                    <h4 className="font-extrabold text-lg text-gray-900 dark:text-white group-hover:text-sky-600 dark:group-hover:text-sky-400 transition-colors">
                      {card.title}
                    </h4>
                    <p className="text-xs text-gray-500 dark:text-gray-400 mt-2 leading-relaxed">
                      {card.subtitle}
                    </p>
                  </div>
                </button>
              ))}
            </div>
          </div>
        ) : (
          /* STEP 2: CREDENTIAL INPUT FORM */
          <div className="bg-white dark:bg-gray-800 py-8 px-6 shadow-xl rounded-2xl border border-gray-100 dark:border-gray-700 sm:px-10">
            <div className="flex items-center justify-between pb-4 border-b border-gray-100 dark:border-gray-700 mb-6">
              <button
                type="button"
                onClick={handleBackToRoleSelection}
                className="flex items-center gap-1.5 text-xs font-bold text-gray-500 dark:text-gray-400 hover:text-sky-600 dark:hover:text-sky-400 transition-colors cursor-pointer"
              >
                <ArrowLeft className="w-4 h-4" />
                <span>Change Role</span>
              </button>
              {selectedConfig && (
                <span className={`text-xs font-extrabold px-3 py-1 rounded-full ${selectedConfig.badgeBg}`}>
                  Logging in as {selectedConfig.title}
                </span>
              )}
            </div>

            {error && (
              <div className="mb-4 p-3 bg-red-50 dark:bg-red-950/50 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-300 rounded-xl text-sm flex items-center gap-2">
                <AlertCircle className="w-5 h-5 flex-shrink-0" />
                <span>{error}</span>
              </div>
            )}

            <form className="space-y-5" onSubmit={handleLogin}>
              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  Username
                </label>
                <div className="relative rounded-xl shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                    <UserCheck className="w-5 h-5" />
                  </div>
                  <input
                    type="text"
                    required
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-500 dark:focus:ring-sky-400 text-sm"
                    placeholder="Enter your username"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 dark:text-gray-300 mb-1">
                  Password
                </label>
                <div className="relative rounded-xl shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                    <Key className="w-5 h-5" />
                  </div>
                  <input
                    type="password"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-600 rounded-xl bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-sky-500 dark:focus:ring-sky-400 text-sm"
                    placeholder="••••••••"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full py-3 px-4 border border-transparent rounded-xl shadow-md text-sm font-bold text-white bg-sky-600 hover:bg-sky-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500 transition-all disabled:opacity-50 cursor-pointer"
              >
                {loading ? 'Authenticating...' : `Sign In as ${selectedConfig?.title || 'User'}`}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};
