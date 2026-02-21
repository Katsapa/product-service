package katsapa.spring.productservice.locking;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class RedisLockManager {
    private final StringRedisTemplate stringRedisTemplate;
    private static final String RELEASE_LOCK_LUA_SCRIPT = """
            if redis.call('GET', KEY[1]) == ARGV[1] then
                return redis.call('DEL', KEY[1])
            else return 0 end
            """;

    public String tryLock(
            String key,
            Duration ttl
    ){
        String lockKey = "lock:" + key;
        String lockId = UUID.randomUUID().toString();

        boolean isLockSuccessful = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockId, ttl);

        if(isLockSuccessful){
            log.info("Lock has been successful for lockKey={}, lockId={}", lockKey, lockId);
            return lockId;
        }
        return null;
    }

    public void tryUnlock(
            String key,
            String lockId
    ){
        Long result = stringRedisTemplate.execute(connection -> connection.scriptingCommands().eval(
                RELEASE_LOCK_LUA_SCRIPT.getBytes(StandardCharsets.UTF_8),
                ReturnType.INTEGER,
                1,
                key.getBytes(StandardCharsets.UTF_8),
                lockId.getBytes(StandardCharsets.UTF_8)
        ), true);

        if(result != null && result == 1L){
            log.info("Lock has been released: lockKey={}, lockId={}", key, lockId);
        } else {
            log.info("Lock was already released or re-acquired: lockKey={}, lockId={}", key, lockId);
        }
    }
}
