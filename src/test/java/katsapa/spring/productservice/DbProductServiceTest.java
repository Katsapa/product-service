package katsapa.spring.productservice;

import katsapa.spring.productservice.api.ProductUpdateRequest;
import katsapa.spring.productservice.domain.db.ProductEntity;
import katsapa.spring.productservice.domain.db.ProductRepository;
import katsapa.spring.productservice.domain.service.DbProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DbProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DbProductService productService;

    @Test
    public void RequestUpdate(){
        ProductEntity existing = ProductEntity.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .description("Old description")
                .build();

        ProductUpdateRequest updateRequest = new ProductUpdateRequest(
                BigDecimal.valueOf(799.99),
                "New description"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(existing);

        ProductEntity result = productService.update(1L, updateRequest);

        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(799.99));
        assertThat(result.getDescription()).isEqualTo("New description");

        verify(productRepository).save(existing);
    }

    @Test
    void deleteRequest(){
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteNotExistID(){
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found: 99");

        verify(productRepository, never()).deleteById(any());
    }
}
