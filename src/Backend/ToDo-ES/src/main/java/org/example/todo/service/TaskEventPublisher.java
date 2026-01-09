package org.example.todo.service;

import org.example.todo.domain.TaskEventDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


import java.time.Duration;

@Component
public class TaskEventPublisher {

    private final Sinks.Many<TaskEventDto> sink =
            Sinks.many().replay().latest();

    public void publish(TaskEventDto event) {
        Sinks.EmitResult result = sink.tryEmitNext(event);
        if (result.isFailure()) {
            System.err.println("SSE emit failed: " + result);
        }
    }

    public Flux<TaskEventDto> getEvents() {
        return sink.asFlux()
                .mergeWith(
                        Flux.interval(Duration.ofSeconds(15))
                                .map(i -> TaskEventDto.heartbeat())
                );
    }
}

