import { db, LocalXpTransaction } from './db';

export const offlineXpRepository = {
  async getPendingXp(studentId: number): Promise<number> {
    const transactions = await db.xpTransactions.where('studentId').equals(Number(studentId)).toArray();
    return transactions.filter((transaction) => !transaction.synced).reduce((total, transaction) => total + transaction.xp, 0);
  },

  async getStudentTransactions(studentId: number): Promise<LocalXpTransaction[]> {
    return db.xpTransactions.where('studentId').equals(Number(studentId)).toArray();
  }
};
