package Demo.Spring.Repository;

import Demo.Spring.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryProduct extends JpaRepository<Product,String> {
}
