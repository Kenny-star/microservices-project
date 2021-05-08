package com.kenny.microservices.core.recommendation.businesslayer;

import com.kenny.Utils.http.ServiceUtil;
import com.kenny.api.core.recommendation.Recommendation;
import com.kenny.microservices.core.recommendation.datalayer.RecommendationEntity;
import com.kenny.microservices.core.recommendation.datalayer.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService{
    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final RecommendationRepository repository;

    private final RecommendationMapper mapper;

    private final ServiceUtil serviceUtil;

    public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getByProductId(int productId) {

        List<RecommendationEntity> entityList = repository.findByProductId((productId));
        List<Recommendation> list = mapper.entityListToModelList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("Recommendations getByProductId: response size: {}", list.size());
        return list;
    }

    @Override
    public Recommendation createRecommendation(Recommendation model) {

        RecommendationEntity entity = mapper.modelToEntity(model);
        RecommendationEntity newEntity = repository.save(entity);

        LOG.debug("RecommendationService createRecommendation: created a recommendation entity: {}/{}", model.getProductId(), model.getRecommendationId());
        return mapper.entityToModel(newEntity);
    }

    @Override
    public void deleteRecommendations(int productId) {
        LOG.debug("RecommendationService deleteRecommendations: tries to delete all recommendations for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
