package com.algaworks.algashop.product.catalog.contract.base;

import com.algaworks.algashop.product.catalog.contract.base.ProductBase;
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
public class ProductTest extends ProductBase {

	@Test
	public void validate_createProductV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.body("{\"name\":\"Notebook X11\",\"brand\":\"Deep Diver\",\"regularPrice\":1500.00,\"salePrice\":1000.00,\"enabled\":true,\"categoryId\":\"f5ab7a1e-37da-41e1-892b-a1d38275c2f2\",\"description\":\"A Gamer Notebook\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/api/v1/products");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['id']").matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
			assertThatJson(parsedJson).field("['addedAt']").matches("([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\\.\\d+)?(Z|[+-][01]\\d:[0-5]\\d)");
			assertThatJson(parsedJson).field("['name']").isEqualTo("Notebook X11");
			assertThatJson(parsedJson).field("['brand']").isEqualTo("Deep Diver");
			assertThatJson(parsedJson).field("['regularPrice']").isEqualTo(1500.0);
			assertThatJson(parsedJson).field("['salePrice']").isEqualTo(1000.0);
			assertThatJson(parsedJson).field("['inStock']").isEqualTo(false);
			assertThatJson(parsedJson).field("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).field("['category']").field("['id']").matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
			assertThatJson(parsedJson).field("['category']").field("['name']").isEqualTo("Notebook");
			assertThatJson(parsedJson).field("['description']").isEqualTo("A Gamer Notebook");
	}

	@Test
	public void validate_createProductV1BadRequest() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.body("{\"name\":\"\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/api/v1/products");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).matches("application/problem\\+json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/products");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/invalid-fields");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Invalid fields");
			assertThatJson(parsedJson).field("['detail']").isEqualTo("One or more fields are invalid");
			assertThatJson(parsedJson).field("['fields']").field("['name']").matches("^\\s*\\S[\\S\\s]*");
			assertThatJson(parsedJson).field("['fields']").field("['brand']").matches("^\\s*\\S[\\S\\s]*");
			assertThatJson(parsedJson).field("['fields']").field("['regularPrice']").matches("^\\s*\\S[\\S\\s]*");
			assertThatJson(parsedJson).field("['fields']").field("['salePrice']").matches("^\\s*\\S[\\S\\s]*");
			assertThatJson(parsedJson).field("['fields']").field("['enabled']").matches("^\\s*\\S[\\S\\s]*");
			assertThatJson(parsedJson).field("['fields']").field("['categoryId']").matches("^\\s*\\S[\\S\\s]*");
	}

	@Test
	public void validate_deleteProductByIdNotFoundV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.delete("/api/v1/products/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");

		// then:
			assertThat(response.statusCode()).isEqualTo(404);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/products/7a6f3c9b-2d8e-4f1a-b5e2-9c3d7f8a1b2e");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/not-found");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Not found");
	}

	@Test
	public void validate_deleteProductByIdV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.delete("/api/v1/products/f1d3a7c4-6b2e-4f8a-9217-5d9c2e1b3a5f");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

	@Test
	public void validate_findProductByIdNotFound() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/api/v1/products/21651a12-b126-4213-ac21-19f66ff4642e");

		// then:
			assertThat(response.statusCode()).isEqualTo(404);
			assertThat(response.header("Content-Type")).matches("application/problem\\+json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/products/21651a12-b126-4213-ac21-19f66ff4642e");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/not-found");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Not found");
	}

	@Test
	public void validate_findProductByIdV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/api/v1/products/fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['id']").isEqualTo("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");
			assertThatJson(parsedJson).field("['addedAt']").matches("([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\\.\\d+)?(Z|[+-][01]\\d:[0-5]\\d)");
			assertThatJson(parsedJson).field("['name']").isEqualTo("Notebook X11");
			assertThatJson(parsedJson).field("['brand']").isEqualTo("Deep Diver");
			assertThatJson(parsedJson).field("['regularPrice']").isEqualTo(1500.00);
			assertThatJson(parsedJson).field("['salePrice']").isEqualTo(1000.00);
			assertThatJson(parsedJson).field("['inStock']").isEqualTo(true);
			assertThatJson(parsedJson).field("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).field("['category']").field("['id']").matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
			assertThatJson(parsedJson).field("['category']").field("['name']").isEqualTo("Notebook");
			assertThatJson(parsedJson).field("['description']").isEqualTo("A Gamer Notebook");
	}

	@Test
	public void validate_findProductsV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"size\":\"10\",\"number\":0,\"totalElements\":2,\"totalPages\":1,\"content\":[{\"id\":\"d3f20899-f48a-4368-aa3f-c89dab2ef95e\",\"addedAt\":\"2014-04-14T12:23:34.123Z\",\"name\":\"Notebook X11\",\"brand\":\"Deep Diver\",\"regularPrice\":1500.00,\"salePrice\":1000.00,\"inStock\":true,\"enabled\":true,\"category\":{\"id\":\"99a03223-f78c-473b-a0e4-a51a09e8ea71\",\"name\":\"Notebook\"},\"description\":\"A Gamer Notebook\"},{\"id\":\"8b2e953b-1c07-4371-95f3-2eee00d388d8\",\"addedAt\":\"2012-02-12T12:23:34.123Z\",\"name\":\"Desktop I9000\",\"brand\":\"Deep Diver\",\"regularPrice\":3500.00,\"salePrice\":3000.00,\"inStock\":false,\"enabled\":true,\"category\":{\"id\":\"1aa7c0f9-8be3-4c65-9c2a-77a1449cd736\",\"name\":\"Desktop\"},\"description\":\"A Gamer Desktop\"}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("size","10")
					.queryParam("number","0")
					.get("/api/v1/products");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	public void validate_updateProductByIdNotFoundV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.body("{\"name\":\"Notebook X11\",\"brand\":\"Deep Diver\",\"regularPrice\":1500.00,\"salePrice\":1000.00,\"enabled\":false,\"categoryId\":\"f5ab7a1e-37da-41e1-892b-a1d38275c2f2\",\"description\":\"A Gamer Notebook\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/api/v1/products/c7e42a19-8b54-4c92-9d2a-1f8ef83a37e6");

		// then:
			assertThat(response.statusCode()).isEqualTo(404);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['instance']").isEqualTo("/api/v1/products/c7e42a19-8b54-4c92-9d2a-1f8ef83a37e6");
			assertThatJson(parsedJson).field("['type']").isEqualTo("/errors/not-found");
			assertThatJson(parsedJson).field("['title']").isEqualTo("Not found");
	}

	@Test
	public void validate_updateProductByIdV1() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.body("{\"name\":\"Notebook X11\",\"brand\":\"Deep Diver\",\"regularPrice\":1500.00,\"salePrice\":1000.00,\"enabled\":false,\"categoryId\":\"f5ab7a1e-37da-41e1-892b-a1d38275c2f2\",\"description\":\"A Gamer Notebook\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/api/v1/products/a3927f81-5d33-4b0e-b2e4-3c1a7bba8d5f");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("application/json.*");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['id']").matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
			assertThatJson(parsedJson).field("['addedAt']").matches("([0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\\.\\d+)?(Z|[+-][01]\\d:[0-5]\\d)");
			assertThatJson(parsedJson).field("['name']").isEqualTo("Notebook X11");
			assertThatJson(parsedJson).field("['brand']").isEqualTo("Deep Diver");
			assertThatJson(parsedJson).field("['regularPrice']").isEqualTo(1500.0);
			assertThatJson(parsedJson).field("['salePrice']").isEqualTo(1000.0);
			assertThatJson(parsedJson).field("['inStock']").matches("(true|false)");
			assertThatJson(parsedJson).field("['enabled']").matches("(true|false)");
			assertThatJson(parsedJson).field("['category']").field("['id']").matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
			assertThatJson(parsedJson).field("['category']").field("['name']").isEqualTo("Notebook");
			assertThatJson(parsedJson).field("['description']").isEqualTo("A Gamer Notebook");
	}

}
