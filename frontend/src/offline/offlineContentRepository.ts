import { db, LocalLessonContent, LocalQuizQuestion } from './db';
import { LessonContent, QuizQuestion } from '../types';

export const offlineContentRepository = {
  async saveLessonContent(content: LessonContent): Promise<void> {
    if (content.id != null) {
      await db.lessonContent.put({ ...content, id: content.id } as LocalLessonContent);
    } else {
      const existing = await db.lessonContent.where('activityId').equals(content.activityId).first();
      await db.lessonContent.put({ ...content, id: existing?.id ?? content.activityId } as LocalLessonContent);
    }
  },

  async getLessonContent(activityId: number): Promise<LessonContent | undefined> {
    return db.lessonContent.where('activityId').equals(activityId).first();
  },

  async saveQuizQuestions(activityId: number, questions: QuizQuestion[]): Promise<void> {
    await db.transaction('rw', db.quizQuestions, async () => {
      await db.quizQuestions.where('activityId').equals(activityId).delete();
      if (questions.length) {
        await db.quizQuestions.bulkPut(questions.map((question, index) => ({
          ...question,
          id: question.id ?? activityId * 100000 + index + 1,
          activityId
        } as LocalQuizQuestion)));
      }
    });
  },

  async getQuizQuestions(activityId: number): Promise<QuizQuestion[]> {
    return db.quizQuestions.where('activityId').equals(activityId).sortBy('displayOrder');
  }
};
