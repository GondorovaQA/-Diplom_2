import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class OrderGetTest {
    private static String accessToken;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        accessToken = getAccessToken();
    }

    private static String getAccessToken() {
        Response response = given()
                .contentType("application/json")
                .body("{\"email\": \"test-data@yandex.ru\", \"password\": \"password\"}")
                .when()
                .post("/api/auth/login");

        if (response.statusCode() == 200) {
            return response.jsonPath().getString("accessToken");
        } else {
            throw new RuntimeException("Failed to get access token");
        }
    }

    public static List<String> getIngredientsIds() {
        Response response = given()
                .contentType("application/json")
                .when()
                .get("/api/ingredients");

        List<String> ingredientsIds = response.jsonPath().getList("data._id");
        return ingredientsIds;
    }
    @Test
    @Step
    public void testGetAllOrders() {
        String accessToken = getAccessToken(); // Получение токена доступа

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .when()
                .get("/api/orders/all")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("total", greaterThanOrEqualTo(0));
    }
    @Test
    @Step
    public void testGetUserOrders() {
        String accessToken = getAccessToken();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("total", greaterThanOrEqualTo(0));
    }
    @AfterClass
    public static void tearDown() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/orders");
    }
}
