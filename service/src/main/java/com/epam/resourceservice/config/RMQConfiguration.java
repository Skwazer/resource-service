package com.epam.resourceservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.epam.com
 */
@Configuration
public class RMQConfiguration {
    @Value("${spring.rabbitmq.queue.create}")
    private String createQueue;

    @Value("${spring.rabbitmq.queue.delete}")
    private String deleteQueue;


    @Bean
    public Queue createQueue() {
        return new Queue(createQueue, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(deleteQueue, true);
    }
}
