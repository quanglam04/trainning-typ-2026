package Demo.Spring.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ResponseCart {
    private String id;
    private LocalDateTime updateAt;
    private List<ResponseCartItem> items;
    private double totalAmount;
}
