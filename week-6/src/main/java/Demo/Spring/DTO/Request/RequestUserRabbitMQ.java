package Demo.Spring.DTO.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUserRabbitMQ {
    private String username;
    private String password;
    private String email;
}
