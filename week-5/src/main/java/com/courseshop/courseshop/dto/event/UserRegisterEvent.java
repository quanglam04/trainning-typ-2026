package com.courseshop.courseshop.dto.event;

import java.time.Instant;
import java.util.UUID;

import com.courseshop.courseshop.model.User;

/** Payload gọn mô tả sự kiện đăng ký user; không mang password, role hay quan hệ JPA. */
public class UserRegisterEvent {

    private UUID eventId;
    private Long userId;
    private String name;
    private String email;
    private Instant occurredAt;

    public UserRegisterEvent() {}

    public UserRegisterEvent(UUID eventId, Long userId, String name, String email, Instant occurredAt) {
        this.eventId = eventId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.occurredAt = occurredAt;
    }

    /** Tạo payload sự kiện từ user đã lưu thành công trong database. */
    public static UserRegisterEvent from(User user) {
        return new UserRegisterEvent(
                UUID.randomUUID(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                Instant.now());
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
