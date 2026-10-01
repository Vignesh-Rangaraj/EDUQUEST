import Dexie, { Table } from 'dexie';

export interface LocalStudent {
  id?: number;
  userAccountId: number;
  fullName: string;
  username: string;
  grade?: number;
  section?: string;
  xp?: number;
  level?: number;
  profileData?: string;
  lastUpdated: string;
}

export interface LocalModule {
  id: number;
  title: string;
  description: string;
  subject: string;
  difficultyLevel: string;
  estimatedMinutes?: number;
  classroomId?: number;
  status: string;
  createdByTeacherId?: number;
}

export interface LocalActivity {
  id: number;
  moduleId?: number;
  title: string;
  description: string;
  subject: string;
  activityType: string;
  gameType?: string;
  activityMetadataJson?: string;
  status: string;
  displayOrder?: number;
  prerequisiteActivityId?: number;
  unlockType?: string;
  unlockValue?: string;
  xpReward?: number;
  visibleToStudents?: boolean;
  assignedClassroomId?: number;
  createdByTeacherId?: number;
}

export interface LocalLessonContent {
  id: number;
  activityId: number;
  content: string;
  estimatedMinutes?: number;
}

export interface LocalQuizQuestion {
  id: number;
  activityId: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctAnswer: string;
  explanation?: string;
  displayOrder?: number;
}

export interface LocalProgress {
  id: string; // "studentId-activityId"
  studentId: number;
  activityId: number;
  score: number;
  bestScore?: number;
  attemptCount?: number;
  completed: boolean;
  completedAt: string;
  synced: boolean;
  /** Monotonically increasing per student/activity local revision. */
  version?: number;
  xpAwarded?: number;
}

export interface LocalXpTransaction {
  id: string;
  studentId: number;
  activityId: number;
  xp: number;
  synced: boolean;
  createdAt: string;
}

export interface LocalModuleProgress {
  id: string; // "studentId-moduleId"
  studentId: number;
  moduleId: number;
  completedActivities: number;
  totalActivities: number;
  completionPercentage: number;
  completed: boolean;
}

export interface LocalSyncQueueItem {
  id: string;
  actionType: string;
  payload: string;
  createdAt: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  retryCount: number;
  nextAttemptAt?: string;
  lastError?: string;
}

export interface LocalSetting {
  key: string;
  value: string;
}

export class EduQuestOfflineDB extends Dexie {
  students!: Table<LocalStudent, number>;
  modules!: Table<LocalModule, number>;
  activities!: Table<LocalActivity, number>;
  lessonContent!: Table<LocalLessonContent, number>;
  quizQuestions!: Table<LocalQuizQuestion, number>;
  progress!: Table<LocalProgress, string>;
  moduleProgress!: Table<LocalModuleProgress, string>;
  syncQueue!: Table<LocalSyncQueueItem, string>;
  settings!: Table<LocalSetting, string>;
  xpTransactions!: Table<LocalXpTransaction, string>;

  constructor() {
    super('EduQuestOfflineDB');
    this.version(2).stores({
      students: '++id, userAccountId, username',
      modules: 'id, subject, difficultyLevel, status, classroomId',
      activities: 'id, moduleId, subject, activityType, status, displayOrder, prerequisiteActivityId',
      lessonContent: 'id, activityId',
      quizQuestions: 'id, activityId, displayOrder',
      progress: 'id, studentId, activityId, synced',
      moduleProgress: 'id, studentId, moduleId',
      syncQueue: 'id, status, actionType, createdAt',
      settings: 'key'
    });
    this.version(3).stores({
      students: '++id, userAccountId, username',
      modules: 'id, subject, difficultyLevel, status, classroomId',
      activities: 'id, moduleId, subject, activityType, status, displayOrder, prerequisiteActivityId',
      lessonContent: 'id, activityId',
      quizQuestions: 'id, activityId, displayOrder',
      progress: 'id, studentId, activityId, synced',
      moduleProgress: 'id, studentId, moduleId',
      syncQueue: 'id, status, actionType, createdAt',
      settings: 'key',
      xpTransactions: 'id, studentId, activityId, synced, createdAt'
    });
  }
}

export const db = new EduQuestOfflineDB();
