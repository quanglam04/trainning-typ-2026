package Demo.Spring.Publisher;

import Demo.Spring.DTO.Event.OrderCreatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventListener {
    private final RabbitMQJsonProducer rabbitMQJsonProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterOrderCommit(OrderCreatedMessage message) {
        try {
            rabbitMQJsonProducer.sendOrderCreated(message);
            log.info("Đã gửi event order.created cho orderId={}", message.orderId());
        } catch (AmqpException exception) {
            // The order is already committed; log the notification failure without misreporting
            // the order creation as failed to the client.
            log.error("Không gửi được event order.created cho orderId={}", message.orderId(), exception);
        }
    }
}
