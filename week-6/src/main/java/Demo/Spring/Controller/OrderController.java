package Demo.Spring.Controller;

import Demo.Spring.DTO.Request.RequestCartToOrder;
import Demo.Spring.DTO.Request.RequestOrder;
import Demo.Spring.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody RequestOrder requestOrder, Authentication authentication){
        return orderService.set(requestOrder, authentication.getName());
    }
    @PostMapping("/cart/create")
        public ResponseEntity<Object> createCart(@Valid @RequestBody RequestCartToOrder requestCartToOrder, Authentication authentication){
        return orderService.setCart(requestCartToOrder, authentication.getName());
    }
}
