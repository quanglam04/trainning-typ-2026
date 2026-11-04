package com.courseshop.courseshop.controller;

/*
 * Endpoint publish JSON thủ công được giữ lại để tham khảo, nhưng đã tắt.
 * Luồng chính hiện là POST /users → UserService → RabbitMQJsonProducer.
 *
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.event.UserRegisterEvent;
import com.courseshop.courseshop.publisher.RabbitMQJsonProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1")
public class MessageJsonController {
    private final RabbitMQJsonProducer jsonProducer;

    public MessageJsonController(RabbitMQJsonProducer jsonProducer) {
        this.jsonProducer = jsonProducer;
    }
    
    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@RequestBody UserRegisterEvent event) {
        jsonProducer.sendJsonMessage(event);
        return ResponseEntity.ok("Json message send to RabbitMQ...");
    }

    
}
*/
