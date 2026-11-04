package com.courseshop.courseshop.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.dto.event.UserRegisterEvent;

@Service
public class RabbitMQJsonProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.json.key}")
    private String routingJsonKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQJsonProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQJsonProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /** Publish payload đăng ký gọn; converter của RabbitTemplate sẽ tuần tự hóa thành JSON. */
    public void sendJsonMessage(UserRegisterEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingJsonKey, event);
        LOGGER.info("User registration event sent: eventId={}, userId={}",
                event.getEventId(), event.getUserId());
    }

}
