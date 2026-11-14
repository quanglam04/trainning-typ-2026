package Demo.Spring.Service;

import Demo.Spring.DTO.Request.RequestCartToOrder;
import Demo.Spring.DTO.Request.RequestOrder;
import Demo.Spring.DTO.Response.SuccessResponse;
import Demo.Spring.DTO.Event.OrderCreatedMessage;
import Demo.Spring.anotation.DistributedLock;
import Demo.Spring.Entity.*;
import Demo.Spring.Exceptions.AppException;
import Demo.Spring.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static Demo.Spring.Exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderTransactionService {
    private final RepositoryOrder repositoryOrder;
    private final RepositoryUser repositoryUser;
    private final RepositoryOrderItem repositoryOrderItem;
    private final RepositoryProduct repositoryProduct;
    private final RepositoryCart repositoryCart;
    private final RepositoryCartItem repositoryCartItem;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @DistributedLock(key = "#p2", waitTime = 5, leaseTime = -1)
    public ResponseEntity<Object> set(RequestOrder requestOrder, String userName, List<String> productIds) {
        User user = repositoryUser.findByUsername(userName)
                .orElseThrow(() -> new AppException(USER_NOT_EXIST));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(requestOrder.getShippingAddress());
        order.setPaymentMethod(requestOrder.getPaymentMethod());
        order.setTotalAmount(0);
        repositoryOrder.save(order);

        double sum = 0;
        for (var requestItem : requestOrder.getItems()) {
            if (requestItem.getQuantity() < 1) throw new AppException(INVALID_QUANTITY);

            Product product = repositoryProduct.findById(requestItem.getProductId())
                    .orElseThrow(() -> new AppException(PRODUCT_NOT_EXIST));
            if (product.getQuantity() < requestItem.getQuantity()) throw new AppException(TOKEN_ERROR);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(requestItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(requestItem.getQuantity() * product.getPrice());

            product.setQuantity(product.getQuantity() - requestItem.getQuantity());
            sum += requestItem.getQuantity() * product.getPrice();
            repositoryOrderItem.save(orderItem);
            repositoryProduct.save(product);
        }

        order.setTotalAmount(sum);
        repositoryOrder.save(order);
        eventPublisher.publishEvent(new OrderCreatedMessage(order.getId(), user.getEmail(), sum));
        return ResponseEntity.ok(new SuccessResponse<>(200, "Success", order));
    }

    @Transactional
    @DistributedLock(key = "#p2", waitTime = 5, leaseTime = -1)
    public ResponseEntity<Object> setCart(RequestCartToOrder request, String userName,
                                          List<String> lockedProductIds) {
        User user = repositoryUser.findByUsername(userName)
                .orElseThrow(() -> new AppException(USER_NOT_EXIST));
        Cart cart = repositoryCart.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(CART_ITEM_NOT_EXIST));
        List<CartItem> cartItems = repositoryCartItem.findByCartId(cart.getId());
        if (cartItems.isEmpty()) throw new AppException(CART_ITEM_NOT_EXIST);

        List<String> currentProductIds = cartItems.stream()
                .map(item -> item.getProduct().getId())
                .distinct()
                .sorted()
                .toList();
        if (!currentProductIds.equals(lockedProductIds)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Giỏ hàng vừa thay đổi, vui lòng thử đặt hàng lại");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTotalAmount(0);
        repositoryOrder.save(order);

        double sum = 0;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getQuantity() < 1) throw new AppException(INVALID_QUANTITY);

            Optional<Product> productResult = repositoryProduct.findById(cartItem.getProduct().getId());
            if (productResult.isEmpty()) throw new AppException(PRODUCT_NOT_EXIST);
            Product product = productResult.get();
            if (product.getQuantity() < cartItem.getQuantity()) throw new AppException(TOKEN_ERROR);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(cartItem.getQuantity() * product.getPrice());

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            sum += cartItem.getQuantity() * product.getPrice();
            repositoryOrderItem.save(orderItem);
            repositoryProduct.save(product);
            repositoryCartItem.delete(cartItem);
        }

        order.setTotalAmount(sum);
        repositoryOrder.save(order);
        eventPublisher.publishEvent(new OrderCreatedMessage(order.getId(), user.getEmail(), sum));
        return ResponseEntity.ok(new SuccessResponse<>(200, "Success", order));
    }
}
