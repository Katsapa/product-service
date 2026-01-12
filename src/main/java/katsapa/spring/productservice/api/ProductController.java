package katsapa.spring.productservice.api;

import katsapa.spring.productservice.domain.service.DbProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    ){
        log.info("Creating product with cacheMode={}", "non");
    }
}
