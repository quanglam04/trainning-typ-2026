package Demo.Spring.Service;

import Demo.Spring.DTO.Request.RequestCartToOrder;
import Demo.Spring.DTO.Request.RequestOrder;
import Demo.Spring.DTO.Response.SuccessResponse;
import Demo.Spring.Entity.Cart;
import Demo.Spring.Entity.CartItem;
import Demo.Spring.Entity.User;
import Demo.Spring.Exceptions.AppException;
import Demo.Spring.Repository.RepositoryCart;
import Demo.Spring.Repository.RepositoryCartItem;
import Demo.Spring.Repository.RepositoryUser;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static Demo.Spring.Exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final RepositoryUser repositoryUser;
    private final RepositoryCart repositoryCart;
    private final RepositoryCartItem repositoryCartItem;
    private final OrderTransactionService orderTransactionService;

    @CacheEvict(cacheNames = {"product_list", "product_detail"}, allEntries = true)
    public ResponseEntity<Object> set(RequestOrder requestOrder, String userName) {
        List<String> productIds = requestOrder.getItems().stream()
                .map(item -> item.getProductId())
                .distinct()
                .sorted()
                .toList();

        return orderTransactionService.set(requestOrder, userName, productIds);
    }

    @CacheEvict(cacheNames = {"product_list", "product_detail"}, allEntries = true)
    public ResponseEntity<Object> setCart(RequestCartToOrder request, String userName) {
        User user = repositoryUser.findByUsername(userName)
                .orElseThrow(() -> new AppException(USER_NOT_EXIST));
        Cart cart = repositoryCart.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(CART_ITEM_NOT_EXIST));

        List<String> productIds = repositoryCartItem.findByCartId(cart.getId()).stream()
                .map(CartItem::getProduct)
                .map(product -> product.getId())
                .distinct()
                .sorted()
                .toList();

        if (productIds.isEmpty()) {
            throw new AppException(CART_ITEM_NOT_EXIST);
        }

        return orderTransactionService.setCart(request, userName, productIds);
    }
}
