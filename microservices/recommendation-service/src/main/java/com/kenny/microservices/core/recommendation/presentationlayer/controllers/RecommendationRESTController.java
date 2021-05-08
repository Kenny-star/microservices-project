package com.kenny.microservices.core.recommendation.presentationlayer.controllers;

import com.kenny.Utils.exceptions.InvalidInputException;
import com.kenny.Utils.http.ServiceUtil;
import com.kenny.api.core.recommendation.Recommendation;
import com.kenny.api.core.recommendation.RecommendationServiceAPI;
import com.kenny.microservices.core.recommendation.businesslayer.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationRESTController implements RecommendationServiceAPI {

    private static final Logger Log = LoggerFactory.getLogger(RecommendationRESTController.class);

    private final RecommendationService recommendationService;

    public RecommendationRESTController(RecommendationService recommendationService){
        this.recommendationService = recommendationService;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<Recommendation> listRecommendations = recommendationService.getByProductId(productId);
        /*
        if(productId == 113){
            Log.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }

        List<Recommendation> listRecommendation = new ArrayList<>();
        listRecommendation.add(new Recommendation(productId, 1, "Author 1", 455, "Content 1", serviceUtil.getServiceAddress()));
        listRecommendation.add(new Recommendation(productId, 2, "Author 2", 35, "Content 2", serviceUtil.getServiceAddress()));
        listRecommendation.add(new Recommendation(productId, 3, "Author 3", 45, "Content 3", serviceUtil.getServiceAddress()));
*/
        Log.debug("/recommendations found response size: {}", listRecommendations.size());
        return listRecommendations;
    }

    @Override
    public Recommendation createRecommendation(Recommendation model) {
        Recommendation recommendation = recommendationService.createRecommendation(model);
        Log.debug("REST Controller createRecommendation: created an entity: {} / {}", recommendation.getProductId(), recommendation.getRecommendationId());
        return recommendation;
    }

    @Override
    public void deleteRecommendations(int productId) {
        Log.debug("REST Controller deleteRecommendations: trying to delete recommendations for the product with productId: {}", productId);

        recommendationService.deleteRecommendations(productId);
    }

}

