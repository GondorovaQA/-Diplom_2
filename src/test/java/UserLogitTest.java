import io.restassured.RestAssured;
import org.example.LoginRequest;
import org.example.User;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;

public class UserLogitTest {
    @BeforeClass
    public static void setup() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @Step
    public void testLoginWithValidCredentials() {
        User validUser = new User("unique-test-user@example.com", "password123");
        LoginRequest loginRequest = new LoginRequest(validUser);

        given()
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @Step
    public void testLoginWithInvalidCredentials() {
        String email = "invalid-login@test.com";
        String password = "invalid-password";

        given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body(("message"), equalTo("email or password are incorrect"));
    }

}
