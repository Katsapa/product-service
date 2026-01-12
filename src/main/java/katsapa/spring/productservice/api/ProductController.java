package katsapa.spring.productservice.api;

import katsapa.spring.productservice.domain.db.ProductEntity;
import katsapa.spring.productservice.domain.service.DbProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final DbProductService dbProductService;
    private final ProductDtoMapper mapper;


    public ProductController(DbProductService dbProductService, ProductDtoMapper mapper) {
        this.dbProductService = dbProductService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(
        @RequestBody ProductCreateRequest request
    ){
        log.info("Creating product with cacheMode={}", "non");
        ProductEntity product = dbProductService.create(request);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(
            @PathVariable Long id
    ){
        log.info("Getting product {} with cacheMode={}", id, "non");
        ProductEntity productEntity = dbProductService.getById(id);
        ProductDto product = mapper.toProductDto(productEntity);
        return  ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest request
    ){
        log.info("Updating product {} with cacheMode={}", id, "non");
        ProductEntity entity = dbProductService.update(id, request);
        ProductDto dto = mapper.toProductDto(entity);

        return  ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ){
        log.info("Deleting product {} with cacheMode={}", id, "non");

        dbProductService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
