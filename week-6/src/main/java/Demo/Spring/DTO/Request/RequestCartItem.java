package Demo.Spring.DTO.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCartItem {
    @NotBlank
    private String productId;

    @Min(1)
    private int quantity;
}
