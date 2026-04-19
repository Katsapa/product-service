package katsapa.spring.productservice.locking;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import katsapa.spring.productservice.api.ProductDto;
import katsapa.spring.productservice.api.ProductDtoMapper;
import katsapa.spring.productservice.api.ProductUpdateRequest;
import katsapa.spring.productservice.domain.db.ProductEntity;
import katsapa.spring.productservice.domain.service.DbProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/product/lock")
public class ProductUpdateLockingController {
    private final RedisLockManager redisLockManager;
    private final DbProductService dbProductService;
    private final ProductDtoMapper productDtoMapper;

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить продукт c lock",
            description = "Обновляет существующий продукт"
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
                    description = "Время захвата лока"
            )
            @RequestParam(defaultValue = "500")long workMs
    ) {
        log.info("Updating product with id={}", id);
        String lockKey = "product:" + id;

        String lockId = redisLockManager.tryLock(lockKey, Duration.ofMinutes(1));

        if(lockId == null){
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Блокировка для объекта %s уже захвачена. Попробуйте позже".formatted(lockKey)
            );
        }

        try{
            ProductEntity entity = dbProductService.update(id, request);
            ProductDto dto = productDtoMapper.toProductDto(entity);
            log.info("Product has been update: id={}", id);
            return ResponseEntity.ok(dto);
        } finally {
            redisLockManager.tryUnlock(lockKey, lockId);
        }
    }
}
