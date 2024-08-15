package org.example;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;
public class UserApiClient extends Client{

    private final static String REGISTER_USER_PATH = "api/auth/register";
    private final static String UPDATE_USER_PATH = "api/auth/user";

    @Step
    public static ValidatableResponse registerUser(UserApi data) {
        return given()
                .spec(getSpec())
                .body(data)
                .when()
                .post(REGISTER_USER_PATH)
                .then();
    }

    @Step
    public static ValidatableResponse updateUser(UserApi data, String bearerPlusToken) {
        return given()
                .spec(getSpec(bearerPlusToken))
                .body(data)
                .when()
                .patch(UPDATE_USER_PATH)
                .then();
    }

    @Step
    public static ValidatableResponse deleteUser(String bearerPlusToken) {
        return given()
                .spec(getSpec(bearerPlusToken))
                .when()
                .delete(UPDATE_USER_PATH)
                .then();
    }

}

