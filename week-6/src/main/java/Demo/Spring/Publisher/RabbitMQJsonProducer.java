package Demo.Spring.Publisher;

import Demo.Spring.DTO.Event.OrderCreatedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitMQJsonProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendOrderCreated(OrderCreatedMessage message) {
        rabbitTemplate.convertAndSend("exchange", "order.created", message);
    }
}
