import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.Step;

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

    @Test(expected = AssertionError.class)
    @Step
    public void testCreateExistingUser() {

        String email = "existing-test-user@example.com";
        String password = "password123";
        String name = "Registered Test User";

        given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\" }")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("message", equalTo("User with such email already exists"));
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


