package com.kenny.microservices.composite.product.integrationlayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenny.Utils.exceptions.InvalidInputException;
import com.kenny.Utils.exceptions.NotFoundException;
import com.kenny.Utils.http.HttpErrorInfo;
import com.kenny.api.core.product.Product;
import com.kenny.api.core.product.ProductServiceAPI;
import com.kenny.api.core.recommendation.Recommendation;
import com.kenny.api.core.recommendation.RecommendationServiceAPI;
import com.kenny.api.core.review.Review;
import com.kenny.api.core.review.ReviewServiceAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCompositeIntegration implements ProductServiceAPI, RecommendationServiceAPI, ReviewServiceAPI {
    private static final Logger Log = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,

            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort

    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        //productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        //recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
        //reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }

    @Override
    public Product getProduct(int productId) {
        try{
            String url = productServiceUrl + "/" + productId;
            Log.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);

            Log.debug("Found a product with id: {}", product.getProductId());

            return product;
        }catch(HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex){
        switch(ex.getStatusCode()){
            case NOT_FOUND:
                throw new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY:
                throw new InvalidInputException(getErrorMessage(ex));

            default:
                Log.warn("Got an unexpected HTTP error: {}. will rethrow it.", ex.getStatusCode());
                Log.warn("Error body: {}", ex.getResponseBodyAsString());
                throw ex;
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {

        try{
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();

        }catch(IOException ioex){
            return ex.getMessage();
        }
    }

    @Override
    public Product createProduct(Product model) {

        try{
            String url = productServiceUrl;
            Log.debug("Will call createProduct API on URL: {}", url);
            return restTemplate.postForObject(url, model, Product.class);

        }catch(HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }

    }

    @Override
    public void deleteProduct(int productId) {

        try{
            String url = productServiceUrl + "/" + productId;
            Log.debug("Will call deleteProduct API on url: {}", url);

            restTemplate.delete(url);

        }catch(HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }

    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;
            Log.debug("Will call getRecommendation API on URL: {}", url);

            List<Recommendation> recommendations = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Recommendation>>() {
                    }).getBody();

            Log.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;

        }catch(Exception ex){
            Log.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }

    }
    @Override
    public Recommendation createRecommendation(Recommendation model){
        try{
            String url = recommendationServiceUrl;
            Log.debug("Will call createRecommendation API on URL: {}", url);

            Recommendation recommendation = restTemplate.postForObject(url, model, Recommendation.class);
            Log.debug("Created a recommendation with productId: {} and recommendationId: {}", recommendation.getProductId(), recommendation.getRecommendationId());

            return recommendation;

        }catch(HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteRecommendations(int productId){
        try{
            String url = recommendationServiceUrl + "?productId=" + productId;
            Log.debug("Will call deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);

        }catch(HttpClientErrorException ex){
            handleHttpClientException(ex);
        }
    }
    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;

            Log.debug("Will call getReviews API on URL: {}", url);

            List<Review> reviews = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Review>>() {
            }).getBody();

            Log.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;

        }catch(Exception ex){
            Log.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Review createReview(Review model){
        try{
            String url = reviewServiceUrl;
            Log.debug("Will call createReviews API on URL: {}", url);

            Review review = restTemplate.postForObject(url,model,Review.class);
            Log.debug("Created a review for productId: {} and reviewId: {}", review.getProductId(), review.getReviewId());

            return review;

        }catch(HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteReviews(int productId){
        try{
            String url = reviewServiceUrl + "?productId=" + productId;
            Log.debug("Will call deleteReviews API on URL: {}", url);

            restTemplate.delete(url);
        }catch(HttpClientErrorException ex){
            throw handleHttpClientException(ex);
        }
    }

}
