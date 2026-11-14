package Demo.Spring.Controller;

import Demo.Spring.DTO.Request.RequestProduct;
import Demo.Spring.Entity.Product;
import Demo.Spring.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<Object> create(
            @RequestBody RequestProduct request,
            Authentication authentication) {
        return productService.created(request, authentication.getName());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id, Authentication authentication) {
            return productService.delete(id, authentication.getName());
    }

    @GetMapping()
    public ResponseEntity<Object> get(@RequestParam(defaultValue = "0") int page){
        return ResponseEntity.ok(productService.getInformation(page));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id){
        return ResponseEntity.ok(productService.getById(id));
    }

}
