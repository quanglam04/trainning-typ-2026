package com.example.coursemanagement_finalprojectmodule03.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentNotificationConsumer {
    private final EnrollmentNotificationHandler handler;

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void receive(EnrollmentCreatedEvent event) {
        handler.handle(event);
        log.info("MQ processed eventId={}", event.eventId());
        // Không ném lỗi -> Spring ACK. Có lỗi -> retry, sau đó chuyển sang DLQ.
    }
}
