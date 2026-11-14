package Demo.Spring.Repository;

import Demo.Spring.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryOrderItem extends JpaRepository<OrderItem, String> {
}
