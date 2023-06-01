package io.github.joxebus.mockapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiPath;

class MockApiTest {

	@Test
	void sampleTest(){
		assertEquals( 12, 5+7);
	}

	ApiConfiguration buildApiConfiguration() {
		ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setName("sample");

		Map<String, String[]> headers = Map.of("author", new String[]{"Omar Bautista"});

		ApiPath get = new ApiPath();
		get.setMethod("get");
		get.setStatusCode(200);
		get.setHeaders(headers);
		get.setBody("{\n" +
				"          \"code\": \"42TV\",\n" +
				"          \"description\": \"42 inch TV\",\n" +
				"          \"amenitySortIndex\": \"8\",\n" +
				"          \"chargable\": false\n" +
				"        }");

		apiConfiguration.setPaths(Map.of("GET_CODE", get));

		ApiPath post = new ApiPath();
		post.setMethod("post");
		post.setStatusCode(201);
		post.setHeaders(headers);

		apiConfiguration.setPaths(Map.of("GET_CODE", get, "POST_CODE", post));
		return apiConfiguration;
	}

}
