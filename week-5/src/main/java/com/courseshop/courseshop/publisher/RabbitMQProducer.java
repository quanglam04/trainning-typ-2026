package com.courseshop.courseshop.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Producer gửi message từ ứng dụng CourseShop tới RabbitMQ. */
@Service
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /** Gửi message tới exchange với routing key đã cấu hình và ghi log kết quả gửi. */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        LOGGER.info(String.format("Message sen -> %s", message));
    }
}
