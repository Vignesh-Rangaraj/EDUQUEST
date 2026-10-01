import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { ProtectedRoute } from './components/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { AdminDashboard } from './pages/AdminDashboard';
import { TeacherDashboard } from './pages/TeacherDashboard';
import { StudentDashboard } from './pages/StudentDashboard';
import { StudentLessonPlayer } from './pages/student/StudentLessonPlayer';
import { ParentProfilePage } from './pages/ParentProfilePage';
import { AiTutorPage } from './modules/ai/AiTutorPage';

const AppLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <div className="relative min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors flex flex-col w-full overflow-x-hidden">
      <Navbar />
      <div className="flex flex-1 w-full min-w-0">
        <Sidebar />
        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto w-full min-w-0">{children}</main>
      </div>
    </div>
  );
};

const RootRedirect: React.FC = () => {
  const { isAuthenticated, user } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role === 'SUPER_ADMIN') return <Navigate to="/admin" replace />;
  if (user?.role === 'TEACHER') return <Navigate to="/teacher" replace />;
  if (user?.role === 'STUDENT') return <Navigate to="/student" replace />;
  if (user?.role === 'PARENT') return <Navigate to="/parent" replace />;
  return <Navigate to="/login" replace />;
};

export const App: React.FC = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Router>
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route
              path="/admin"
              element={
                <ProtectedRoute allowedRoles={['SUPER_ADMIN']}>
                  <AppLayout>
                    <AdminDashboard />
                  </AppLayout>
                </ProtectedRoute>
              }
            />

            <Route
              path="/teacher"
              element={
                <ProtectedRoute allowedRoles={['TEACHER', 'SUPER_ADMIN']}>
                  <AppLayout>
                    <TeacherDashboard />
                  </AppLayout>
                </ProtectedRoute>
              }
            />

            <Route
              path="/student"
              element={
                <ProtectedRoute allowedRoles={['STUDENT', 'TEACHER', 'SUPER_ADMIN']}>
                  <AppLayout>
                    <StudentDashboard />
                  </AppLayout>
                </ProtectedRoute>
              }
            />

            <Route
              path="/student/lesson/:id"
              element={
                <ProtectedRoute allowedRoles={['STUDENT', 'TEACHER', 'SUPER_ADMIN']}>
                  <AppLayout>
                    <StudentLessonPlayer />
                  </AppLayout>
                </ProtectedRoute>
              }
            />

            <Route
              path="/parent"
              element={
                <ProtectedRoute allowedRoles={['PARENT', 'SUPER_ADMIN']}>
                  <AppLayout>
                    <ParentProfilePage />
                  </AppLayout>
                </ProtectedRoute>
              }
            />

            <Route
              path="/ai-tutor"
              element={
                <ProtectedRoute allowedRoles={['STUDENT', 'TEACHER', 'PARENT', 'SUPER_ADMIN']}>
                  <AppLayout>
                    <AiTutorPage />
                  </AppLayout>
                </ProtectedRoute>
              }
            />

            <Route
              path="/student/ai-tutor"
              element={
                <ProtectedRoute allowedRoles={['STUDENT', 'TEACHER', 'PARENT', 'SUPER_ADMIN']}>
                  <AppLayout>
                    <AiTutorPage />
                  </AppLayout>
                </ProtectedRoute>
              }
            />

            <Route path="*" element={<RootRedirect />} />
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App;
