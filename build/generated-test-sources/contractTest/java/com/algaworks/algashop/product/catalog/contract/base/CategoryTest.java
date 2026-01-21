package com.algaworks.algashop.product.catalog.contract.base;

import com.algaworks.algashop.product.catalog.contract.base.CategoryBase;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;

@SuppressWarnings("rawtypes")
public class CategoryTest extends CategoryBase {

	@Test
	public void validate_createCategoryBadRequestV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"name\":\"\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/api/v1/categories");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).matches("application/problem\\+json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/categories");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/invalid-fields");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Invalid fields");
			assertThatJson(parsedJson).field("['detail']").isEqualTo("One or more fields are invalid");
			assertThatJson(parsedJson).field("['fields']").field("['name']").matches("^\\s*\\S[\\S\\s]*");
			assertThatJson(parsedJson).field("['fields']").field("['enabled']").matches("^\\s*\\S[\\S\\s]*");
	}

	@Test
	public void validate_createCategoryV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"name\":\"Electronics\",\"enabled\":true}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/api/v1/categories");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['id']").matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
			assertThatJson(parsedJson).field("['name']").isEqualTo("Electronics");
			assertThatJson(parsedJson).field("['enabled']").isEqualTo(true);
	}

	@Test
	public void validate_deleteCategoryByIdNotFoundV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.delete("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b9e");

		// then:
			assertThat(response.statusCode()).isEqualTo(404);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b9e");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/not-found");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Not found");
	}

	@Test
	public void validate_deleteCategoryV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.delete("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

	@Test
	public void validate_findCategoriesV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("page","0")
					.queryParam("size","2")
					.get("/api/v1/categories");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['number']").isEqualTo(0);
			assertThatJson(parsedJson).field("['size']").isEqualTo(2);
			assertThatJson(parsedJson).field("['totalPages']").isEqualTo(1);
			assertThatJson(parsedJson).field("['totalElements']").isEqualTo(2);
			assertThatJson(parsedJson).array("['content']").contains("['id']").matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
			assertThatJson(parsedJson).array("['content']").contains("['name']").isEqualTo("Electronics");
			assertThatJson(parsedJson).array("['content']").contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array("['content']").contains("['name']").isEqualTo("Books");
			assertThatJson(parsedJson).array("['content']").contains("['enabled']").isEqualTo(false);
	}

	@Test
	public void validate_findCategoryByIdNotFoundV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b6e");

		// then:
			assertThat(response.statusCode()).isEqualTo(404);
			assertThat(response.header("Content-Type")).matches("application/problem\\+json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b6e");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/not-found");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Not found");
	}

	@Test
	public void validate_findCategoryByIdV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['id']").isEqualTo("7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");
			assertThatJson(parsedJson).field("['name']").isEqualTo("Electronics");
			assertThatJson(parsedJson).field("['enabled']").isEqualTo(true);
	}

	@Test
	public void validate_updateCategoryByIdNotFoundV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"name\":\"Desktops\",\"enabled\":false}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b8e");

		// then:
			assertThat(response.statusCode()).isEqualTo(404);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b8e");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/not-found");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Not found");
	}

	@Test
	public void validate_updateCategoryV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"name\":\"Desktops\",\"enabled\":true}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/api/v1/categories/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['id']").isEqualTo("7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");
			assertThatJson(parsedJson).field("['name']").matches("^\\s*\\S[\\S\\s]*");
			assertThatJson(parsedJson).field("['enabled']").matches("(true|false)");
	}

}
