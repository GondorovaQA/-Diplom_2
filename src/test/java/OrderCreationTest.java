package site.stellarburgers;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.UserGenerator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;


@RunWith(Parameterized.class)
public class OrderCreationTest {

    private String authorizationHeader;
    private String body;
    private int expectedStatusCode;
    private static String accessToken;

    public OrderCreationTest(String authorizationHeader, String body, int expectedStatusCode) {
        this.authorizationHeader = authorizationHeader;
        this.body = body;
        this.expectedStatusCode = expectedStatusCode;
    }

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        accessToken = UserGenerator.getDefaultRegistrationData().getAccessToken();
    }

    @Parameterized.Parameters(name = "{index}: Test Create Order Without Authorization")
    public static Object[][] createParameters() {
        return new Object[][]{
                {null, "", 400},
        };
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
                .statusCode(expectedStatusCode);
    }
    @Test
    @Step
    public void testCreateOrderWithAuthorization() {
        List<String> ingredientsIds = getIngredientsIds();

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body("{\"ingredients\": [" + String.join(",", ingredientsIds) + "]}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400);
    }
    @Test
    @Step
    public void testCreateOrderWithoutIngredients() {
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body("{}") // Пустой JSON
                .when()
                .post("/api/orders")
                .then()
                .statusCode(403);
    }
    @Test
    @Step
    public void testCreateOrderWithInvalidIngredientHash() {
        List<String> invalidIngredientsIds = Arrays.asList("invalidHash1", "invalidHash2");

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body("{\"ingredients\": [" + String.join(",", invalidIngredientsIds) + "]}")
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400);
    }
    @Test
    @Step
    public void testGetOrdersUnauthorizedUser() {
        given()
                .contentType("application/json")
                .when()
                .get("/api/orders")
                .then()
                .statusCode(401);
    }

    private static List<String> getIngredientsIds() {
        Response response = given()
                .contentType("application/json")
                .when()
                .get("/api/ingredients");

        return response.jsonPath().getList("data._id");
    }
}
