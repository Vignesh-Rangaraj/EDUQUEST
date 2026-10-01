import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { adminService } from '../services/adminService';
import { curriculumService } from '../services/curriculumService';
import { gamificationService } from '../services/gamificationService';
import { Teacher, Student, Parent, Classroom, CurriculumOverview, AdminAnalytics } from '../types';
import {
  UserPlus,
  Link,
  Users,
  GraduationCap,
  School as SchoolIcon,
  Shield,
  CheckCircle2,
  AlertCircle,
  BookOpen,
  Layers,
  Award,
  BarChart3,
  TrendingUp,
  Zap,
  Trophy,
  Edit3,
  X,
  Filter,
  Save,
  Search
} from 'lucide-react';

export const AdminDashboard: React.FC = () => {
  const [searchParams] = useSearchParams();
  const activeTab = searchParams.get('tab') || 'STUDENTS';

  const [teachers, setTeachers] = useState<Teacher[]>([]);
  const [students, setStudents] = useState<Student[]>([]);
  const [parents, setParents] = useState<Parent[]>([]);
  const [classrooms, setClassrooms] = useState<Classroom[]>([]);
  const [curriculum, setCurriculum] = useState<CurriculumOverview | null>(null);
  const [analytics, setAnalytics] = useState<AdminAnalytics | null>(null);
  const [challengeAdminStats, setChallengeAdminStats] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Class Filter state for Student Directory
  const [selectedClassFilter, setSelectedClassFilter] = useState<string>('ALL');
  const [studentSearchQuery, setStudentSearchQuery] = useState<string>('');
  const [teacherSearchQuery, setTeacherSearchQuery] = useState<string>('');

  // Modals state
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);
  const [editingStudentFullName, setEditingStudentFullName] = useState<string>('');
  const [editingStudentClassroomId, setEditingStudentClassroomId] = useState<number | ''>('');
  const [editingStudentParentId, setEditingStudentParentId] = useState<number | ''>('');

  const [editingTeacher, setEditingTeacher] = useState<Teacher | null>(null);
  const [editingTeacherFullName, setEditingTeacherFullName] = useState<string>('');
  const [editingTeacherClassroomId, setEditingTeacherClassroomId] = useState<number | ''>('');

  // Account creation form state
  const [roleToCreate, setRoleToCreate] = useState<'STUDENT' | 'TEACHER' | 'PARENT'>('STUDENT');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [selectedClassroomId, setSelectedClassroomId] = useState<number | ''>('');
  const [selectedParentId, setSelectedParentId] = useState<number | ''>('');

  // Assignment states
  const [assignTeacherId, setAssignTeacherId] = useState<number | ''>('');
  const [assignTeacherClassroomId, setAssignTeacherClassroomId] = useState<number | ''>('');

  const [assignStudentId, setAssignStudentId] = useState<number | ''>('');
  const [assignStudentParentId, setAssignStudentParentId] = useState<number | ''>('');

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    setLoading(true);
    try {
      const [tData, sData, pData, cData, currData, analyticsData, cStats] = await Promise.all([
        adminService.getAllTeachers(),
        adminService.getAllStudents(),
        adminService.getAllParents(),
        adminService.getAllClassrooms(),
        curriculumService.getCurriculumOverview().catch(() => null),
        adminService.getAnalytics().catch(() => null),
        gamificationService.getAdminChallengeStats().catch(() => null)
      ]);
      setTeachers(tData);
      setStudents(sData);
      setParents(pData);
      setClassrooms(cData);
      setCurriculum(currData);
      setAnalytics(analyticsData);
      setChallengeAdminStats(cStats);
    } catch (err) {
      console.error('Failed to load admin data', err);
    } finally {
      setLoading(false);
    }
  };

  // Student Edit Handlers
  const handleOpenEditStudent = (student: Student) => {
    setEditingStudent(student);
    setEditingStudentFullName(student.fullName);
    setEditingStudentClassroomId(student.classroomId || '');
    setEditingStudentParentId(student.parentId || '');
  };

  const handleSaveStudentEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingStudent) return;
    setMessage(null);

    try {
      await adminService.updateStudent(editingStudent.id, {
        fullName: editingStudentFullName,
        classroomId: editingStudentClassroomId ? Number(editingStudentClassroomId) : undefined,
        parentId: editingStudentParentId ? Number(editingStudentParentId) : undefined
      });

      setMessage({ type: 'success', text: `Student details for "${editingStudentFullName}" updated successfully!` });
      setEditingStudent(null);
      loadAllData();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to update student details' });
    }
  };

  // Teacher Edit Handlers
  const handleOpenEditTeacher = (teacher: Teacher) => {
    setEditingTeacher(teacher);
    setEditingTeacherFullName(teacher.fullName);
    setEditingTeacherClassroomId(teacher.classroomId || '');
  };

  const handleSaveTeacherEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingTeacher) return;
    setMessage(null);

    try {
      await adminService.updateTeacher(editingTeacher.id, {
        fullName: editingTeacherFullName,
        classroomId: editingTeacherClassroomId ? Number(editingTeacherClassroomId) : undefined
      });

      setMessage({ type: 'success', text: `Teacher details for "${editingTeacherFullName}" updated successfully!` });
      setEditingTeacher(null);
      loadAllData();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to update teacher details' });
    }
  };

  // User Creation & Assignment Handlers
  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);

    try {
      if (roleToCreate === 'TEACHER') {
        await adminService.createTeacher({
          username,
          password,
          fullName,
          classroomId: selectedClassroomId ? Number(selectedClassroomId) : undefined
        });
        setMessage({ type: 'success', text: `Teacher account "${fullName}" created successfully!` });
      } else if (roleToCreate === 'STUDENT') {
        await adminService.createStudent({
          username,
          password,
          fullName,
          classroomId: selectedClassroomId ? Number(selectedClassroomId) : undefined,
          parentId: selectedParentId ? Number(selectedParentId) : undefined
        });
        setMessage({ type: 'success', text: `Student account "${fullName}" created successfully!` });
      } else if (roleToCreate === 'PARENT') {
        await adminService.createParent({ username, password, fullName });
        setMessage({ type: 'success', text: `Parent account "${fullName}" created successfully!` });
      } else if (roleToCreate === 'SUPER_ADMIN') {
        await adminService.createSuperAdmin({ username, password, fullName });
        setMessage({ type: 'success', text: `Super Admin account "${fullName}" created successfully!` });
      }

      setUsername('');
      setPassword('');
      setFullName('');
      setSelectedClassroomId('');
      setSelectedParentId('');
      loadAllData();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to create user account' });
    }
  };

  const handleAssignTeacher = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!assignTeacherId || !assignTeacherClassroomId) return;
    try {
      await adminService.assignTeacherToClassroom(Number(assignTeacherId), Number(assignTeacherClassroomId));
      setMessage({ type: 'success', text: 'Teacher successfully assigned to classroom!' });
      loadAllData();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to assign teacher' });
    }
  };

  const handleAssignParent = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!assignStudentId || !assignStudentParentId) return;
    try {
      await adminService.assignParentToStudent(Number(assignStudentId), Number(assignStudentParentId));
      setMessage({ type: 'success', text: 'Parent successfully linked to student!' });
      loadAllData();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Failed to assign parent' });
    }
  };

  // Filter students class-wise
  const filteredStudents = students.filter((student) => {
    const matchesSearch =
      student.fullName.toLowerCase().includes(studentSearchQuery.toLowerCase()) ||
      student.username.toLowerCase().includes(studentSearchQuery.toLowerCase()) ||
      (student.parentFullName && student.parentFullName.toLowerCase().includes(studentSearchQuery.toLowerCase()));

    if (selectedClassFilter === 'ALL') return matchesSearch;
    if (selectedClassFilter.startsWith('CLASS_')) {
      const classId = Number(selectedClassFilter.replace('CLASS_', ''));
      return student.classroomId === classId && matchesSearch;
    }
    if (selectedClassFilter.startsWith('GRADE_')) {
      const gradeNum = selectedClassFilter.replace('GRADE_', '');
      return student.classroomName.includes(gradeNum) && matchesSearch;
    }
    return matchesSearch;
  });

  // Filter teachers
  const filteredTeachers = teachers.filter(
    (teacher) =>
      teacher.fullName.toLowerCase().includes(teacherSearchQuery.toLowerCase()) ||
      teacher.username.toLowerCase().includes(teacherSearchQuery.toLowerCase()) ||
      teacher.classroomName.toLowerCase().includes(teacherSearchQuery.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-sky-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-6xl mx-auto pb-12">
      {/* Super Admin Top Header Banner */}
      <div className="bg-gradient-to-r from-sky-700 via-sky-800 to-indigo-900 text-white p-6 rounded-2xl shadow-md">
        <div className="flex items-center gap-4">
          <div className="p-3.5 bg-white/10 rounded-2xl backdrop-blur-sm border border-white/20">
            <Shield className="w-8 h-8 text-white" />
          </div>
          <div>
            <h1 className="text-2xl font-bold tracking-tight">Super Admin Console</h1>
            <p className="text-sky-100 text-sm mt-0.5">EduQuest Demo School • Administrative Control & Directory</p>
          </div>
        </div>
      </div>

      {message && (
        <div
          className={`p-4 rounded-xl text-sm flex items-center justify-between gap-2 shadow-sm ${
            message.type === 'success'
              ? 'bg-emerald-50 dark:bg-emerald-950/50 text-emerald-800 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800'
              : 'bg-red-50 dark:bg-red-950/50 text-red-800 dark:text-red-300 border border-red-200 dark:border-red-800'
          }`}
        >
          <div className="flex items-center gap-2">
            {message.type === 'success' ? <CheckCircle2 className="w-5 h-5 text-emerald-600" /> : <AlertCircle className="w-5 h-5 text-red-600" />}
            <span className="font-semibold">{message.text}</span>
          </div>
          <button onClick={() => setMessage(null)} className="text-gray-400 hover:text-gray-600">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* TAB 1: STUDENT DIRECTORY & CLASS-WISE MANAGEMENT */}
      {activeTab === 'STUDENTS' && (
        <div className="space-y-6">
          <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-6">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-gray-100 dark:border-gray-700 pb-4">
              <div>
                <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                  <GraduationCap className="w-5 h-5 text-sky-600" />
                  Student Directory & Basic Details
                </h2>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                  View and manage basic student profiles, assigned classrooms, and parent mappings.
                </p>
              </div>

              {/* Class Dropdown & Search Controls */}
              <div className="flex flex-wrap items-center gap-3">
                <div className="flex items-center gap-2 bg-gray-50 dark:bg-gray-900/50 px-3 py-1.5 rounded-xl border border-gray-200 dark:border-gray-700">
                  <Filter className="w-4 h-4 text-sky-600" />
                  <span className="text-xs font-semibold text-gray-600 dark:text-gray-300">Class Filter:</span>
                  <select
                    value={selectedClassFilter}
                    onChange={(e) => setSelectedClassFilter(e.target.value)}
                    className="text-xs font-bold bg-transparent text-gray-900 dark:text-white focus:outline-none"
                  >
                    <option value="ALL">All Classrooms ({students.length})</option>
                    {classrooms.map((c) => (
                      <option key={c.id} value={`CLASS_${c.id}`}>
                        Class {c.name} (Grade {c.grade})
                      </option>
                    ))}
                    <option value="GRADE_6">Grade 6th</option>
                    <option value="GRADE_7">Grade 7th</option>
                    <option value="GRADE_8">Grade 8th</option>
                    <option value="GRADE_9">Grade 9th</option>
                    <option value="GRADE_10">Grade 10th</option>
                  </select>
                </div>

                <div className="relative">
                  <Search className="w-4 h-4 text-gray-400 absolute left-3 top-2.5" />
                  <input
                    type="text"
                    placeholder="Search student or parent..."
                    value={studentSearchQuery}
                    onChange={(e) => setStudentSearchQuery(e.target.value)}
                    className="pl-9 pr-3 py-1.5 text-xs rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:border-sky-500 w-48"
                  />
                </div>
              </div>
            </div>

            {/* Basic Student List Table */}
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-gray-200 dark:border-gray-700 text-xs font-bold text-gray-500 uppercase">
                    <th className="py-3 px-4">Student Name</th>
                    <th className="py-3 px-4">Reg No / Username</th>
                    <th className="py-3 px-4">Assigned Classroom</th>
                    <th className="py-3 px-4">Parent Name & Account</th>
                    <th className="py-3 px-4 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 dark:divide-gray-700/50 text-sm">
                  {filteredStudents.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="text-center py-8 text-xs text-gray-400 italic">
                        No students match the selected class filter or search term.
                      </td>
                    </tr>
                  ) : (
                    filteredStudents.map((s) => {
                      const linkedParent = parents.find((p) => p.id === s.parentId);
                      return (
                        <tr key={s.id} className="hover:bg-gray-50 dark:hover:bg-gray-700/40 transition-colors">
                          <td className="py-3.5 px-4 font-bold text-gray-900 dark:text-white flex items-center gap-2">
                            <div className="w-8 h-8 rounded-full bg-sky-100 dark:bg-sky-900/50 flex items-center justify-center text-sky-700 dark:text-sky-300 font-bold text-xs">
                              {s.fullName.charAt(0)}
                            </div>
                            <span>{s.fullName}</span>
                          </td>
                          <td className="py-3.5 px-4 text-xs font-semibold text-gray-600 dark:text-gray-300">
                            @{s.username} <span className="text-gray-400">(ID: #{s.id})</span>
                          </td>
                          <td className="py-3.5 px-4">
                            <span className="px-2.5 py-1 text-xs font-bold bg-sky-100 dark:bg-sky-950 text-sky-800 dark:text-sky-300 rounded-full border border-sky-200 dark:border-sky-800">
                              Class {s.classroomName || 'Unassigned'}
                            </span>
                          </td>
                          <td className="py-3.5 px-4 font-medium text-emerald-600 dark:text-emerald-400 text-xs">
                            {s.parentFullName && s.parentFullName !== 'Unassigned' ? (
                              <div>
                                <p className="font-bold">{s.parentFullName}</p>
                                <p className="text-[11px] text-gray-400">@{linkedParent?.username || 'parent'}</p>
                              </div>
                            ) : (
                              <span className="text-gray-400 italic">No Parent Linked</span>
                            )}
                          </td>
                          <td className="py-3.5 px-4 text-center">
                            <button
                              onClick={() => handleOpenEditStudent(s)}
                              className="px-3 py-1.5 bg-sky-50 dark:bg-sky-950/60 hover:bg-sky-100 dark:hover:bg-sky-900 text-sky-700 dark:text-sky-300 rounded-xl text-xs font-bold border border-sky-200 dark:border-sky-800 transition-all flex items-center gap-1.5 mx-auto shadow-sm"
                            >
                              <Edit3 className="w-3.5 h-3.5" />
                              Edit Basic Info
                            </button>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: TEACHER DIRECTORY & MANAGEMENT */}
      {activeTab === 'TEACHERS' && (
        <div className="space-y-6">
          <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm space-y-6">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-gray-100 dark:border-gray-700 pb-4">
              <div>
                <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                  <Users className="w-5 h-5 text-emerald-600" />
                  Teacher Directory & Classroom Assignments
                </h2>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                  View basic teacher details and update assigned classrooms in database records.
                </p>
              </div>

              <div className="relative">
                <Search className="w-4 h-4 text-gray-400 absolute left-3 top-2.5" />
                <input
                  type="text"
                  placeholder="Search teacher by name..."
                  value={teacherSearchQuery}
                  onChange={(e) => setTeacherSearchQuery(e.target.value)}
                  className="pl-9 pr-3 py-1.5 text-xs rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:border-emerald-500 w-56"
                />
              </div>
            </div>

            {/* Basic Teacher List Table */}
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-gray-200 dark:border-gray-700 text-xs font-bold text-gray-500 uppercase">
                    <th className="py-3 px-4">Teacher Name</th>
                    <th className="py-3 px-4">Username</th>
                    <th className="py-3 px-4">Assigned Classroom</th>
                    <th className="py-3 px-4">Registration Date</th>
                    <th className="py-3 px-4 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 dark:divide-gray-700/50 text-sm">
                  {filteredTeachers.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="text-center py-8 text-xs text-gray-400 italic">
                        No teachers found matching your search.
                      </td>
                    </tr>
                  ) : (
                    filteredTeachers.map((t) => (
                      <tr key={t.id} className="hover:bg-gray-50 dark:hover:bg-gray-700/40 transition-colors">
                        <td className="py-3.5 px-4 font-bold text-gray-900 dark:text-white flex items-center gap-2">
                          <div className="w-8 h-8 rounded-full bg-emerald-100 dark:bg-emerald-900/50 flex items-center justify-center text-emerald-700 dark:text-emerald-300 font-bold text-xs">
                            {t.fullName.charAt(0)}
                          </div>
                          <span>{t.fullName}</span>
                        </td>
                        <td className="py-3.5 px-4 text-xs font-semibold text-gray-600 dark:text-gray-300">
                          @{t.username}
                        </td>
                        <td className="py-3.5 px-4">
                          <span className="px-2.5 py-1 text-xs font-bold bg-emerald-100 dark:bg-emerald-950 text-emerald-800 dark:text-emerald-300 rounded-full border border-emerald-200 dark:border-emerald-800">
                            Class {t.classroomName || 'Unassigned'}
                          </span>
                        </td>
                        <td className="py-3.5 px-4 text-xs text-gray-400">
                          {new Date(t.createdAt).toLocaleDateString()}
                        </td>
                        <td className="py-3.5 px-4 text-center">
                          <button
                            onClick={() => handleOpenEditTeacher(t)}
                            className="px-3 py-1.5 bg-emerald-50 dark:bg-emerald-950/60 hover:bg-emerald-100 dark:hover:bg-emerald-900 text-emerald-700 dark:text-emerald-300 rounded-xl text-xs font-bold border border-emerald-200 dark:border-emerald-800 transition-all flex items-center gap-1.5 mx-auto shadow-sm"
                          >
                            <Edit3 className="w-3.5 h-3.5" />
                            Edit Teacher Info
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: ACCOUNT CREATION & ROLE ASSIGNMENTS */}
      {activeTab === 'ACCOUNTS' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Create User Card */}
          <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl shadow-sm border border-gray-200 dark:border-gray-700">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
              <UserPlus className="w-5 h-5 text-sky-600 dark:text-sky-400" />
              Create New System Account
            </h2>

            <div className="flex gap-2 mb-6">
              {(['STUDENT', 'TEACHER', 'PARENT'] as const).map((r) => (
                <button
                  key={r}
                  type="button"
                  onClick={() => setRoleToCreate(r)}
                  className={`flex-1 py-2 px-3 text-xs font-bold rounded-xl transition-all ${
                    roleToCreate === r
                      ? 'bg-sky-600 text-white shadow-sm'
                      : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
                  }`}
                >
                  {r}
                </button>
              ))}
            </div>

            <form onSubmit={handleCreateUser} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">Full Name</label>
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="e.g. Anand Kumar"
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">Username</label>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="e.g. student_anand"
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">Password</label>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                />
              </div>

              {(roleToCreate === 'TEACHER' || roleToCreate === 'STUDENT') && (
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">Assign Classroom</label>
                  <select
                    value={selectedClassroomId}
                    onChange={(e) => setSelectedClassroomId(e.target.value ? Number(e.target.value) : '')}
                    className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                  >
                    <option value="">Select Classroom (Optional)</option>
                    {classrooms.map((c) => (
                      <option key={c.id} value={c.id}>
                        Class {c.name}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              {roleToCreate === 'STUDENT' && (
                <div>
                  <label className="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1">Assign Parent</label>
                  <select
                    value={selectedParentId}
                    onChange={(e) => setSelectedParentId(e.target.value ? Number(e.target.value) : '')}
                    className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                  >
                    <option value="">Select Parent (Optional)</option>
                    {parents.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.fullName} ({p.username})
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <button
                type="submit"
                className="w-full py-2.5 bg-sky-600 hover:bg-sky-700 text-white font-bold text-sm rounded-xl transition-all shadow-sm"
              >
                Create {roleToCreate.replace('_', ' ')} Account
              </button>
            </form>
          </div>

          {/* Assignments Forms Card */}
          <div className="space-y-6">
            <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl shadow-sm border border-gray-200 dark:border-gray-700">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                <Link className="w-5 h-5 text-sky-600 dark:text-sky-400" />
                Assign Teacher to Classroom
              </h2>
              <form onSubmit={handleAssignTeacher} className="space-y-3">
                <div className="grid grid-cols-2 gap-3">
                  <select
                    value={assignTeacherId}
                    onChange={(e) => setAssignTeacherId(e.target.value ? Number(e.target.value) : '')}
                    className="px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                    required
                  >
                    <option value="">Select Teacher</option>
                    {teachers.map((t) => (
                      <option key={t.id} value={t.id}>
                        {t.fullName} ({t.classroomName})
                      </option>
                    ))}
                  </select>

                  <select
                    value={assignTeacherClassroomId}
                    onChange={(e) => setAssignTeacherClassroomId(e.target.value ? Number(e.target.value) : '')}
                    className="px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                    required
                  >
                    <option value="">Select Classroom</option>
                    {classrooms.map((c) => (
                      <option key={c.id} value={c.id}>
                        Class {c.name}
                      </option>
                    ))}
                  </select>
                </div>
                <button
                  type="submit"
                  className="w-full py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-xl transition-all shadow-sm"
                >
                  Link Teacher to Classroom
                </button>
              </form>
            </div>

            <div className="bg-white dark:bg-gray-800 p-6 rounded-2xl shadow-sm border border-gray-200 dark:border-gray-700">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                <Users className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
                Assign Parent to Student
              </h2>
              <form onSubmit={handleAssignParent} className="space-y-3">
                <div className="grid grid-cols-2 gap-3">
                  <select
                    value={assignStudentId}
                    onChange={(e) => setAssignStudentId(e.target.value ? Number(e.target.value) : '')}
                    className="px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                    required
                  >
                    <option value="">Select Student</option>
                    {students.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.fullName} ({s.classroomName})
                      </option>
                    ))}
                  </select>

                  <select
                    value={assignStudentParentId}
                    onChange={(e) => setAssignStudentParentId(e.target.value ? Number(e.target.value) : '')}
                    className="px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white"
                    required
                  >
                    <option value="">Select Parent</option>
                    {parents.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.fullName}
                      </option>
                    ))}
                  </select>
                </div>
                <button
                  type="submit"
                  className="w-full py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-xs rounded-xl transition-all shadow-sm"
                >
                  Link Parent to Student
                </button>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* TAB 4: SYSTEM ANALYTICS */}
      {activeTab === 'ANALYTICS' && (
        <div className="space-y-6">
          {analytics && (
            <div className="space-y-4">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <BarChart3 className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
                School & System Telemetry Analytics
              </h2>
              <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-7 gap-4">
                <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Schools</p>
                  <p className="text-2xl font-bold text-sky-600 dark:text-sky-400">{analytics.totalSchools || 1}</p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Teachers</p>
                  <p className="text-2xl font-bold text-indigo-600 dark:text-indigo-400">{analytics.totalTeachers}</p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Students</p>
                  <p className="text-2xl font-bold text-emerald-600 dark:text-emerald-400">{analytics.totalStudents}</p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Modules</p>
                  <p className="text-2xl font-bold text-cyan-600 dark:text-cyan-400">{analytics.totalModules || 0}</p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Lessons</p>
                  <p className="text-2xl font-bold text-purple-600 dark:text-purple-400">{analytics.totalLessons || 0}</p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Quizzes</p>
                  <p className="text-2xl font-bold text-amber-600 dark:text-amber-400">{analytics.totalQuizzes}</p>
                </div>
                <div className="bg-white dark:bg-gray-800 p-4 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-sm col-span-2 sm:col-span-1">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Active Rate</p>
                  <p className="text-2xl font-bold text-teal-600 dark:text-teal-400">{analytics.activeRatePercentage}%</p>
                </div>
              </div>
            </div>
          )}

          {challengeAdminStats && (
            <div className="bg-white dark:bg-gray-800 p-5 rounded-2xl border border-amber-200 dark:border-amber-800/40 shadow-sm space-y-3">
              <h3 className="text-sm font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <Trophy className="w-4 h-4 text-amber-500" />
                Gamification & Weekly Challenge Performance
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div className="bg-amber-50/50 dark:bg-amber-950/30 p-3.5 rounded-xl border border-amber-100 dark:border-amber-900/40">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Total Platform Challenges</p>
                  <p className="text-2xl font-bold text-amber-600 dark:text-amber-400">{challengeAdminStats.totalPlatformChallenges ?? 0}</p>
                </div>
                <div className="bg-emerald-50/50 dark:bg-emerald-950/30 p-3.5 rounded-xl border border-emerald-100 dark:border-emerald-900/40">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Challenge Completion Rate</p>
                  <p className="text-2xl font-bold text-emerald-600 dark:text-emerald-400">{challengeAdminStats.challengeCompletionRate ?? 0}%</p>
                </div>
                <div className="bg-gray-50 dark:bg-gray-700/50 p-3.5 rounded-xl border border-gray-200 dark:border-gray-600">
                  <p className="text-xs font-semibold text-gray-500 uppercase">Archived Challenges</p>
                  <p className="text-2xl font-bold text-gray-700 dark:text-gray-300">{challengeAdminStats.archivedChallenges ?? 0}</p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* EDIT STUDENT MODAL */}
      {editingStudent && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-md w-full p-6 shadow-2xl border border-gray-200 dark:border-gray-700 space-y-5 animate-in fade-in zoom-in duration-200">
            <div className="flex justify-between items-center border-b border-gray-100 dark:border-gray-700 pb-3">
              <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <Edit3 className="w-4 h-4 text-sky-600" />
                Edit Basic Student Details
              </h3>
              <button onClick={() => setEditingStudent(null)} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveStudentEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-gray-700 dark:text-gray-300 mb-1">Student Full Name</label>
                <input
                  type="text"
                  required
                  value={editingStudentFullName}
                  onChange={(e) => setEditingStudentFullName(e.target.value)}
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:border-sky-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-700 dark:text-gray-300 mb-1">Assigned Classroom</label>
                <select
                  value={editingStudentClassroomId}
                  onChange={(e) => setEditingStudentClassroomId(e.target.value ? Number(e.target.value) : '')}
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:border-sky-500"
                >
                  <option value="">Unassigned</option>
                  {classrooms.map((c) => (
                    <option key={c.id} value={c.id}>
                      Class {c.name} (Grade {c.grade})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-700 dark:text-gray-300 mb-1">Linked Parent</label>
                <select
                  value={editingStudentParentId}
                  onChange={(e) => setEditingStudentParentId(e.target.value ? Number(e.target.value) : '')}
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:border-sky-500"
                >
                  <option value="">No Parent Linked</option>
                  {parents.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.fullName} (@{p.username})
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setEditingStudent(null)}
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-bold bg-sky-600 hover:bg-sky-700 text-white shadow-sm flex items-center gap-1.5"
                >
                  <Save className="w-3.5 h-3.5" />
                  Save Student Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* EDIT TEACHER MODAL */}
      {editingTeacher && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4 z-50">
          <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-md w-full p-6 shadow-2xl border border-gray-200 dark:border-gray-700 space-y-5 animate-in fade-in zoom-in duration-200">
            <div className="flex justify-between items-center border-b border-gray-100 dark:border-gray-700 pb-3">
              <h3 className="text-base font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <Edit3 className="w-4 h-4 text-emerald-600" />
                Edit Basic Teacher Details
              </h3>
              <button onClick={() => setEditingTeacher(null)} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveTeacherEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-gray-700 dark:text-gray-300 mb-1">Teacher Full Name</label>
                <input
                  type="text"
                  required
                  value={editingTeacherFullName}
                  onChange={(e) => setEditingTeacherFullName(e.target.value)}
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:border-emerald-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-gray-700 dark:text-gray-300 mb-1">Assigned Classroom</label>
                <select
                  value={editingTeacherClassroomId}
                  onChange={(e) => setEditingTeacherClassroomId(e.target.value ? Number(e.target.value) : '')}
                  className="w-full px-3 py-2 text-sm rounded-xl border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:border-emerald-500"
                >
                  <option value="">Unassigned</option>
                  {classrooms.map((c) => (
                    <option key={c.id} value={c.id}>
                      Class {c.name} (Grade {c.grade})
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setEditingTeacher(null)}
                  className="px-4 py-2 rounded-xl text-xs font-bold bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-bold bg-emerald-600 hover:bg-emerald-700 text-white shadow-sm flex items-center gap-1.5"
                >
                  <Save className="w-3.5 h-3.5" />
                  Save Teacher Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
