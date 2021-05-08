package com.kenny.microservices.composite.product.businesslayer;

import com.kenny.api.composite.product.ProductAggregate;

public interface ProductCompositeService {
    public ProductAggregate getProduct(int productId);
    public void createProduct(ProductAggregate model);
    public void deleteProduct(int productId);

}
