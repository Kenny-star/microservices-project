package com.kenny.microservices.core.review;

import com.kenny.api.core.review.Review;
import com.kenny.microservices.core.review.datalayer.ReviewEntity;
import com.kenny.microservices.core.review.datalayer.ReviewRepository;
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

import static reactor.core.publisher.Mono.just;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.datasource.url=jdbc:h2:mem:review-db"})
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class ReviewServiceApplicationTests {

	private static final int PRODUCT_ID_OKAY = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 213;
	private static final String PRODUCT_ID_INVALID_STRING = "not-integer";
	private static final int PRODUCT_ID_INVALID_NEGATIVE = -1;

	private static final int REVIEW_ID = 1;

	@Autowired
	private WebTestClient client;

	@Autowired
	ReviewRepository repository;

	@BeforeEach
	public void setupDb(){repository.deleteAll();}

	@Test
	public void getReviewByProductId(){

		int expectedLength = 3;

		//add the reviews to the repo
		ReviewEntity entity1 = new ReviewEntity(PRODUCT_ID_OKAY, 1, "author-1", "subject-1", "content-1");
		repository.save(entity1);
		ReviewEntity entity2 = new ReviewEntity(PRODUCT_ID_OKAY, 2, "author-2", "subject-2", "content-2");
		repository.save(entity2);
		ReviewEntity entity3 = new ReviewEntity(PRODUCT_ID_OKAY, 3, "author-3", "subject-3", "content-3");
		repository.save(entity3);

		assertEquals(expectedLength, repository.findByProductId(PRODUCT_ID_OKAY).size());

		client.get()
				.uri("/review?productId=" + PRODUCT_ID_OKAY)
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
	public void createReview(){
		int expectedSize =1;
		//create the review

		Review review = new Review(PRODUCT_ID_OKAY, REVIEW_ID,
				"Author " + REVIEW_ID, "Subject" + REVIEW_ID, "Content " + REVIEW_ID, "SA");


		//send the POST request
		client.post()
				.uri("/review")
				.body(just(review), Review.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();

		assertEquals(expectedSize, repository.findByProductId(PRODUCT_ID_OKAY).size());


	}

	@Test
	void deleteReviews(){

		//create a review entity

		ReviewEntity entity = new ReviewEntity(PRODUCT_ID_OKAY, REVIEW_ID, "author-1", "subject-1", "content-1");
		//save it
		repository.save(entity);

		//verify there are exactly 1  entity for product id 1
		assertEquals(1, repository.findByProductId(PRODUCT_ID_OKAY).size());

		//send the DELETE request
		client.delete()
				.uri("/review?productId=" + PRODUCT_ID_OKAY)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody();

		//verify there are no entities for product id 1
		assertEquals(0, repository.findByProductId(PRODUCT_ID_OKAY).size());
	}

	@Test
	public void getReviewMissingParameter(){

		client.get()
				.uri("/review")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");

	}

	@Test
	public void getReviewsInvalidParameterString(){

		client.get()
				.uri("/review?productId=" + PRODUCT_ID_INVALID_STRING)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Type mismatch.");

	}

	@Test
	public void getReviewsInvalidParameterNegativeValue(){

		client.get()
				.uri("/review?productId=" + PRODUCT_ID_INVALID_NEGATIVE)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + PRODUCT_ID_INVALID_NEGATIVE);
	}


	@Test
	public void getReviewsNotFound(){

		int expectedLength = 0;

		client.get()
				.uri("/review?productId=" + PRODUCT_ID_NOT_FOUND)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(expectedLength);

	}

}
