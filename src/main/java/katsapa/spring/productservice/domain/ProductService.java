package katsapa.spring.productservice.domain;

import katsapa.spring.productservice.api.ProductCreateRequest;
import katsapa.spring.productservice.api.ProductUpdateRequest;
import katsapa.spring.productservice.domain.db.ProductEntity;
import org.springframework.stereotype.Component;

public interface ProductService {
    ProductEntity create(ProductCreateRequest createRequest);
    ProductEntity update(Long id, ProductUpdateRequest updateRequest);
    ProductEntity getById(Long id);
    void delete(Long id);
}
