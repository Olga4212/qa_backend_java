import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UpdateTest extends BaseTest {

    String uploadedImageId;
    String uploadedImageHashCode;

    @BeforeEach
    void setUp() {
        Response response = given()
                .headers(headers)
                .multiPart("image", getResourceFile("image.jpeg"))
                .expect()
                .body("success", is(true))
                .body("status", is(200))
                .body("data.id", not(emptyOrNullString()))
                .body("data.link", not(emptyOrNullString()))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response();

        uploadedImageId = response
                .jsonPath()
                .getString("data.id");
        uploadedImageHashCode = response
                .jsonPath()
                .getString("data.deletehash");
    }

    @AfterEach
    void afterEach() {
        given()
                .headers(headers)
//                .log()
//                .all()
                .when()
                .delete("/image/" + uploadedImageHashCode)
                .then()
                .statusCode(200);
    }


    @ParameterizedTest()
    @ValueSource(strings = {"Banana", "@#$%(*&^", "Банан", "Title with spaces",})
    void successUpdateTest(String title) {
        updateTitle(title);
        checkTitle(title);
    }

    @Test()
    void emptyUpdateTest() {
        updateTitle("");
        checkTitle(null);
    }

    @Test()
    void longUpdateTest() {
        String longValue = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        String cutLongValue = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678";
        updateTitle(longValue);
        checkTitle(cutLongValue);
    }

    private void checkTitle(String title) {
        given()
                .headers(headers)
                .expect()
                .body("success", is(true))
                .body("status", is(200))
                .body("data.title", is(title))
                .when()
                .get("/image/" + uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    private void updateTitle(String title) {
        given()
                .headers(headers)
                .param("title", title)
                .expect()
                .body("success", is(true))
                .body("status", is(200))
                .when()
                .post("/image/" + uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}



