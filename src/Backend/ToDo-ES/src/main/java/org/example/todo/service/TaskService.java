package org.example.todo.service;

import org.example.todo.domain.*;
import org.example.todo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher eventPublisher;

    public TaskService(TaskRepository taskRepository,
                       TaskEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public Task createTask(TaskCreateDto taskCreateDto) {
        var newTask = new Task();
        newTask.setTitle(taskCreateDto.title());
        newTask.setDescription(taskCreateDto.description());
        newTask.setPriority(Priority.valueOf(taskCreateDto.priority()));

        var savedTask = taskRepository.save(newTask);

        eventPublisher.publish(
                new TaskEventDto(
                        TaskEventType.CREATED,
                        savedTask.getId(),
                        TaskCreatedDto.from(savedTask)
                )
        );

        return savedTask;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return;
        }

        taskRepository.deleteById(id);

        eventPublisher.publish(
                new TaskEventDto(TaskEventType.DELETED, id, null)
        );
    }

    public Task toggleCompleted(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task não encontrada"));

        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);

        eventPublisher.publish(
                new TaskEventDto(
                        TaskEventType.UPDATED,
                        task.getId(),
                        TaskCreatedDto.from(task)
                )
        );

        return task;
    }

    public Flux<TaskEventDto> streamEvents() {
        return eventPublisher.getEvents();
    }


}
