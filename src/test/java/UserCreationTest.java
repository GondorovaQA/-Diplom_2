import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;
import org.example.UserClient;

public class UserCreationTest {

    @BeforeClass
    public static void setup() {

        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Test
    @Step
    public void testCreateUniqueUser() {
        String email = "null";
        String password = "password";
        String name = "Username";

        given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\" }")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("user.email", equalTo(null))
                .body("user.name", equalTo(null));
    }

    @Test
    @Step
    public void registerDuplicateUser() {
        String email = "existing-test-user@example.com";
        String password = "password123";
        String name = "Existing Test User";
        var registerData = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\" }";
        UserClient userClient = new UserClient();
        ValidatableResponse responseRegister1 = userClient.registerUser(registerData);
        ValidatableResponse responseRegister2 = userClient.registerUser(registerData);
        String token = responseRegister1.extract().path("accessToken");
        int statusCode = responseRegister2.extract().statusCode();
        boolean isRegistered = responseRegister2.extract().path("success");
        assertThat("Ошибка в коде или теле ответа", statusCode, is(403));
        assertThat("Ошибка в коде или теле ответа", isRegistered, is(equalTo(false)));

    }

    @Test
    @Step
    public void testMissingField() {

        String email = "missing-field-test@example.com";
        String password = "password123";
        String name = "";

        given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\" }")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @AfterClass
    public static void tearDown() {

        given()
                .contentType("application/json")
                .body("{ \"email\": \"existing-test-user@example.com\", \"password\": \"password123\", \"name\": \"Registered Test User\" }")
                .when()
                .delete("/api/auth/delete");
    }

}


