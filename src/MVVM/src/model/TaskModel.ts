import { api } from '../service/api'
import type { Task } from './Task'

export type TaskEvent =
  | { type: 'CREATED'; task: Task }
  | { type: 'UPDATED'; task: Task }
  | { type: 'DELETED'; taskId: number }
  | { type: 'HEARTBEAT' }


export class TaskModel {
  async getAll(): Promise<Task[]> {
    const res = await api.get<Task[]>('/task')
    return res.data
  }

  async create(task: Omit<Task, 'id' | 'createdAt'>): Promise<void> {
    await api.post('/task', task)
  }

  async delete(id: number): Promise<void> {
    await api.delete(`/task/${id}`)
  }

  async toggle(id: number): Promise<void> {
    await api.patch(`/task/${id}/toggle`)
  }

  createEventSource(onEvent: (event: TaskEvent) => void): EventSource {
    const es = new EventSource("https://todo-engenharia-software.onrender.com/task/events")

    es.onmessage = e => {
      const event: TaskEvent = JSON.parse(e.data)
      if (event.type !== 'HEARTBEAT') {
        onEvent(event)
      }
    }

    return es
  }
}
