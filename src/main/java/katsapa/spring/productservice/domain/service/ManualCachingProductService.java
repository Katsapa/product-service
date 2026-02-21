package katsapa.spring.productservice.domain.service;

import katsapa.spring.productservice.api.ProductCreateRequest;
import katsapa.spring.productservice.api.ProductUpdateRequest;
import katsapa.spring.productservice.domain.ProductService;
import katsapa.spring.productservice.domain.db.ProductEntity;
import katsapa.spring.productservice.domain.db.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class ManualCachingProductService implements ProductService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, ProductEntity> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "product:";
    private static final long CACHE_TTL_MINUTES= 1;


    @Override
    public ProductEntity create(ProductCreateRequest createRequest){
        log.info("Creating product in DB: {}", createRequest.name());
        ProductEntity product = ProductEntity
                .builder()
                .name(createRequest.name())
                .price(createRequest.price())
                .description(createRequest.description())
                .build();
        return productRepository.save(product);
    }

    public ProductEntity update(Long id, ProductUpdateRequest updateRequest){

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        if(updateRequest.price() != null){
            product.setPrice(updateRequest.price());
        }

        if(updateRequest.description() != null){
            product.setDescription(updateRequest.description());
        }

        log.info("Updating product in DB: {}", id);
        var savedProduct = productRepository.save(product);

        var cacheKey = CACHE_KEY_PREFIX + id;
        redisTemplate.delete(cacheKey);
        //redisTemplate.opsForValue().set(cacheKey, savedProduct);
        return savedProduct;
    }

    public ProductEntity getById(Long id){
        log.info("Getting product: id={}", id);
        var cacheKey = CACHE_KEY_PREFIX + id;

        ProductEntity entityFromCache = redisTemplate.opsForValue().get("product:" + id);

        if(entityFromCache != null){
            return entityFromCache;
        }
        log.info("Product doesnt find in cache: if={}", id);
        ProductEntity entityForBd = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found in BD: " + id));

        redisTemplate.opsForValue()
                .set(cacheKey, entityForBd, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.info("Product was add in cache: id={}", id);

        return entityForBd;
    }

    public void delete(Long id){
        log.info("Deleting product from DB: {}", id);
        if(!productRepository.existsById(id)){
            throw new RuntimeException("Product not found: " + id);
        }
        String cacheKey = "product:" + id;

        productRepository.deleteById(id);

        redisTemplate.delete(cacheKey);
        log.info("Cache invalidated for deleted product: id={}", id);
    }
}
