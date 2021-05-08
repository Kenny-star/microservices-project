package com.kenny.microservices.core.recommendation.businesslayer;

import com.kenny.api.core.recommendation.Recommendation;

import java.util.List;

public interface RecommendationService {

    public List<Recommendation> getByProductId(int productId);
    public Recommendation createRecommendation(Recommendation model);
    public void deleteRecommendations(int productId);


}
