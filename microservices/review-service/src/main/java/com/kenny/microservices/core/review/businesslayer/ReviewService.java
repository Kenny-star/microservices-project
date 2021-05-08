package com.kenny.microservices.core.review.businesslayer;

import com.kenny.api.core.review.Review;

import java.util.List;

public interface ReviewService {

    public List<Review> getByProductId(int productId);
    public Review createReview(Review model);
    public void deleteReviews(int productId);


}
