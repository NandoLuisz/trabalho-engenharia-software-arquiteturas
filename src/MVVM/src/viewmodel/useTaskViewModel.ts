//useTaskViewModel.ts
import { useEffect, useRef, useState, useCallback } from 'react'
import { TaskModel, type TaskEvent } from '../model/TaskModel'
import type { Task } from '../model/Task'

export function useTaskViewModel() {
  const [tasks, setTasks] = useState<Task[]>([])

  const modelRef = useRef<TaskModel | null>(null)
  const eventSourceRef = useRef<EventSource | null>(null)

  const handleEvent = useCallback((event: TaskEvent) => {
    if (event.type === 'HEARTBEAT') return

    setTasks((prev: Task[]): Task[] => {
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
  }, [])

  useEffect(() => {
    if (!modelRef.current) {
      modelRef.current = new TaskModel()
    }

    modelRef.current.getAll().then(setTasks)

    eventSourceRef.current =
      modelRef.current.createEventSource(handleEvent)

    return () => {
      eventSourceRef.current?.close()
      eventSourceRef.current = null
    }
  }, [handleEvent])

  return {
    tasks,

    createTask: (task: Omit<Task, 'id' | 'createdAt'>) =>
      modelRef.current!.create(task),

    deleteTask: (id: number) =>
      modelRef.current!.delete(id),

    toggleTask: (id: number) =>
      modelRef.current!.toggle(id),
  }

}
