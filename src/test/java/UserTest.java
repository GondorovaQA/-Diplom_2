;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/auth/user";
    private static String accessToken;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;

    }

    @Test
    @Step
    public void testGetUserInfo() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body("success", equalTo(false));
    }

    @Test
    @Step
    public void testUpdateUserInfo() {
        String newName = "NewName";

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body("{\"name\": \"" + newName + "\"}")
                .when()
                .patch()
                .then()
                .statusCode(403)
                .body("success", equalTo(false));
    }
}
