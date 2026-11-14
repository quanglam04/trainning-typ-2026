package Demo.Spring.aspect;

import Demo.Spring.anotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(Demo.Spring.anotation.DistributedLock)")
    public Object handleDistributedLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = AopUtils.getMostSpecificMethod(signature.getMethod(), joinPoint.getTarget().getClass());
        DistributedLock distributedLock = AnnotatedElementUtils.findMergedAnnotation(method, DistributedLock.class);
        if (distributedLock == null) {
            throw new IllegalStateException("Không tìm thấy @DistributedLock trên method " + method.getName());
        }
        Object[] args = joinPoint.getArgs();

        // The expression can produce one key or a collection of product IDs.
        List<String> lockKeys = parseLockKeys(distributedLock.key(), method, args);
        List<RLock> acquiredLocks = new ArrayList<>();

        try {
            // Keys are sorted before acquisition to avoid deadlocks when orders overlap products.
            for (String lockKey : lockKeys) {
                RLock lock = redissonClient.getLock(lockKey);
                log.info("[DistributedLock] Đang thử xin Lock cho key: {} (Wait: {}s, Lease: {}s)",
                        lockKey, distributedLock.waitTime(), distributedLock.leaseTime());

                boolean acquired;
                if (distributedLock.leaseTime() <= 0) {
                    // Redisson watchdog renews the lock while the owning client is alive.
                    acquired = lock.tryLock(distributedLock.waitTime(), distributedLock.timeUnit());
                } else {
                    acquired = lock.tryLock(distributedLock.waitTime(),
                            distributedLock.leaseTime(), distributedLock.timeUnit());
                }

                if (!acquired) {
                    log.warn("[DistributedLock] Không thể lấy Lock cho key: {}", lockKey);
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Sản phẩm đang được xử lý, vui lòng thử lại sau");
                }

                acquiredLocks.add(lock);
                log.info("[DistributedLock] Lấy Lock thành công cho key: {}", lockKey);
            }

            // Proceeding invokes the transactional method; commit completes before this returns
            // because this aspect has the highest precedence.
            return joinPoint.proceed();

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Bị gián đoạn khi chờ khóa sản phẩm", exception);
        } finally {
            // Release in reverse order and only from the thread that owns each lock.
            for (int i = acquiredLocks.size() - 1; i >= 0; i--) {
                RLock lock = acquiredLocks.get(i);
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("[DistributedLock] Đã giải phóng Lock: {}", lock.getName());
                }
            }
        }
    }

    /**
     * Phương thức phân tích biểu thức SpEL (Spring Expression Language)
     * để trích xuất giá trị tham số động (Ví dụ: "#productId" -> "101")
     */
    private List<String> parseLockKeys(String spelKey, Method method, Object[] args) {
        if (spelKey == null || spelKey.trim().isEmpty()) {
            return List.of(method.getName());
        }

        String[] parameterNames = nameDiscoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // Positional aliases keep SpEL working even if parameter names are not in bytecode.
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
            context.setVariable("a" + i, args[i]);
        }

        Object value = parser.parseExpression(spelKey).getValue(context);
        if (value instanceof Collection<?> values) {
            return values.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .map(productId -> "lock:product:" + productId)
                    .distinct()
                    .sorted()
                    .toList();
        }

        if (value == null || String.valueOf(value).isBlank()) {
            throw new IllegalArgumentException("Biểu thức DistributedLock không tạo ra lock key");
        }
        return List.of(String.valueOf(value));
    }
}
