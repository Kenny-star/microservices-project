package com.kenny.microservices.core.product;

import com.kenny.microservices.core.product.datalayer.ProductEntity;
import com.kenny.microservices.core.product.datalayer.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class PersistenceTests {
    @Autowired
    private ProductRepository repository;
    private ProductEntity savedEntity;
    @BeforeEach
    public void setupDb() {
        repository.deleteAll();
        ProductEntity entity = new ProductEntity(1, "n", 1);
        savedEntity = repository.save(entity);
        //expected, actual
        //assertEqualsProduct(entity, savedEntity);
        //actual, expected
        assertThat(savedEntity, samePropertyValuesAs(entity));
    }
    @Test
    public void createProductEntity() {
        ProductEntity newEntity = new ProductEntity(2, "n", 2);
        repository.save(newEntity);
        ProductEntity foundEntity = repository.findById(newEntity.getId()).get();
        //assertEqualsProduct(newEntity, foundEntity);
        assertThat(foundEntity, samePropertyValuesAs(newEntity));
        assertEquals(2, repository.count());
    }
    @Test
    public void updateProductEntity() {
        savedEntity.setName("n2");
        repository.save(savedEntity);
        ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("n2", foundEntity.getName());
    }
    @Test
    public void deleteProductEntity() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }
    @Test
    public void getProductEntity() {
        Optional<ProductEntity> entity = repository.findByProductId(savedEntity.getProductId());
        assertTrue(entity.isPresent());
        //assertEqualsProduct(savedEntity, entity.get());
        assertThat(entity.get(), samePropertyValuesAs(savedEntity));
    }
    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(),        actualEntity.getProductId());
        assertEquals(expectedEntity.getName(),           actualEntity.getName());
        assertEquals(expectedEntity.getWeight(),           actualEntity.getWeight());
    }
}
