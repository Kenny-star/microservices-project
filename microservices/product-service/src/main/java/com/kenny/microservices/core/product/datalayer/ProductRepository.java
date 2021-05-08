package com.kenny.microservices.core.product.datalayer;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, String> {

    Optional<ProductEntity> findByProductId(int productId); //have to follow the syntax for the method naming
}
