package Demo.Spring.DTO.Event;

public record OrderCreatedMessage(
        String orderId,
        String customerEmail,
        double totalAmount) {
}
