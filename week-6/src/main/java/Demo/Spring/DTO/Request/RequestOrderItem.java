package Demo.Spring.DTO.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderItem {
    @NotBlank(message = "Mã sản phẩm không được để trống")
    private String productId;
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;
}
