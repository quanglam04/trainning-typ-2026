package Demo.Spring.Service;

import Demo.Spring.DTO.Request.RequestProduct;
import Demo.Spring.DTO.Response.SuccessResponse;
import Demo.Spring.DTO.Response.ResponseProduct;
import Demo.Spring.Entity.Product;
import Demo.Spring.Entity.User;
import Demo.Spring.Exceptions.AppException;
import Demo.Spring.Mapper.MapperProduct;
import Demo.Spring.Repository.RepositoryProduct;
import Demo.Spring.Repository.RepositoryUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static Demo.Spring.Exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int PRODUCT_PAGE_SIZE = 10;
    private final RepositoryProduct repositoryProduct;
    private final RepositoryUser repositoryUser;
    private final MapperProduct mapperProduct;

    @CacheEvict(cacheNames = "product_list", allEntries = true)
    public ResponseEntity<Object> created(RequestProduct request, String username){
        List<User> list = repositoryUser.findByusername(username);
        User seller = new User();
        if(list.isEmpty()){
            throw new AppException(USER_NOT_EXIST);
        }
        else seller = list.get(0);

        if(request.getQuantity() == null||request.getQuantity() <= 0){
            throw new AppException(TOKEN_ERROR);
        }

        Product newProduct = mapperProduct.toProduct(request);
        newProduct.setSeller(seller);
        newProduct.setCreatedAt(LocalDateTime.now());
        Product savedProduct = repositoryProduct.save(newProduct);

        ResponseProduct response = new ResponseProduct(
                savedProduct.getId(), savedProduct.getTitle(), savedProduct.getDescription(),
                savedProduct.getPrice(), savedProduct.getImageUrl(), savedProduct.getQuantity(),
                savedProduct.getCreatedAt(),
                seller.getUsername());
        return ResponseEntity.ok().body(new SuccessResponse<>(200, "Success", response));
    }
    @CacheEvict(cacheNames = {"product_list", "product_detail"}, allEntries = true)
    public ResponseEntity<Object> delete(String id, String username){
        Optional<Product> product = repositoryProduct.findById(id);

        if(product.isEmpty()) throw new AppException(PRODUCT_NOT_EXIST);
        if(!product.get().getSeller().getUsername().equals(username)) {
            throw new AppException(USER_ERROR);
        }
        repositoryProduct.delete(product.get());
        return ResponseEntity.ok().body(new SuccessResponse<>(200, "Success", null));
    }
    @Cacheable(cacheNames = "product_list", key = "#page")
    public SuccessResponse<Page<ResponseProduct>> getInformation(int page){
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page phải >= 0");
        }
        Page<Product> products = repositoryProduct.findAll(
                PageRequest.of(page, PRODUCT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<ResponseProduct> response = products.map(mapperProduct::toResponseProduct);
        return new SuccessResponse<>(200, "Success", response);
    }
    @Cacheable(cacheNames = "product_detail", key = "#id")
    public SuccessResponse<ResponseProduct> getById(String id){
        Optional<Product> product = repositoryProduct.findById(id);
        if(product.isEmpty()) throw new AppException(PRODUCT_NOT_EXIST);

        ResponseProduct responseProduct = mapperProduct.toResponseProduct(product.get());
        return new SuccessResponse<>(200, "Success", responseProduct);
    }
}
