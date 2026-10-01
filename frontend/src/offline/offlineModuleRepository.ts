import { db, LocalModule } from './db';
import { Module } from '../types';

export const offlineModuleRepository = {
  async saveModules(modules: Module[]): Promise<void> {
    await db.modules.bulkPut(modules.map((module) => ({ ...module } as LocalModule)));
  },

  async getStudentModules(): Promise<Module[]> {
    return db.modules.where('status').equals('PUBLISHED').toArray() as Promise<Module[]>;
  },

  async getModuleById(moduleId: number): Promise<Module | undefined> {
    return db.modules.get(moduleId) as Promise<Module | undefined>;
  }
};
