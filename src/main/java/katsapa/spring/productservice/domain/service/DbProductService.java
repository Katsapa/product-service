package katsapa.spring.productservice.domain.service;

import jakarta.persistence.EntityNotFoundException;
import katsapa.spring.productservice.api.ProductCreateRequest;
import katsapa.spring.productservice.api.ProductUpdateRequest;
import katsapa.spring.productservice.domain.ProductService;
import katsapa.spring.productservice.domain.db.ProductEntity;
import katsapa.spring.productservice.domain.db.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbProductService implements ProductService {
    private final ProductRepository productRepository;

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
        log.info("Updating product in DB: {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        if(updateRequest.price() != null){
            product.setPrice(updateRequest.price());
        }

        if(updateRequest.description() != null){
            product.setDescription(updateRequest.description());
        }
        return productRepository.save(product);
    }
    public ProductEntity getById(Long id){
        log.info("Getting product from DB: id={}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public void delete(Long id){
        log.info("Deleting product from DB: {}", id);
        if(!productRepository.existsById(id)){
            throw new RuntimeException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
