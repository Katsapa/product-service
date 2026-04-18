package katsapa.spring.productservice.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Данные продукта")
public record ProductDto (

        @Schema(description = "Уникальный ID", example = "1")
        Long id,

        @Schema(description = "Название продукта", example = "Ноутбук ASUS")
        String name,

        @Schema(description = "Цена в рублях", example = "89999.99")
        BigDecimal price,

        @Schema(description = "Дата создания продукта", example = "2026-02-16 17:26:26.263217 +00:00")
        Instant createdAt,

        @Schema(description = "Дата создания продукта", example = "2026-02-19 11:45:45.425781 +00:00")
        Instant updatedAt
){
}
