package katsapa.spring.productservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import katsapa.spring.productservice.domain.CacheMode;
import katsapa.spring.productservice.domain.ProductService;
import katsapa.spring.productservice.domain.db.ProductEntity;
import katsapa.spring.productservice.domain.service.DbProductService;
import katsapa.spring.productservice.domain.service.ManualCachingProductService;
import katsapa.spring.productservice.domain.service.SpringAnnotationChachingProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Управление продуктами. Поддерживает три стратегии кэширования.")
public class ProductController {

    private final DbProductService dbProductService;
    private final ProductDtoMapper mapper;
    private final ManualCachingProductService cachingProductService;
    private final SpringAnnotationChachingProductService springAnnotationChachingProductService;

    @PostMapping
    @Operation(
            summary = "Создать продукт",
            description = "Создаёт новый продукт. Стратегия кэширования влияет только на последующие операции чтения."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Продукт создан",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content)
    })
    public ResponseEntity<ProductDto> create(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового продукта",
                    required = true
            )
            ProductCreateRequest request,

            @Parameter(
                    description = "Стратегия кэширования",
                    schema = @Schema(implementation = CacheMode.class, defaultValue = "NONE_CACHE")
            )
            @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) {
        ProductService productService = resolveProductService(cacheMode);
        log.info("Creating product with cacheMode={}", cacheMode);

        ProductEntity product = productService.create(request);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить продукт по ID",
            description = "Возвращает продукт по указанному ID. При MANUAL или SPRING — сначала ищет в кэше."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продукт найден",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content)
    })
    public ResponseEntity<ProductDto> getById(
            @Parameter(description = "ID продукта", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(
                    description = "Стратегия кэширования",
                    schema = @Schema(implementation = CacheMode.class, defaultValue = "NONE_CACHE")
            )
            @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) {
        log.info("Getting product {} with cacheMode={}", id, cacheMode);
        ProductService productService = resolveProductService(cacheMode);

        ProductEntity productEntity = productService.getById(id);
        ProductDto product = mapper.toProductDto(productEntity);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить продукт",
            description = "Обновляет существующий продукт. При MANUAL/SPRING кэш инвалидируется автоматически."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продукт обновлён",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content)
    })
    public ResponseEntity<ProductDto> update(
            @Parameter(description = "ID продукта", example = "1", required = true)
            @PathVariable Long id,

            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные продукта",
                    required = true
            )
            ProductUpdateRequest request,

            @Parameter(
                    description = "Стратегия кэширования",
                    schema = @Schema(implementation = CacheMode.class, defaultValue = "NONE_CACHE")
            )
            @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) {
        log.info("Updating product {} with cacheMode={}", id, cacheMode);
        ProductService productService = resolveProductService(cacheMode);

        ProductEntity entity = productService.update(id, request);
        ProductDto dto = mapper.toProductDto(entity);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить продукт",
            description = "Удаляет продукт по ID. При MANUAL/SPRING запись также удаляется из кэша."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Продукт удалён",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID продукта", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(
                    description = "Стратегия кэширования",
                    schema = @Schema(implementation = CacheMode.class, defaultValue = "NONE_CACHE")
            )
            @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ) {
        log.info("Deleting product {} with cacheMode={}", id, cacheMode);
        ProductService productService = resolveProductService(cacheMode);

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private ProductService resolveProductService(CacheMode cacheMode) {
        return switch (cacheMode) {
            case NONE_CACHE -> dbProductService;
            case MANUAL -> cachingProductService;
            case SPRING -> springAnnotationChachingProductService;
        };
    }
}