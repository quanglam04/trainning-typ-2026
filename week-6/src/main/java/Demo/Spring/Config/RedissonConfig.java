package Demo.Spring.Config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    private String redisHort = "localhost";
    private String redisPort = "6379";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHort + ":" + redisPort)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(50);
        return Redisson.create(config);
    }
}
