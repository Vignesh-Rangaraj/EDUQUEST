import React from 'react';
import { NavLink, useSearchParams, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldCheck, GraduationCap, UserCheck, Users, Trophy, BookOpen, Sparkles, Layers, Award, BarChart3 } from 'lucide-react';

export const Sidebar: React.FC = () => {
  const { user } = useAuth();
  const [searchParams] = useSearchParams();
  const location = useLocation();

  if (!user) return null;

  const currentTab = searchParams.get('tab') || 'MATHEMATICS';
  const isStudentRoute = location.pathname.startsWith('/student');
  const isTeacherRoute = location.pathname.startsWith('/teacher');
  const isParentRoute = location.pathname.startsWith('/parent');
  const isAdminRoute = location.pathname.startsWith('/admin');
  const activeTeacherTab = (isTeacherRoute && searchParams.get('tab')) || 'MODULES';
  const activeParentTab = (isParentRoute && searchParams.get('tab')) || 'OVERVIEW';
  const activeAdminTab = (isAdminRoute && searchParams.get('tab')) || 'STUDENTS';

  return (
    <aside className="w-64 flex-shrink-0 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 p-4 min-h-[calc(100vh-4rem)] flex flex-col justify-between">
      <nav className="space-y-3">
        {user.role === 'SUPER_ADMIN' && (
          <div className="space-y-3">
            <div className="space-y-1">
              <div className="px-3 py-1 text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1.5">
                <ShieldCheck className="w-3.5 h-3.5 text-sky-600" />
                <span>Super Admin Control</span>
              </div>

              {[
                { tab: 'STUDENTS', label: 'Student Directory', icon: GraduationCap },
                { tab: 'TEACHERS', label: 'Teacher Directory', icon: Users },
                { tab: 'ACCOUNTS', label: 'Accounts & Roles', icon: ShieldCheck },
                { tab: 'ANALYTICS', label: 'System Analytics', icon: Layers }
              ].map((item) => {
                const IconComponent = item.icon;
                const isActive = isAdminRoute && activeAdminTab === item.tab;
                return (
                  <NavLink
                    key={item.tab}
                    to={`/admin?tab=${item.tab}`}
                    className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                      isActive
                        ? 'bg-sky-600 text-white shadow-sm'
                        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                    }`}
                  >
                    <IconComponent className="w-4 h-4" />
                    <span>{item.label}</span>
                  </NavLink>
                );
              })}
            </div>
          </div>
        )}

        {user.role === 'TEACHER' && (
          <div className="space-y-3">
            <div className="space-y-1">
              <div className="px-3 py-1 text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1.5">
                <GraduationCap className="w-3.5 h-3.5 text-emerald-600" />
                <span>Teacher Sections</span>
              </div>

              {[
                { tab: 'MODULES', label: 'General Education', icon: Layers },
                { tab: 'ACTIVITIES', label: 'All Activities', icon: BookOpen },
                { tab: 'ROSTER', label: 'Enrollment list', icon: Users },
                { tab: 'ANALYTICS', label: 'Analytics', icon: BarChart3 },
                { tab: 'CHALLENGES', label: 'Classroom Challenges', icon: Trophy }
              ].map((item) => {
                const IconComponent = item.icon;
                const isActive = isTeacherRoute && activeTeacherTab === item.tab;
                return (
                  <NavLink
                    key={item.tab}
                    to={`/teacher?tab=${item.tab}`}
                    className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                      isActive
                        ? 'bg-emerald-600 text-white shadow-sm'
                        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                    }`}
                  >
                    <IconComponent className="w-4 h-4" />
                    <span>{item.label}</span>
                  </NavLink>
                );
              })}

              <NavLink
                to="/ai-tutor"
                className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                  location.pathname.includes('/ai-tutor')
                    ? 'bg-blue-600 text-white shadow-sm'
                    : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                }`}
              >
                <Sparkles className="w-4 h-4 text-purple-400" />
                <span>AI Tutor Assistant</span>
              </NavLink>
            </div>
          </div>
        )}

        {user.role === 'STUDENT' && (
          <div className="space-y-3">
            <div className="space-y-1">
              <div className="px-3 py-1 text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1.5">
                <BookOpen className="w-3.5 h-3.5 text-sky-600" />
                <span>Subject Modules</span>
              </div>

              {[
                { tab: 'MATHEMATICS', label: 'Mathematics', icon: '📐' },
                { tab: 'SCIENCE', label: 'Science', icon: '🔬' },
                { tab: 'ENGLISH', label: 'English', icon: '📚' },
                { tab: 'SOCIAL_SCIENCE', label: 'Social Science', icon: '🌍' },
                { tab: 'TAMIL', label: 'Tamil', icon: '🔤' },
                { tab: 'ALL_SUBJECTS', label: 'All Subjects', icon: '🌟' }
              ].map((item) => {
                const isActive = isStudentRoute && currentTab === item.tab;
                return (
                  <NavLink
                    key={item.tab}
                    to={`/student?tab=${item.tab}`}
                    className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                      isActive
                        ? 'bg-sky-600 text-white shadow-sm'
                        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                    }`}
                  >
                    <span className="text-sm">{item.icon}</span>
                    <span>{item.label}</span>
                  </NavLink>
                );
              })}
            </div>

            <div className="space-y-1 pt-2 border-t border-gray-100 dark:border-gray-700">
              <div className="px-3 py-1 text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                <span>Portal Sections</span>
              </div>

              <NavLink
                to="/student?tab=LEADERBOARD"
                className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                  isStudentRoute && currentTab === 'LEADERBOARD'
                    ? 'bg-amber-500 text-white shadow-sm'
                    : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                }`}
              >
                <Trophy className="w-4 h-4 text-amber-400" />
                <span>Leaderboard</span>
              </NavLink>

              <NavLink
                to="/student?tab=PROFILE"
                className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                  isStudentRoute && currentTab === 'PROFILE'
                    ? 'bg-indigo-600 text-white shadow-sm'
                    : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                }`}
              >
                <UserCheck className="w-4 h-4 text-indigo-300" />
                <span>Student Profile</span>
              </NavLink>

              <NavLink
                to="/ai-tutor"
                className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                  location.pathname.includes('/ai-tutor')
                    ? 'bg-blue-600 text-white shadow-sm'
                    : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                }`}
              >
                <Sparkles className="w-4 h-4 text-purple-400" />
                <span>AI Tutor Assistant</span>
              </NavLink>
            </div>
          </div>
        )}

        {user.role === 'PARENT' && (
          <div className="space-y-3">
            <div className="space-y-1">
              <div className="px-3 py-1 text-[11px] font-bold text-gray-400 uppercase tracking-wider flex items-center gap-1.5">
                <Users className="w-3.5 h-3.5 text-amber-600" />
                <span>Parent Portal</span>
              </div>

              {[
                { tab: 'OVERVIEW', label: 'Overview & Progress', icon: Users },
                { tab: 'BADGES', label: 'Badges & Achievements', icon: Award },
                { tab: 'LEADERBOARD', label: 'Leaderboard & Streaks', icon: Trophy },
                { tab: 'SUBJECTS', label: 'Subject Performance', icon: BookOpen }
              ].map((item) => {
                const IconComponent = item.icon;
                const isActive = isParentRoute && activeParentTab === item.tab;
                return (
                  <NavLink
                    key={item.tab}
                    to={`/parent?tab=${item.tab}`}
                    className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                      isActive
                        ? 'bg-amber-600 text-white shadow-sm'
                        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700/50'
                    }`}
                  >
                    <IconComponent className="w-4 h-4" />
                    <span>{item.label}</span>
                  </NavLink>
                );
              })}
            </div>
          </div>
        )}
      </nav>

      <div className="p-4 bg-sky-50 dark:bg-sky-950/40 rounded-xl border border-sky-100 dark:border-sky-900/50">
        <p className="text-xs font-semibold text-sky-900 dark:text-sky-200">EduQuest Demo School</p>
        <p className="text-[11px] text-sky-700 dark:text-sky-400 mt-0.5">Offline-First Engine v2.0</p>
      </div>
    </aside>
  );
};
