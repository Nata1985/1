package com.geekbrains.spoonaccular;

import com.geekbrains.BaseTest;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class SpoonAccularApiTest extends BaseTest {


    private static final String API_KEY = "c0f7ee8317a94ef195f1a5d9040eec08";
    private static final String BASE_URL = "https://api.spoonacular.com";
    private static String user = "your-users-name27";
    private static Integer createdId;

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = BASE_URL;
       // createdId = null;
    }

    @Disabled
    @Test
    void testGetComplexSearch() {
        String actually = RestAssured.given()
                .queryParam("apiKey", API_KEY)
                .queryParam("query", "pasta")
                .queryParam("cuisine", "italian")
                .log()
                .uri()
                .expect()
                .log()
                .body()
                .statusCode(200)
                .time(lessThan(2000L))
                .body("results[0].id", Matchers.notNullValue())
                .body("offset", is(0))
                .body("number", is(10))
                .body("totalResults", is(127))
                .when()
                .get("recipes/complexSearch")
                .body()
                .asPrettyString();

        System.out.println(actually);


    }

    @Disabled
    @Test
    void testClassifyGroceryProduct() {

       Response response =
                RestAssured.given()
                        .queryParam("apiKey", API_KEY)
                        .body("{ \"title\": \"Kroger Vitamin A & D Reduced Fat 2% Milk\", \"upc\": \"\", \"plu_code\": \"\" }")
                        .expect()
                        .statusCode(200)
                        .time(lessThan(10000L))
                        .body("image", containsString("https://spoonacular.com/cdn/ingredients_100x100/milk.png"))
                        .body("category", equalTo("2 percent milk"))
                        .body("usdaCode", is(1174))
                        .log()
                        .all()
                        .when()
                        .post("/food/products/classify")
                        .prettyPeek();


        assertThat(response.jsonPath().get("breadcrumbs [0]"), equalTo("milk"));
    }

    @Test
    @Order(1)
    void createDish() {
        Response create = RestAssured.given()
                .queryParam("apiKey", API_KEY)
                .queryParam("hash", "8c59430b3f57033bc796c18bd43888970d0134ea")
                .pathParam("username", user)
                .when()
                .body(new File("src/test/resources/com.geekbrains/Spoonaccular/{} expected.json"))
                .expect()
                .statusCode(200)
                .time(lessThan(10000L))
                .body("id", Matchers.notNullValue())
                .when()
                .post("/mealplanner/{username}/items")
                .prettyPeek();
        JsonPath jsonPathEvaluator = create.jsonPath();
        createdId = jsonPathEvaluator.get("id");

    }

    @Test
    @Order(2)
    void deleteDish() {
        Assertions.assertNotNull(createdId, "createdId is null, you need start createDish first to initialize createdId");
        Response delete = RestAssured.given()
                .queryParam("apiKey", API_KEY)
                .queryParam("hash", "8c59430b3f57033bc796c18bd43888970d0134ea")
                .pathParam("username", user)
                .pathParam("id", createdId)
                .when()
                .expect()
                .statusCode(200)
                .time(lessThan(10000L))
                .body("status", equalTo("success"))
                .when()
                .delete("/mealplanner/{username}/items/{id}")
                .prettyPeek();


    }
    @Test
    void searchRecipesByIngredients() {

        Response response =
                RestAssured.given()
                        .queryParam("apiKey", API_KEY)
                        .queryParam("ingredients", "potatoes")
                        .queryParam("number", "1")
                        .queryParam("limitLicense", "true")
                        .expect()
                        .statusCode(200)
                        .time(lessThan(3000L))
                        .body("[0].id", is(641122))
                        .body("[0].title", equalTo("Curry Leaves Potato Chips"))
                        .body("[0].likes", is(4))
                        //.body("[0].statusCode", equalTo("HTTP/1.1 200 OK"))
                        .when()
                        .get("/recipes/findByIngredients")
                        .prettyPeek();



        
                }


    @Test
    void classifyCuisine() {

        Response response =
                RestAssured.given()
                        .queryParam("apiKey", API_KEY)
                        .expect()
                        .statusCode(200)
                        .time(lessThan(3000L))
                        .body("cuisine", equalTo("Mediterranean"))
                        .body("cuisines[0]", equalTo("Mediterranean"))
                        .body("cuisines[1]", equalTo("European"))
                        .body("cuisines[2]", equalTo("Italian"))
                        .when()
                        .post("/recipes/cuisine")
                        .prettyPeek();




}
