package com.kenny.microservices.core.review.businesslayer;

import com.kenny.Utils.exceptions.InvalidInputException;
import com.kenny.Utils.http.ServiceUtil;
import com.kenny.api.core.review.Review;
import com.kenny.microservices.core.review.datalayer.ReviewEntity;
import com.kenny.microservices.core.review.datalayer.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService{
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repository;

    private final ReviewMapper mapper;

    private final ServiceUtil serviceUtil;

    public ReviewServiceImpl(ReviewRepository repository, ReviewMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getByProductId(int productId) {

        List<ReviewEntity> entityList = repository.findByProductId((productId));
        List<Review> list = mapper.entityListToModelList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("Reviews getByProductId: response size: {}", list.size());
        return list;
    }

    @Override
    public Review createReview(Review model) {
        try{
            ReviewEntity entity = mapper.modelToEntity(model);
            ReviewEntity newEntity = repository.save(entity);
            LOG.debug("createReview: entity created for productId: {}", model.getProductId());
            return mapper.entityToModel(newEntity);
        }catch (DuplicateKeyException dke){
            throw new InvalidInputException("Duplicate key for productId: " + model.getProductId());
        }
    }

    @Override
    public void deleteReviews(int productId) {
        LOG.debug("ReviewService deleteReviews: tries to delete all reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
