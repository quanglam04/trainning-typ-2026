interface MessageSender {
    void send(String message);
}

class EmailSender implements MessageSender {
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

class SmsSender implements MessageSender {
    @Override
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}

class FakeMessageSender implements MessageSender {
    private String lastMessage;

    @Override
    public void send(String message) {
        lastMessage = message;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}

class NotificationService {
    private final MessageSender sender;

    public NotificationService(MessageSender sender) {
        this.sender = sender;
    }

    public void notifyUser(String message) {
        sender.send(message);
    }
}

public class DiIocDemo {
    public static void main(String[] args) {
        NotificationService emailService = new NotificationService(new EmailSender());
        emailService.notifyUser("Don hang da duoc xac nhan");

        NotificationService smsService = new NotificationService(new SmsSender());
        smsService.notifyUser("Don hang da duoc xac nhan");

        FakeMessageSender fake = new FakeMessageSender();
        NotificationService testService = new NotificationService(fake);
        testService.notifyUser("Kiem thu DI");

        System.out.println("Fake captured: " + fake.getLastMessage());
        System.out.println("Test result: " + ("Kiem thu DI".equals(fake.getLastMessage()) ? "PASS" : "FAIL"));
    }
}
