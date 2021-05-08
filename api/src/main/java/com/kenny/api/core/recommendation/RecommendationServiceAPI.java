package com.kenny.api.core.recommendation;

import com.kenny.api.core.product.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface RecommendationServiceAPI {
    @GetMapping(
            value = "/recommendation",
            produces = "application/json"
    )
    List <Recommendation> getRecommendations(@RequestParam(value = "productId", required = true) int productId);

    @PostMapping(
            value = "/recommendation",
            consumes = "application/json",
            produces = "application/json"
    )

    Recommendation createRecommendation(@RequestBody Recommendation model);

    @DeleteMapping(
            value = "/recommendation"
    )
    void deleteRecommendations(@RequestParam(value = "productId", required = true) int productId);


}
