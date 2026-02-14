package katsapa.spring.productservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import katsapa.spring.productservice.domain.db.ProductEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class CacheConfiguration {
    @SuppressWarnings("removal")
    @Bean
    public RedisTemplate<String, ProductEntity> redisProductTemplate(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper
    ){
        RedisTemplate<String, ProductEntity> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        var serialize = new Jackson2JsonRedisSerializer<>(objectMapper, ProductEntity.class);
        redisTemplate.setValueSerializer(serialize);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
