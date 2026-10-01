import api from './api';
import { Teacher, Student, TeacherAnalytics } from '../types';

export interface ClassroomProgressStudent {
  studentId: number;
  fullName: string;
  username: string;
  xp: number;
  level: number;
  completedCount: number;
  pendingCount: number;
  totalActivities: number;
  completionPercentage: number;
  lastActivityAt: string | null;
}

export interface ClassroomProgressAnalytics {
  classroomId: number | null;
  classroomName: string;
  studentCount: number;
  availableActivityCount: number;
  completedAssignments: number;
  pendingAssignments: number;
  classroomCompletionPercentage: number;
  students: ClassroomProgressStudent[];
}

export const teacherService = {
  getProfile: async (): Promise<Teacher> => {
    const res = await api.get<Teacher>('/teacher/profile');
    return res.data;
  },
  getAssignedStudents: async (): Promise<Student[]> => {
    const res = await api.get<Student[]>('/teacher/students');
    return res.data;
  },
  getAnalytics: async (): Promise<TeacherAnalytics> => {
    const res = await api.get<TeacherAnalytics>('/teacher/analytics');
    return res.data;
  },
  getClassroomProgressAnalytics: async (): Promise<ClassroomProgressAnalytics> => {
    const res = await api.get<ClassroomProgressAnalytics>('/teacher/analytics/classroom-progress');
    return res.data;
  }
};
