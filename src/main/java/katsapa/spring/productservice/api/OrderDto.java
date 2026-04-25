package katsapa.spring.productservice.api;

import io.swagger.v3.oas.annotations.media.Schema;
import katsapa.spring.productservice.domain.db.OrderStatus;

import java.time.Instant;

@Schema(description = "Данные заказа")
public record OrderDto(

        @Schema(description = "ID заказа", example = "10")
        Long id,

        @Schema(description = "ID продукта", example = "1")
        Long productId,

        @Schema(description = "Название продукта", example = "Laptop")
        String productName,

        @Schema(description = "Количество единиц товара", example = "3")
        Integer quantity,

        @Schema(description = "Статус заказа", example = "PENDING")
        OrderStatus status,

        @Schema(description = "Дата создания")
        Instant createdAt,

        @Schema(description = "Дата последнего обновления")
        Instant updatedAt
) {}