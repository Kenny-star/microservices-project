package com.kenny.microservices.core.recommendation;

import com.kenny.api.core.recommendation.Recommendation;
import com.kenny.microservices.core.recommendation.datalayer.RecommendationEntity;
import com.kenny.microservices.core.recommendation.datalayer.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class RecommendationServiceApplicationTests {

	private static final int PRODUCT_ID_OKAY = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 113;
	private static final String PRODUCT_ID_INVALID_STRING = "not-integer";
	private static final int PRODUCT_ID_INVALID_NEGATIVE = -1;

	private static final int RECOMMENDATION_ID = 1;

	@Autowired
	private WebTestClient client;

	@Autowired
	private RecommendationRepository repository;

	@BeforeEach
	public void setupDb(){
		repository.deleteAll();
	}
	@Test
	public void getRecommendationsByProductId(){

		int expectedLength = 3;

		RecommendationEntity entity1 = new RecommendationEntity(PRODUCT_ID_OKAY, RECOMMENDATION_ID, "author-1", 1, "content-1");
		repository.save(entity1);

		RecommendationEntity entity2 = new RecommendationEntity(PRODUCT_ID_OKAY, RECOMMENDATION_ID + 1, "author-2", 1, "content-2");
		repository.save(entity2);

		RecommendationEntity entity3 = new RecommendationEntity(PRODUCT_ID_OKAY, RECOMMENDATION_ID + 2, "author-3", 1, "content-3");
		repository.save(entity3);

		assertEquals(expectedLength, repository.findByProductId(PRODUCT_ID_OKAY).size());

		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(expectedLength)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OKAY)
				.jsonPath("$[1].productId").isEqualTo(PRODUCT_ID_OKAY)
				.jsonPath("$[2].productId").isEqualTo(PRODUCT_ID_OKAY);
	}

	@Test
	public void createRecommendation(){

		int expectedSize = 1;
		Recommendation recommendation = new Recommendation(PRODUCT_ID_OKAY, RECOMMENDATION_ID, "Author " + RECOMMENDATION_ID, RECOMMENDATION_ID, "Content " + RECOMMENDATION_ID, "SA");

		client.post()
				.uri("/recommendation")
				.body(just(recommendation),Recommendation.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();

		assertEquals(expectedSize, repository.findByProductId(PRODUCT_ID_OKAY).size());
	}

	@Test
	public void deleteRecommendations() {

		RecommendationEntity entity = new RecommendationEntity(PRODUCT_ID_OKAY, RECOMMENDATION_ID, "author-1", 1, "content-1");
		repository.save(entity);

		assertEquals(1,repository.findByProductId(PRODUCT_ID_OKAY).size());

		client.delete()
				.uri("/recommendation?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody();

		assertEquals(0, repository.findByProductId(PRODUCT_ID_OKAY).size());
	}

	@Test
	public void getRecommendationsMissingParameter(){

		client.get()
				.uri("/recommendation")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present" );
	}

	@Test
	public void getRecommendationsInvalidParameterString(){

		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_INVALID_STRING)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Type mismatch." );
	}

	@Test
	public void getRecommendationsInvalidParameterNegativeValue(){

		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_INVALID_NEGATIVE)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + PRODUCT_ID_INVALID_NEGATIVE );
	}


	@Test
	public void getRecommendationsNotFound(){

		int expectedLength = 0;

		client.get()
				.uri("/recommendation?productId=" + PRODUCT_ID_NOT_FOUND)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(expectedLength);
	}

}
