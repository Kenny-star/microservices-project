package com.kenny.microservices.composite.product.businesslayer;

import com.kenny.Utils.exceptions.NotFoundException;
import com.kenny.Utils.http.ServiceUtil;
import com.kenny.api.composite.product.ProductAggregate;
import com.kenny.api.composite.product.RecommendationSummary;
import com.kenny.api.composite.product.ReviewSummary;
import com.kenny.api.composite.product.ServiceAddress;
import com.kenny.api.core.product.Product;
import com.kenny.api.core.recommendation.Recommendation;
import com.kenny.api.core.review.Review;
import com.kenny.microservices.composite.product.integrationlayer.ProductCompositeIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCompositeServiceImpl implements ProductCompositeService{
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ProductCompositeIntegration integration;
    private final ServiceUtil serviceUtil;

    public ProductCompositeServiceImpl(ProductCompositeIntegration integration, ServiceUtil serviceUtil) {
        this.integration = integration;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = integration.getProduct(productId);
        if(product== null) throw new NotFoundException("no products found for productId: " + productId);

        List<Recommendation> recommendations = integration.getRecommendations(productId);
        List<Review> reviews = integration.getReviews(productId);

        return createProductAggregate(product,recommendations,reviews, serviceUtil.getServiceAddress());

    }

    @Override
    public void createProduct(ProductAggregate model) {

        try{
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", model.getProductId());

            Product product = new Product(model.getProductId(), model.getName(), model.getWeight(), null );
            integration.createProduct(product);

            if(model.getRecommendations() != null){
                model.getRecommendations().forEach(r -> {
                    Recommendation recommendation = new Recommendation(model.getProductId(), r.getRecommendationId(),
                            r.getAuthor(), r.getRate(), r.getContent(), null);

                    integration.createRecommendation(recommendation);
                });
            }
            if(model.getReviews() != null){
                model.getReviews().forEach(r -> {
                    Review review = new Review(model.getProductId(), r.getReviewId(), r.getAuthor(),
                            r.getSubject(), r.getContent(), null);

                    integration.createReview(review);
                });
            }

            LOG.debug("createCompositeProduct: composite entities created for productId: {}", model.getProductId());

        }catch(RuntimeException ex){
            LOG.warn("createCompositeProduct failed", ex);
            throw ex;
        }
    }

    @Override
    public void deleteProduct(int productId) {

        LOG.debug("deleteCompositeProduct: starting to delete a product aggregate for productId: {}", productId);

        integration.deleteProduct(productId);
        integration.deleteRecommendations(productId);
        integration.deleteReviews(productId);

        LOG.debug("deleteCompositeProduct: Deleted a product aggregate for productId: {}", productId);
    }
    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress){
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                        .collect(Collectors.toList());

        List<ReviewSummary> reviewSummaries = (reviews == null) ? null:
                reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                        .collect(Collectors.toList());

        String productAddress = product.getServiceAddress();
        String recommendationAddress = (recommendations != null && recommendations.size() > 0)
                ? recommendations.get(0).getServiceAddress(): "";
        String reviewAddress = (reviews != null & reviews.size() > 0)
                ? reviews.get(0).getServiceAddress() : "";
        ServiceAddress serviceAddresses = new ServiceAddress(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }
}
