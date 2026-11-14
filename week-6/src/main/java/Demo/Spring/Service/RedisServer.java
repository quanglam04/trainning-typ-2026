package Demo.Spring.Service;


import Demo.Spring.DTO.Response.ResponseUser;
import Demo.Spring.Entity.User;
import Demo.Spring.Exceptions.AppException;
import Demo.Spring.Repository.RepositoryUser;
import Demo.Spring.Mapper.MapperUser;
import static Demo.Spring.Exceptions.ErrorCode.USER_NOT_EXIST;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final RepositoryUser repositoryUser;
    private final MapperUser mapperUser;

    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public void setWithTTL(String key, Object value, long timeout,  TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    // List operator

    public void pushLeftToList(String key, Object value){
        redisTemplate.opsForList().leftPush(key, value);
    }

    public void pushRightToList(String key, Object value){
        redisTemplate.opsForList().rightPush(key, value);
    }

    public Object popLeftToList(String key){
        return redisTemplate.opsForList().leftPop(key);
    }

    public Object popRightToList(String key){
        return redisTemplate.opsForList().rightPop(key);
    }

    public Object lRange(String key, int start, int end){
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Object findUserById(String id){

        Object cache = redisTemplate.opsForValue().get(id);
        if(cache != null){
            return cache;
        }

        List<User> list = repositoryUser.findByid(id);
        if(list.size() == 0){
            throw new AppException(USER_NOT_EXIST);
        }
        redisTemplate.opsForValue().set(id,mapperUser.toResponseUser(list.get(0)), 10, TimeUnit.MINUTES);
        return mapperUser.toResponseUser(list.get(0));
    }
}
