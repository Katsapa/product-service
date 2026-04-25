package katsapa.spring.productservice.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import katsapa.spring.productservice.domain.db.OrderStatus;

@Schema(description = "Запрос на обновление заказа")
public record OrderUpdateRequest(

        @Schema(description = "Новое количество товара (необязательно)", example = "5")
        @Min(1)
        Integer quantity,

        @Schema(description = "Новый статус заказа (необязательно)", example = "CONFIRMED")
        OrderStatus status
) {}