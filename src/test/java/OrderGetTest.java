import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.List;
import java.util.Random;

public class OrderGetTest {
    private static String accessToken;
    private static String userEmail;
    private static String userName;
    private static String userPassword;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        generateUserDataWithRandomEmail();
        registerUser(userEmail, userPassword, userName);
        accessToken = getAccessToken();
    }
    private static ValidatableResponse registerUser(String email, String password, String name) {
        return given()
                .contentType("application/json")
                .body(String.format("{ \"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\" }", email, password, name))
                .when()
                .post("/api/auth/register")
                .then();
    }

    private static void generateUserDataWithRandomEmail() {
        Random random = new Random();
        userEmail = "test" + random.nextInt(100000) + "@example.com";
        userName = "Test User " + random.nextInt(100000);
        userPassword = "Test password" + random.nextInt(100000);
    }
    private static String getAccessToken() {
        Response response = given()
                .contentType("application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", userEmail, userPassword))
                .when()
                .post("/api/auth/login");

        if (response.statusCode() == 200) {
            return response.jsonPath().getString("accessToken");
        } else {
            throw new RuntimeException("Failed to get access token");
        }
    }

    @Test
    @Step
    public void testGetAllOrders() {
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
    public void testGetUserOrders() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .when()
                .get("/api/orders")
                .then()
                .statusCode(403);

        JsonPath jsonPathEvaluator = given().get("/api/orders").jsonPath();

        assertThat(jsonPathEvaluator.getString("success"), equalTo("false"));

    }
    @AfterClass
    public static void tearDown() {
        given()
                .contentType("application/json")
                .body(String.format("{ \"email\": \"%s\", \"password\": \"%s\" }", userEmail, userPassword))
                .when()
                .delete("/api/auth/delete");
    }
}
