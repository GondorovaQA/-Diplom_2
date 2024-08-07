import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class OrderCreationTest {
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
    public void testCreateOrderWithoutAuthorization() {
        List<String> ingredientsIds = getIngredientsIds();

        given()
                .contentType("application/json")
                .body("{\"ingredients\": [" + String.join(",", ingredientsIds) + "]}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400);
    }
    @Test
    @Step
    public void testCreateOrderWithoutIngredients() {
        String accessToken = getAccessToken();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("jwt malformed"));
    }
    @Test
    @Step
    public void testCreateOrderWithInvalidIngredientsHash() {
        String accessToken = getAccessToken();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body("{\"ingredients\": [\"invalid-hash\"]}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(403);
    }
    @Test
    @Step
    public void testGetOrdersUnauthorizedUser() {
        given()
                .contentType("application/json")
                .when()
                .get("/api/orders")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
    @AfterClass
    public static void tearDown() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/orders");
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
                .get("/api/orders");
    }


}








