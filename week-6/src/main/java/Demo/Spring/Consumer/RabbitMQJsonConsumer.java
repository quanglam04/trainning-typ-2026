package Demo.Spring.Consumer;

import Demo.Spring.DTO.Event.OrderCreatedMessage;
import Demo.Spring.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQJsonConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = "order_confirmation_queue")
    public void receiveMessage(OrderCreatedMessage message) {
        log.info("Gửi email xác nhận cho orderId={}", message.orderId());
        String content = "Bạn đã đặt hàng thành công. Mã đơn: " + message.orderId()
                + ". Tổng tiền: " + message.totalAmount();
        emailService.sendEmail(message.customerEmail(), "Xác nhận đặt hàng", content);
    }
}
