import React, { useEffect, useState } from 'react';
import { Activity, AlertCircle, CheckCircle2, RefreshCw, Users } from 'lucide-react';
import { ClassroomProgressAnalytics, teacherService } from '../../services/teacherService';

export const ClassroomAnalytics: React.FC = () => {
  const [report, setReport] = useState<ClassroomProgressAnalytics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadReport = async () => {
    setLoading(true);
    setError(null);
    try {
      setReport(await teacherService.getClassroomProgressAnalytics());
    } catch {
      setError('Unable to load classroom progress. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadReport();
  }, []);

  if (loading) {
    return <div className="rounded-2xl border border-gray-200 bg-white p-8 text-center text-sm text-gray-500 shadow-sm dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" role="status">Loading classroom analytics…</div>;
  }

  if (error) {
    return (
      <div className="flex items-center justify-between gap-4 rounded-2xl border border-red-200 bg-red-50 p-5 text-sm text-red-700 dark:border-red-900 dark:bg-red-950/30 dark:text-red-300" role="alert">
        <span className="flex items-center gap-2"><AlertCircle className="h-4 w-4" />{error}</span>
        <button onClick={() => void loadReport()} className="inline-flex items-center gap-2 rounded-lg border border-red-200 px-3 py-2 font-semibold hover:bg-red-100 dark:border-red-800 dark:hover:bg-red-900/40">
          <RefreshCw className="h-4 w-4" />Retry
        </button>
      </div>
    );
  }

  if (!report) return null;

  const stats = [
    { label: 'Students in class', value: report.studentCount, icon: Users, color: 'text-blue-600 bg-blue-50 dark:bg-blue-950/50 dark:text-blue-300' },
    { label: 'Available activities', value: report.availableActivityCount, icon: Activity, color: 'text-violet-600 bg-violet-50 dark:bg-violet-950/50 dark:text-violet-300' },
    { label: 'Completed', value: report.completedAssignments, icon: CheckCircle2, color: 'text-emerald-600 bg-emerald-50 dark:bg-emerald-950/50 dark:text-emerald-300' },
    { label: 'Still to complete', value: report.pendingAssignments, icon: AlertCircle, color: 'text-amber-600 bg-amber-50 dark:bg-amber-950/50 dark:text-amber-300' }
  ];

  return (
    <section className="space-y-5" aria-labelledby="classroom-analytics-heading">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 id="classroom-analytics-heading" className="text-lg font-bold text-gray-900 dark:text-white">Classroom Analytics</h2>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">Student progress in {report.classroomName || 'your classroom'} only</p>
        </div>
        <button onClick={() => void loadReport()} className="inline-flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-semibold text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-200 dark:hover:bg-gray-700" aria-label="Refresh classroom analytics">
          <RefreshCw className="h-4 w-4" />Refresh
        </button>
      </div>

      <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
        {stats.map(({ label, value, icon: Icon, color }) => (
          <div key={label} className="flex items-center gap-3 rounded-xl border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-800">
            <div className={`rounded-lg p-2 ${color}`}><Icon className="h-5 w-5" /></div>
            <div><p className="text-xs text-gray-500 dark:text-gray-400">{label}</p><p className="text-xl font-bold text-gray-900 dark:text-white">{value}</p></div>
          </div>
        ))}
      </div>

      <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm dark:border-gray-700 dark:bg-gray-800">
        <div className="flex items-center justify-between text-sm">
          <span className="font-semibold text-gray-700 dark:text-gray-200">Class completion</span>
          <span className="font-bold text-emerald-700 dark:text-emerald-300">{report.classroomCompletionPercentage}%</span>
        </div>
        <div className="mt-2 h-2.5 overflow-hidden rounded-full bg-gray-100 dark:bg-gray-700" role="progressbar" aria-label="Classroom completion" aria-valuemin={0} aria-valuemax={100} aria-valuenow={report.classroomCompletionPercentage}>
          <div className="h-full rounded-full bg-emerald-500 transition-[width]" style={{ width: `${report.classroomCompletionPercentage}%` }} />
        </div>
      </div>

      <div className="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm dark:border-gray-700 dark:bg-gray-800">
        <div className="border-b border-gray-200 px-5 py-4 dark:border-gray-700">
          <h3 className="font-bold text-gray-900 dark:text-white">Student progress</h3>
          <p className="mt-1 text-xs text-gray-500 dark:text-gray-400">Completion is based on activities available to this classroom.</p>
        </div>
        {report.students.length === 0 ? (
          <p className="p-8 text-center text-sm text-gray-500 dark:text-gray-400">No students are currently enrolled in this classroom.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[680px] text-left text-sm">
              <thead className="bg-gray-50 text-xs uppercase text-gray-500 dark:bg-gray-900/40 dark:text-gray-400">
                <tr><th scope="col" className="px-5 py-3">Student</th><th scope="col" className="px-5 py-3">Progress</th><th scope="col" className="px-5 py-3">Completed</th><th scope="col" className="px-5 py-3">Pending</th><th scope="col" className="px-5 py-3">Last completion</th></tr>
              </thead>
              <tbody className="divide-y divide-gray-100 dark:divide-gray-700">
                {report.students.map((student) => (
                  <tr key={student.studentId} className="align-middle">
                    <td className="px-5 py-4"><p className="font-semibold text-gray-900 dark:text-white">{student.fullName}</p><p className="mt-0.5 text-xs text-gray-500 dark:text-gray-400">{student.username} · Level {student.level} · {student.xp} XP</p></td>
                    <td className="px-5 py-4"><div className="flex items-center gap-3"><div className="h-2 w-24 overflow-hidden rounded-full bg-gray-100 dark:bg-gray-700"><div className="h-full rounded-full bg-emerald-500" style={{ width: `${student.completionPercentage}%` }} /></div><span className="font-semibold text-gray-700 dark:text-gray-200">{student.completionPercentage}%</span></div></td>
                    <td className="px-5 py-4 font-medium text-emerald-700 dark:text-emerald-300">{student.completedCount} / {student.totalActivities}</td>
                    <td className="px-5 py-4 font-medium text-amber-700 dark:text-amber-300">{student.pendingCount}</td>
                    <td className="px-5 py-4 text-gray-500 dark:text-gray-400">{student.lastActivityAt ? new Date(student.lastActivityAt).toLocaleDateString() : '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
};
