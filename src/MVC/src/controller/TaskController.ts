import { TaskModel } from '../model/TaskModel'
import type { Task } from '../model/Task'
import type { TaskEvent } from '../model/TaskEvent'
import type { Dispatch, SetStateAction } from 'react'

export class TaskController {
  private model = new TaskModel()
  private eventSource?: EventSource

  async loadTasks(
    setTasks: Dispatch<SetStateAction<Task[]>>
  ) {
    const tasks = await this.model.getAll()
    setTasks(tasks)
  }

  async createTask(
    task: Omit<Task, 'id' | 'createdAt'>,
    clearForm: () => void
  ) {
    await this.model.create(task)
    clearForm()
  }

  async deleteTask(id: number) {
    await this.model.delete(id)
  }

  async toggleTask(id: number) {
    await this.model.toggleCompleted(id)
  }

  subscribeToEvents(
    setTasks: Dispatch<SetStateAction<Task[]>>
  ) {
    this.eventSource = new EventSource(
      'https://todo-engenharia-software.onrender.com/task/events'
    )

    this.eventSource.onmessage = e => {
      const event: TaskEvent = JSON.parse(e.data)

      if (event.type === 'HEARTBEAT') return

      setTasks((prev: Task[]) => {
        switch (event.type) {
          case 'CREATED':
            return [...prev, event.task]

          case 'UPDATED':
            return prev.map(t =>
              t.id === event.task.id ? event.task : t
            )

          case 'DELETED':
            return prev.filter(t => t.id !== event.taskId)

          default:
            return prev
        }
      })
    }
  }

  unsubscribe() {
    this.eventSource?.close()
  }
}
