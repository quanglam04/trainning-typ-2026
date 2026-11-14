package Demo.Spring.Repository;

import Demo.Spring.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositoryCartItem extends JpaRepository<CartItem, String> {
    List<CartItem> findByCartId(String cartId);
    Optional<CartItem> findByIdAndCartId(String id, String cartId);
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);
    void deleteByCartId(String cartId);
}
