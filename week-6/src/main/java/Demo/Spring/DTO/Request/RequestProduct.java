package Demo.Spring.DTO.Request;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestProduct {
    private String title;
    private String description;
    private double price;
    private String imageUrl;
    private Integer quantity;
}
