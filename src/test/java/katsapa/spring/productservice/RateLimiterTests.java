package katsapa.spring.productservice;

import katsapa.spring.productservice.ratelimit.FixedWindowRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimiterTests {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private FixedWindowRateLimiter rateLimiter;

    @BeforeEach
    void setUp(){
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void allowFirstRequest() {
        when(valueOperations.increment(anyString())).thenReturn(1L);

        boolean result = rateLimiter.allowRequest("user-1", 5, Duration.ofMinutes(1));
        assertThat(result).isTrue();

        verify(redisTemplate).expire(anyString(), eq(Duration.ofMinutes(1)));
    }

    @Test
    void allowWithinLimit(){
        when(valueOperations.increment(anyString())).thenReturn(3L);

        boolean result = rateLimiter.allowRequest("user-1", 5, Duration.ofMinutes(1));
        assertThat(result).isTrue();

        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }

    @Test
    void allowExceedAtLimit() {
        when(valueOperations.increment(anyString())).thenReturn(5L);

        boolean result = rateLimiter.allowRequest("user-1", 5, Duration.ofMinutes(1));
        assertThat(result).isTrue();

        verify(redisTemplate).expire(anyString(), eq(Duration.ofMinutes(1)));
    }

    @Test
    void allowExceedingLimit(){
        when(valueOperations.increment(anyString())).thenReturn(6L);

        boolean result = rateLimiter.allowRequest(anyString(), 5, Duration.ofMinutes(1));

        assertThat(result).isFalse();
    }

    @Test
    void allowDifferentClientsRequest(){
        when(valueOperations.increment(anyString())).thenReturn(3L);

        boolean result1 = rateLimiter.allowRequest("user-A", 5, Duration.ofMinutes(1));
        boolean result2 = rateLimiter.allowRequest("user-B", 5, Duration.ofMinutes(1));

        assertThat(result1).isTrue();
        assertThat(result2).isTrue();

        verify(valueOperations, times(2))
                .increment(argThat(key -> key.contains("user-A") || key.contains("user-B")));
    }
}
