package Demo.Spring.Controller;

import Demo.Spring.DTO.Request.RequestCartItem;
import Demo.Spring.Service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Object> getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public ResponseEntity<Object> addItem(@Valid @RequestBody RequestCartItem request, Authentication authentication) {
        return cartService.addItem(request, authentication.getName());
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Object> updateQuantity(@PathVariable String itemId,
            @RequestParam int quantity, Authentication authentication) {
        return cartService.updateQuantity(itemId, quantity, authentication.getName());
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Object> removeItem(@PathVariable String itemId, Authentication authentication) {
        return cartService.removeItem(itemId, authentication.getName());
    }

    @DeleteMapping("/items")
    public ResponseEntity<Object> clearCart(Authentication authentication) {
        return cartService.clearCart(authentication.getName());
    }
}
