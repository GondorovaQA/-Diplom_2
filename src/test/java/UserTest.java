import io.qameta.allure.Step;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTest {

    private static String getAccessToken() {
        return "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY2ODAzMzU3OWVkMjgwMDAxYjQ1NmMzMyIsImlhdCI6MTcyMjk3OTE3NiwiZXhwIjoxNzIyOTgwMzc2fQ.cD6Z2SNVlsi2bQITX1-RFzW1GG2QIpSkJ4F6_knIWyM";
    }
    private static String accessToken;


    @Test
    @Step("Изменение данных пользователя с авторизацией")
    public void testUpdateUserInfoWithAuthorization() {
        String newName = "UpdatedName";
        accessToken = getAccessToken();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body("{\"name\": \"" + newName + "\"}")
                .when()
                .patch()
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Изменение данных пользователя без авторизации")
    public void testUpdateUserInfoWithoutAuthorization() {
        String newName = "AnotherName";

        given()
                .contentType("application/json")
                .body("{\"name\": \"" + newName + "\"}")
                .when()
                .patch()
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

}
