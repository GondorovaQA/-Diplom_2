import org.example.LoginRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class UserLoginTest {

    private String email;
    private String password;
    private int expectedStatusCode;
    private boolean success;
    private String message;

    public UserLoginTest(String email, String password, int expectedStatusCode, boolean success, String message) {
        this.email = email;
        this.password = password;
        this.expectedStatusCode = expectedStatusCode;
        this.success = success;
        this.message = message;
    }
    @Test
    public void testLogin() {
        LoginRequest loginRequest = new LoginRequest(email, password);

        given()
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(expectedStatusCode)
                .body("success", equalTo(success))
                .body("message", equalTo(message));
    }
    @Parameterized.Parameters(name = "{index}: Testing with email={0}, password={1}")
    public static Iterable<Object[]> loginDataProvider() {
        return Arrays.asList(new Object[][]{
                {"unique-test-user@example.com", "password123", 200, true, null},
                {"invalid-login@test.com", "invalid-password", 401, false, "email or password are incorrect"}
        });
    }
}
