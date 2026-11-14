package Demo.Spring.Service;

import Demo.Spring.DTO.Request.RequestCartItem;
import Demo.Spring.DTO.Response.ResponseCart;
import Demo.Spring.DTO.Response.ResponseCartItem;
import Demo.Spring.DTO.Response.SuccessResponse;
import Demo.Spring.Entity.Cart;
import Demo.Spring.Entity.CartItem;
import Demo.Spring.Entity.Product;
import Demo.Spring.Entity.User;
import Demo.Spring.Exceptions.AppException;
import Demo.Spring.Repository.RepositoryCart;
import Demo.Spring.Repository.RepositoryCartItem;
import Demo.Spring.Repository.RepositoryProduct;
import Demo.Spring.Repository.RepositoryUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static Demo.Spring.Exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RepositoryCart repositoryCart;
    private final RepositoryCartItem repositoryCartItem;
    private final RepositoryProduct repositoryProduct;
    private final RepositoryUser repositoryUser;

    @Transactional
    public ResponseEntity<Object> getCart(String username) {
        Cart cart = getOrCreateCart(username);
        return ok(toResponse(cart));
    }

    @Transactional
    public ResponseEntity<Object> addItem(RequestCartItem request, String username) {
        if (request.getQuantity() < 1) throw new AppException(INVALID_QUANTITY);
        Cart cart = getOrCreateCart(username);
        Product product = repositoryProduct.findById(request.getProductId())
                .orElseThrow(() -> new AppException(PRODUCT_NOT_EXIST));
        if (product.getQuantity() < request.getQuantity()) throw new AppException(OUT_OF_STOCK);

        CartItem item = repositoryCartItem.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });
        if (item.getQuantity() + request.getQuantity() > product.getQuantity())
            throw new AppException(OUT_OF_STOCK);
        item.setQuantity(item.getQuantity() + request.getQuantity());
        repositoryCartItem.save(item);
        touch(cart);
        return ok(toResponse(cart));
    }

    @Transactional
    public ResponseEntity<Object> updateQuantity(String itemId, int quantity, String username) {
        if (quantity < 1) throw new AppException(INVALID_QUANTITY);
        Cart cart = getOrCreateCart(username);
        CartItem item = findOwnedItem(itemId, cart);
        if (quantity > item.getProduct().getQuantity()) throw new AppException(OUT_OF_STOCK);
        item.setQuantity(quantity);
        touch(cart);
        return ok(toResponse(cart));
    }

    @Transactional
    public ResponseEntity<Object> removeItem(String itemId, String username) {
        Cart cart = getOrCreateCart(username);
        repositoryCartItem.delete(findOwnedItem(itemId, cart));
        touch(cart);
        return ok(toResponse(cart));
    }

    @Transactional
    public ResponseEntity<Object> clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        repositoryCartItem.deleteByCartId(cart.getId());
        touch(cart);
        return ok(toResponse(cart));
    }

    private Cart getOrCreateCart(String username) {
        User user = repositoryUser.findByUsername(username)
                .orElseThrow(() -> new AppException(USER_NOT_EXIST));
        return repositoryCart.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setUpdateAt(LocalDateTime.now());
            return repositoryCart.save(cart);
        });
    }

    private CartItem findOwnedItem(String itemId, Cart cart) {
        return repositoryCartItem.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new AppException(CART_ITEM_NOT_EXIST));
    }

    private void touch(Cart cart) {
        cart.setUpdateAt(LocalDateTime.now());
        repositoryCart.save(cart);
    }

    private ResponseCart toResponse(Cart cart) {
        List<ResponseCartItem> items = repositoryCartItem.findByCartId(cart.getId()).stream()
                .map(item -> new ResponseCartItem(item.getId(), item.getProduct().getId(),
                        item.getProduct().getTitle(), item.getProduct().getImageUrl(),
                        item.getProduct().getPrice(), item.getQuantity(),
                        item.getProduct().getPrice() * item.getQuantity()))
                .toList();
        double total = items.stream().mapToDouble(ResponseCartItem::getSubtotal).sum();
        return new ResponseCart(cart.getId(), cart.getUpdateAt(), items, total);
    }

    private ResponseEntity<Object> ok(ResponseCart cart) {
        return ResponseEntity.ok(new SuccessResponse<>(200, "Success", cart));
    }
}
