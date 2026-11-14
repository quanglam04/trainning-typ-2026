package Demo.Spring.Repository;

import Demo.Spring.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryOrder extends JpaRepository<Order, String> {
}
