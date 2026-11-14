package Demo.Spring.Repository;

import Demo.Spring.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryCart extends JpaRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);

}
