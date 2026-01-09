package org.example.todo.controller;


import org.example.todo.domain.TaskCreateDto;
import org.example.todo.domain.TaskCreatedDto;
import org.example.todo.domain.TaskEventDto;
import org.example.todo.service.TaskEventPublisher;
import org.example.todo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@CrossOrigin
@RequestMapping("/task")
@RestController
public class ToDoController {
    private final TaskService taskService;

    public ToDoController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskCreatedDto> createTask(@RequestBody TaskCreateDto taskCreateDto) {
        var task = taskService.createTask(taskCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskCreatedDto.from(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskCreatedDto>> getTasks() {
        var tasks = taskService.findAll()
                .stream()
                .map(TaskCreatedDto::from)
                .toList();

        return ResponseEntity.ok(tasks);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskCreatedDto> toggleCompleted(@PathVariable Long id) {
        var task = taskService.toggleCompleted(id);
        return ResponseEntity.ok(TaskCreatedDto.from(task));
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TaskEventDto> streamEvents() {
        return taskService.streamEvents();
    }
    
}