package Demo.Spring.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCartToOrder {
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
}
