package Demo.Spring.Controller;

import Demo.Spring.DTO.Request.RequestUserRabbitMQ;
import Demo.Spring.Publisher.RabbitMQJsonProducer;
import Demo.Spring.Publisher.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    public static RabbitMQJsonProducer rabbitMQJsonProducer;
    public static RabbitMQProducer rabbitMQProducer;
    public MessageController(RabbitMQProducer rabbitMQProducer, RabbitMQJsonProducer rabbitMQJsonProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
        this.rabbitMQJsonProducer = rabbitMQJsonProducer;
    }
    @GetMapping("/publish")
    public ResponseEntity<String> sendMessage(@RequestParam("message") String message){
        rabbitMQProducer.sendMessage(message);
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }

//    @GetMapping("/loginMQ")
//    public ResponseEntity<String> sendMessage2(@RequestBody RequestUserRabbitMQ request){
//        rabbitMQJsonProducer.sendJsonMessage(request);
//        return ResponseEntity.ok("Message sent to RabbitMQ");
//    }
}
