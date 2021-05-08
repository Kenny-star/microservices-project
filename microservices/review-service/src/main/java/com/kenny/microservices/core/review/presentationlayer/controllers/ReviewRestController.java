package com.kenny.microservices.core.review.presentationlayer.controllers;

import com.kenny.Utils.exceptions.InvalidInputException;
import com.kenny.Utils.http.ServiceUtil;
import com.kenny.api.core.recommendation.Recommendation;
import com.kenny.api.core.review.Review;
import com.kenny.api.core.review.ReviewServiceAPI;
import com.kenny.microservices.core.review.businesslayer.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReviewRestController implements ReviewServiceAPI {

    private static final Logger Log = LoggerFactory.getLogger(ReviewRestController.class);

    private final ReviewService reviewService;

    public ReviewRestController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @Override
    public List<Review> getReviews(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<Review> listReviews = reviewService.getByProductId(productId);
/*
        if(productId == 213){
            Log.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }
        List<Review> listReviews = new ArrayList<>();
        listReviews.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        listReviews.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        listReviews.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));
*/

        Log.debug("/reviews found response size: {}", listReviews.size());
                return listReviews;
                }

    @Override
    public Review createReview(Review model) {
            Review review = reviewService.createReview(model);
            Log.debug("REST Controller createReview: created an entity: {} / {}", review.getProductId(), review.getReviewId());
            return review;
            }

    @Override
    public void deleteReviews(int productId) {
            Log.debug("REST Controller deleteReviews: trying to delete reviews for the product with productId: {}", productId);

            reviewService.deleteReviews(productId);
            }

}