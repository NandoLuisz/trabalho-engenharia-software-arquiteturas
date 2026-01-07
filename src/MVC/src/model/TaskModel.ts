//TaskModel.ts
import { api } from '../service/api'
import type { Task } from './Task'

export class TaskModel {

  async getAll(): Promise<Task[]> {
    const response = await api.get<Task[]>('/task')
    return response.data
  }

  async create(
    task: Omit<Task, 'id' | 'createdAt'>
  ): Promise<Task> {
    const response = await api.post<Task>('/task', task)
    return response.data
  }

  async delete(id: number): Promise<void> {
    await api.delete(`/task/${id}`)
  }

  async toggleCompleted(id: number): Promise<Task> {
    const response = await api.patch<Task>(`/task/${id}/toggle`)
    return response.data
  }
}

