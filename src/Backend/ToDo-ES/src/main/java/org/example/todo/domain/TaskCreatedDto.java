package org.example.todo.domain;

import java.time.LocalDateTime;

public record TaskCreatedDto(
        Long id,
        String title,
        String description,
        String priority,
        boolean isCompleted,
        LocalDateTime createdAt
) {
    public static TaskCreatedDto from(Task task) {
        return new TaskCreatedDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority().name(),
                task.isCompleted(),
                task.getCreatedAt()
        );
    }
}

