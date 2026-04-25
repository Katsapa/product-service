package katsapa.spring.productservice.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на создание заказа")
public record OrderCreateRequest(

        @Schema(description = "ID продукта", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Long productId,

        @Schema(description = "Количество единиц товара", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Min(1)
        Integer quantity
) {}