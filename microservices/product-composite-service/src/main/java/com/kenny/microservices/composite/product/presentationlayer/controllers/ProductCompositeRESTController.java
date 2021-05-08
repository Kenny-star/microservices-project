package com.kenny.microservices.composite.product.presentationlayer.controllers;

import com.kenny.Utils.exceptions.NotFoundException;
import com.kenny.Utils.http.ServiceUtil;
import com.kenny.api.composite.product.*;
import com.kenny.api.core.product.Product;
import com.kenny.api.core.recommendation.Recommendation;
import com.kenny.api.core.review.Review;
import com.kenny.microservices.composite.product.businesslayer.ProductCompositeService;
import com.kenny.microservices.composite.product.integrationlayer.ProductCompositeIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeRESTController implements ProductCompositeServiceAPI {

    private static final Logger Log = LoggerFactory.getLogger(ProductCompositeRESTController.class);
    private final ProductCompositeService productCompositeService;

    public ProductCompositeRESTController(ProductCompositeService productCompositeService) {
        this.productCompositeService = productCompositeService;

    }


    @Override
    public ProductAggregate getCompositeProduct(int productId) {
        Log.debug("ProductComposite REST received getCompositeProduct request for productId: {}", productId);

        ProductAggregate productAggregate = productCompositeService.getProduct(productId);
        return productAggregate;
    }

    @Override
    public void createCompositeProduct(ProductAggregate model) {
        Log.debug("ProductComposite REST received createCompositeProduct");
        productCompositeService.createProduct(model);
    }

    @Override
    public void deleteCompositeProduct(int productId) {
        Log.debug("ProductComposite REST received deleteCompositeProduct");
        productCompositeService.deleteProduct(productId);
    }
}
