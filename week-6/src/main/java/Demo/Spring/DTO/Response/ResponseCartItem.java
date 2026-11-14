package Demo.Spring.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCartItem {
    private String id;
    private String productId;
    private String title;
    private String imageUrl;
    private double unitPrice;
    private int quantity;
    private double subtotal;
}
