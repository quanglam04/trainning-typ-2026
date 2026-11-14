package Demo.Spring.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProduct {
    private String id;
    private String title;
    private String description;
    private double price;
    private String imageUrl;
    private int quantity;
    private LocalDateTime createdAt;
    private String sellerUsername;
}
