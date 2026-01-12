package katsapa.spring.productservice.api;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto (
        Long id,
        String name,
        BigDecimal price,
        Instant createdAt,
        Instant updatedAt
){
}
