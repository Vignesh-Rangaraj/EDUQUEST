import api from './api';
import { Teacher, Student, Parent, Classroom, AdminAnalytics, User } from '../types';

export const adminService = {
  createTeacher: async (data: { username: string; password: string; fullName: string; classroomId?: number }): Promise<Teacher> => {
    const res = await api.post<Teacher>('/admin/teachers', data);
    return res.data;
  },
  createStudent: async (data: { username: string; password: string; fullName: string; classroomId?: number; parentId?: number }): Promise<Student> => {
    const res = await api.post<Student>('/admin/students', data);
    return res.data;
  },
  createParent: async (data: { username: string; password: string; fullName: string }): Promise<Parent> => {
    const res = await api.post<Parent>('/admin/parents', data);
    return res.data;
  },
  createSuperAdmin: async (data: { username: string; password: string; fullName: string }): Promise<User> => {
    const res = await api.post<User>('/admin/superadmins', data);
    return res.data;
  },
  getAllSuperAdmins: async (): Promise<User[]> => {
    const res = await api.get<User[]>('/admin/superadmins');
    return res.data;
  },
  assignTeacherToClassroom: async (teacherId: number, classroomId: number): Promise<Teacher> => {
    const res = await api.post<Teacher>('/admin/teachers/assign-classroom', { teacherId, classroomId });
    return res.data;
  },
  assignParentToStudent: async (studentId: number, parentId: number): Promise<Student> => {
    const res = await api.post<Student>('/admin/students/assign-parent', { studentId, parentId });
    return res.data;
  },
  getAllTeachers: async (): Promise<Teacher[]> => {
    const res = await api.get<Teacher[]>('/admin/teachers');
    return res.data;
  },
  getAllStudents: async (): Promise<Student[]> => {
    const res = await api.get<Student[]>('/admin/students');
    return res.data;
  },
  getAllParents: async (): Promise<Parent[]> => {
    const res = await api.get<Parent[]>('/admin/parents');
    return res.data;
  },
  getAllClassrooms: async (): Promise<Classroom[]> => {
    const res = await api.get<Classroom[]>('/admin/classrooms');
    return res.data;
  },
  getAnalytics: async (): Promise<AdminAnalytics> => {
    const res = await api.get<AdminAnalytics>('/admin/analytics');
    return res.data;
  },
  updateStudent: async (id: number, data: { fullName?: string; classroomId?: number; parentId?: number }): Promise<Student> => {
    const res = await api.put<Student>(`/admin/students/${id}`, data);
    return res.data;
  },
  updateTeacher: async (id: number, data: { fullName?: string; classroomId?: number }): Promise<Teacher> => {
    const res = await api.put<Teacher>(`/admin/teachers/${id}`, data);
    return res.data;
  }
};
