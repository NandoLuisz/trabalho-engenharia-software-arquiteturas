package org.example.todo.domain;

public record TaskEventDto(
        TaskEventType type,
        Long taskId,
        TaskCreatedDto task
) {
    public static TaskEventDto heartbeat() {
        return new TaskEventDto(TaskEventType.HEARTBEAT, null, null);
    }

}
