import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Random;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserCreationTest {
    private String userEmail;
    private String userPassword;
    private String userName;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    private ValidatableResponse registerUser(String email, String password, String name) {
        return given()
                .contentType("application/json")
                .body("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"" + name + "\" }")
                .when()
                .post("/api/auth/register")
                .then();
    }
    private void generateUserDataWithRandomEmail() {
        Random random = new Random();
        userEmail = "test" + random.nextInt(100000) + "@example.com";
        userName = "Test User " + random.nextInt(100000);
        userPassword = "Test password" + random.nextInt(100000);
    }

    @Test
    @Step
    public void testCreateUniqueUser() {
        generateUserDataWithRandomEmail();
        registerUser(userEmail, userPassword, userName)
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Step
    public void registerDuplicateUser() {
        generateUserDataWithRandomEmail();
        String emailForSecondUser = userEmail.replaceFirst("\\d+", String.valueOf(new Random().nextInt(100000)));
        registerUser(userEmail, userPassword, userName);
        ValidatableResponse responseRegisterSecondUser = registerUser(emailForSecondUser, userPassword, userName);
        int statusCode = responseRegisterSecondUser.extract().statusCode();
        boolean isRegistered = responseRegisterSecondUser.extract().path("success");
        assertThat("User already exists", statusCode, is(403));
        assertThat("User already exists", isRegistered, is(equalTo(false)));
    }

    @Test
    @Step
    public void testMissingField() {
        generateUserDataWithRandomEmail();
        userName = "";
        given()
                .contentType("application/json")
                .body("{ \"email\": \"" + userEmail + "\", \"password\": \"" + userPassword + "\", \"name\": \"" + userName + "\" }")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
    @After
    public void tearDown() {
        given()
                .contentType("application/json")
                .body("{ \"email\": \"" + userEmail + "\", \"password\": \"" + userPassword + "\" }")
                .when()
                .delete("/api/auth/delete");
    }
}
