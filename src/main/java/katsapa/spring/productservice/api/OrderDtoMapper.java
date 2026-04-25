package katsapa.spring.productservice.api;

import katsapa.spring.productservice.domain.db.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderDtoMapper {

    public OrderDto toOrderDto(OrderEntity entity) {
        return new OrderDto(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getProduct().getName(),
                entity.getQuantity(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}