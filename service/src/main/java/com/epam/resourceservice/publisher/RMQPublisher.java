package com.epam.resourceservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class RMQPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Queue createQueue;
    private final Queue deleteQueue;

    public void publishCreationEvent(String message) {
        rabbitTemplate.convertAndSend(this.createQueue.getName(), message);
        log.info("Create event was sent to MQ, resource id: {}", message);
    }

    public void publishDeleteEvent(String message) {
        rabbitTemplate.convertAndSend(this.deleteQueue.getName(), message);
        log.info("Delete event was sent to MQ, resource id: {}", message);
    }
}
