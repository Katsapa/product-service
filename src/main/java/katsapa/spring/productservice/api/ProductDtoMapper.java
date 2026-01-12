package katsapa.spring.productservice.api;

import katsapa.spring.productservice.domain.db.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ProductDtoMapper {
    ProductEntity toEntity(ProductDto productDto);
    ProductDto toProductDto(ProductEntity productEntity);
}
