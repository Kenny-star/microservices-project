package com.kenny.api.composite.product;

import org.springframework.web.bind.annotation.*;

public interface ProductCompositeServiceAPI {

    @GetMapping(
            value = "/product-composite/{productId}",
            produces = "application/json")
    ProductAggregate getCompositeProduct(@PathVariable int productId);

    @PostMapping(
            value = "/product-composite",
            consumes = "application/json"
    )

    void createCompositeProduct(@RequestBody ProductAggregate model);

    @DeleteMapping(value = "/product-composite/{productId}")
    void deleteCompositeProduct(@PathVariable int productId);
}
