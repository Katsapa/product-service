package katsapa.spring.productservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import katsapa.spring.productservice.domain.db.OrderEntity;
import katsapa.spring.productservice.domain.service.DbOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Управление заказами. Привязаны к существующим продуктам.")
public class OrderController {

    private final DbOrderService orderService;
    private final OrderDtoMapper mapper;

    @PostMapping
    @Operation(
            summary = "Создать заказ",
            description = "Создаёт новый заказ на указанный продукт. Статус по умолчанию — PENDING."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заказ создан",
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content)
    })
    public ResponseEntity<OrderDto> create(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового заказа",
                    required = true
            )
            OrderCreateRequest request
    ) {
        log.info("POST /api/orders - productId={}", request.productId());
        OrderEntity order = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toOrderDto(order));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить заказ по ID",
            description = "Возвращает заказ по указанному идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ найден",
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content)
    })
    public ResponseEntity<OrderDto> getById(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("GET /api/orders/{}", id);
        OrderEntity order = orderService.getById(id);
        return ResponseEntity.ok(mapper.toOrderDto(order));
    }

    @GetMapping
    @Operation(
            summary = "Получить заказы по продукту",
            description = "Возвращает список всех заказов для указанного продукта."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список заказов",
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content)
    })
    public ResponseEntity<List<OrderDto>> getByProductId(
            @Parameter(description = "ID продукта", example = "1", required = true)
            @RequestParam Long productId
    ) {
        log.info("GET /api/orders?productId={}", productId);
        List<OrderDto> orders = orderService.getByProductId(productId)
                .stream()
                .map(mapper::toOrderDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить заказ",
            description = "Обновляет количество товара и/или статус существующего заказа. Передавайте только изменяемые поля."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ обновлён",
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content)
    })
    public ResponseEntity<OrderDto> update(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long id,

            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновляемые поля заказа",
                    required = true
            )
            OrderUpdateRequest request
    ) {
        log.info("PUT /api/orders/{}", id);
        OrderEntity order = orderService.update(id, request);
        return ResponseEntity.ok(mapper.toOrderDto(order));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить заказ",
            description = "Удаляет заказ по ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заказ удалён",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID заказа", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("DELETE /api/orders/{}", id);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}