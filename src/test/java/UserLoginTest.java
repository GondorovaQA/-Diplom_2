import io.restassured.RestAssured;
import org.example.LoginRequest;
import org.example.User;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;

public class UserLoginTest {
    private static User validUser;
    private static User invalidUser;

    @BeforeClass
    public static void createUser() {
        validUser = new User("unique-test-user@example.com", "password123");
        invalidUser = new User("invalid-login@test.com", "invalid-password");
    }

    @Test
    @Step
    public void testLoginWithValidCredentials() {
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
        LoginRequest loginRequest = new LoginRequest(invalidUser);

        given()
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
