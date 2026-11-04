package com.courseshop.courseshop.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.dto.event.UserRegisterEvent;
import com.courseshop.courseshop.service.WelcomeEmailService;

@Service 
public class RabbitMQJsonConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQJsonConsumer.class);

    private final WelcomeEmailService welcomeEmailService;

    public RabbitMQJsonConsumer(WelcomeEmailService welcomeEmailService) {
        this.welcomeEmailService = welcomeEmailService;
    }
    
    @RabbitListener(queues = "${rabbitmq.queue.json.name}")
    public void consumeJsonMessage(UserRegisterEvent event) {
        LOGGER.info("Received user registration event: eventId={}, userId={}, email={}",
                event.getEventId(), event.getUserId(), event.getEmail());
        welcomeEmailService.sendWelcomeEmail(event);
    }
}
