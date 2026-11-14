package Demo.Spring.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOrder {
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
    @NotEmpty(message = "Đơn hàng phải có ít nhất một sản phẩm")
    @Valid
    private List<RequestOrderItem> items;
}
