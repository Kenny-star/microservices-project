package com.kenny.microservices.core.product.presentationlayer.controllers;

import com.kenny.Utils.exceptions.InvalidInputException;
import com.kenny.Utils.exceptions.NotFoundException;
import com.kenny.Utils.http.ServiceUtil;
import com.kenny.api.core.product.Product;
import com.kenny.api.core.product.ProductServiceAPI;
import com.kenny.microservices.core.product.businesslayer.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRESTController implements ProductServiceAPI {

    private static final Logger Log = LoggerFactory.getLogger(ProductRESTController.class);

    //private final ServiceUtil serviceUtil;

    private final ProductService productService;


    //dependency injection
    public ProductRESTController(ProductService productService){
        this.productService = productService;
    }
    @Override
    public Product getProduct(int productId) {

        Log.debug("/product MS returns the found product for productId: " + productId);

        if(productId < 1) throw new InvalidInputException("Invalid Product Id: " + productId);

        //if(productId == 13) throw new NotFoundException("No product found for productId: " + productId);
        //return new Product(productId, "name-" + productId, 120, serviceUtil.getServiceAddress());

        Product product = productService.getProductById(productId);

        return product;
    }

    @Override
    public Product createProduct(Product model) {
        Product product = productService.createProduct(model);

        Log.debug("REST createProduct: product created for productId: {}", product.getProductId());

        return product;
    }

    @Override
    public void deleteProduct(int productId) {
        Log.debug("REST deleteProduct: tried to delete productId: {}", productId);
        productService.deleteProduct((productId));
    }
}
