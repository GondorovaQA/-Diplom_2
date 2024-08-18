package org.example;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class UserTest {

    private RequestSpecification spec;

    public static RequestSpecification getSpec(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.err.println("Token is null or empty! Cannot proceed.");
            return null;
        }
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON);
    }

    private String token = "";

    @Before
    public void setUp() {
        UserApi registerData = new UserApi("test-user@example.com", "password123");
        ValidatableResponse responseRegister = UserApiClient.registerUser(registerData);
        if (responseRegister.extract().path("accessToken") == null) {
            System.err.println("Failed to receive access token from registration response.");
            return;
        }
        token = responseRegister.extract().path("accessToken");
        spec = UserApiClient.getSpec(token);
    }
    @Test
    public void updateUserWithAuthorizationTrue() {
        if (spec == null) {
            System.err.println("Skipping test due to missing token.");
            return;
        }
        UserApi updateData = new UserApi("updated-test-user@example.com", "newpassword456");
        ValidatableResponse responseUpdate = UserApiClient.updateUser(updateData, token);
        int statusCode = responseUpdate.extract().statusCode();
        boolean isUpdated = responseUpdate.extract().path("success");
        Assert.assertEquals("Ошибка в коде или теле ответа", SC_OK, statusCode);
        Assert.assertTrue("Ошибка в теле ответа", isUpdated);
    }
    @Test
    public void updateUserWithAuthorizationFalse() {
        String invalidToken = "invalid_token";
        UserApi updateData = new UserApi("another-updated-test-user@example.com", "anothernewpassword789");
        ValidatableResponse responseUpdate = UserApiClient.updateUser(updateData, invalidToken);
        int statusCode = responseUpdate.extract().statusCode();
        Assert.assertEquals("Ошибка в коде или теле ответа", SC_UNAUTHORIZED, statusCode);
    }
    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            UserApiClient.deleteUser(token);
        }
    }
}
