import io.restassured.response.ValidatableResponse;
import org.example.UserApi;
import org.example.UserApiClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
@RunWith(Parameterized.class)
public class UserTest {

    private UserApi registerData;
    private UserApi updateData;
    private String token = "";
    private int statusCode;
    private boolean isUpdated;

    @Parameterized.Parameters(name = "Изменение данных пользователя с авторизацией: {index} - isAuth={0}, expectedStatus={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, SC_OK},
                {false, SC_UNAUTHORIZED},
        });
    }

    @Parameterized.Parameter(0)
    public boolean isAuth;

    @Parameterized.Parameter(1)
    public int status;


    @Before
    public void setUp() {
        registerData = new UserApi("username", "password");
        updateData = new UserApi("anotherUsername", "anotherPassword");

        ValidatableResponse responseRegister = UserApiClient.registerUser(registerData);
        token = responseRegister.extract().path("accessToken");
    }
    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            ValidatableResponse responseDelete = UserApiClient.deleteUser(token);
        } else {
            System.out.println("Token is null or empty, skipping deletion.");
        }
    }
    @Test
    public void updateUserWithAuthorization() {
        String token2 = isAuth ? token : getValidDefaultToken();

        ValidatableResponse responseUpdate = UserApiClient.updateUser(updateData, token2);

        statusCode = responseUpdate.extract().statusCode();
        isUpdated = responseUpdate.extract().path("success");

        assertEquals("Ошибка", status, statusCode);
        assertEquals("Ошибка", isAuth, isUpdated);
    }

    private String getValidDefaultToken() {
        return "validDefaultToken";
    }
}
