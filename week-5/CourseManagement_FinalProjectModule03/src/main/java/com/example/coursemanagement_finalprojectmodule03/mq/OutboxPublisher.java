package com.example.coursemanagement_finalprojectmodule03.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mq.publisher.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPublisher {
    private final OutboxEventRepository outboxRepo;
    private final RabbitTemplate rabbitTemplate;

    // API ghi DB trước, tác vụ này mới gửi message sang RabbitMQ.
    @Scheduled(fixedDelayString = "${app.mq.publish-interval-ms:3000}")
    public void publishPending() {
        for (OutboxEvent event : outboxRepo.findTop20BySentAtIsNullOrderByCreatedAtAsc()) {
            try {
                CorrelationData correlation = new CorrelationData(event.getId());
                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.EXCHANGE,
                        RabbitMqConfig.ROUTING_KEY,
                        event.toMessage(),
                        message -> {
                            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            return message;
                        },
                        correlation
                );

                var confirm = correlation.getFuture().get(5, TimeUnit.SECONDS);
                if (!confirm.isAck() || correlation.getReturned() != null) {
                    throw new IllegalStateException("RabbitMQ chưa xác nhận message: " + event.getId());
                }

                event.setSentAt(LocalDateTime.now());
                outboxRepo.save(event);
                log.info("MQ published eventId={}", event.getId());
            } catch (Exception e) {
                // Không đánh dấu đã gửi để lần quét sau thử lại.
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                log.warn("MQ publish lỗi, sẽ thử lại eventId={}: {}", event.getId(), e.getMessage());
                break;
            }
        }
    }
}
