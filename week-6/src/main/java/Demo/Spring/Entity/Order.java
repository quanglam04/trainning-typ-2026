package Demo.Spring.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "create_at",  nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "total_amount", nullable = false)
    private double totalAmount;
    @Column(name = "shipping_address",  nullable = false)
    private String shippingAddress;
    @Column(name = "payment_method",   nullable = false)
    private String paymentMethod;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
