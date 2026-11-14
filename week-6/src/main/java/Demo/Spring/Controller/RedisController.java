package Demo.Spring.Controller;


import Demo.Spring.Service.RedisServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisController {

    private final RedisServer redisServer;

    @GetMapping("/redis/set")
    public String set(@RequestParam String key, @RequestParam String value){
        redisServer.set(key,value);
        return "key:" + key + " value:" + value;
    }

    @GetMapping("/redis/users/{id}")
    public Object findUserById(@PathVariable String id) {
        return redisServer.findUserById(id);
    }

}
