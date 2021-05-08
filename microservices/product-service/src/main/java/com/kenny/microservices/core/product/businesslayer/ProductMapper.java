package com.kenny.microservices.core.product.businesslayer;

import com.kenny.api.core.product.Product;
import com.kenny.microservices.core.product.datalayer.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring") //Manage this as a bean in the service
public interface ProductMapper {

    @Mapping(target = "serviceAddress", ignore=true)
    Product entityToModel(ProductEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ProductEntity modelToEntity(Product model);
}
