package com.epam.resourceservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author www.epam.com
 */
@Component
@RequiredArgsConstructor
public class RMQPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Queue createQueue;
    private final Queue deleteQueue;

    public void publishCreationEvent(String message) {
        rabbitTemplate.convertAndSend(this.createQueue.getName(), message);
    }

    public void publishDeleteEvent(String message) {
        rabbitTemplate.convertAndSend(this.deleteQueue.getName(), message);
    }
}
