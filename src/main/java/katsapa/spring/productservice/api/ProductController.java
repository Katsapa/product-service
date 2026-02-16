package katsapa.spring.productservice.api;

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
public class ProductController {

    private final DbProductService dbProductService;
    private final ProductDtoMapper mapper;
    private final ManualCachingProductService cachingProductService;
    private final SpringAnnotationChachingProductService springAnnotationChachingProductService;


    @PostMapping
    public ResponseEntity<ProductDto> create(
        @RequestBody ProductCreateRequest request,
        @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ){
        ProductService productService = resolveProductService(cacheMode);
        log.info("Creating product with cacheMode={}", cacheMode);

        ProductEntity product = productService.create(request);
        ProductDto dto = mapper.toProductDto(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(
            @PathVariable Long id,
            @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ){
        log.info("Getting product {} with cacheMode={}", id, cacheMode);
        ProductService productService = resolveProductService(cacheMode);

        ProductEntity productEntity = productService.getById(id);
        ProductDto product = mapper.toProductDto(productEntity);
        return  ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest request,
            @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ){
        log.info("Updating product {} with cacheMode={}", id, cacheMode);
        ProductService productService = resolveProductService(cacheMode);

        ProductEntity entity = productService.update(id, request);
        ProductDto dto = mapper.toProductDto(entity);

        return  ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(value = "CacheMode", defaultValue = "NONE_CACHE") CacheMode cacheMode
    ){
        log.info("Deleting product {} with cacheMode={}", id, cacheMode);
        ProductService productService = resolveProductService(cacheMode);

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private ProductService resolveProductService(CacheMode cacheMode) {
        return switch (cacheMode){
            case NONE_CACHE -> dbProductService;
            case MANUAL -> cachingProductService;
            case SPRING -> springAnnotationChachingProductService;
        };
    }
}
