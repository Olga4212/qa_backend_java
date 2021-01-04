import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UploadTest extends BaseTest {

    String uploadedImageHashCode;

    @BeforeEach
    void setUp() {
        uploadedImageHashCode = null;
    }

    @AfterEach
    void afterEach() {
        if (uploadedImageHashCode != null) {
            given()
                    .headers(headers)
//                .log()
//                .all()
                    .when()
                    .delete("/image/" + uploadedImageHashCode)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    void uploadFileByUrlTest() {
        uploadedImageHashCode = given()
                .headers(headers)
                .param("image", "https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png")
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
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileBase64Test() throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(getResourceFile("image.jpeg"));
        String fileContentBase64 = Base64.encodeBase64String(fileContent);

        uploadedImageHashCode = given()
                .headers(headers)
                .param("image", fileContentBase64)
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
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileTest() throws IOException {
        File file = getResourceFile("image.jpeg");

        uploadedImageHashCode = given()
                .headers(headers)
                .multiPart("image", file)
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
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadEmptyDataTest() {
        given()
                .headers(headers)
                .param("image", "")
                .expect()
                .body("success", is(false))
                .body("status", is(400))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    void uploadTextFileTest() throws IOException {
        File file = getResourceFile("TextFile");

        given()
                .headers(headers)
                .multiPart("image", file)
                .expect()
                .body("success", is(false))
                .body("status", is(400))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);

    }

    @Test
    void uploadBigFileTest() throws IOException {
        File file = getResourceFile("big.jpg");

        given()
                .headers(headers)
                .multiPart("image", file)
                .expect()
                .body("success", is(false))
                .body("status", is(400))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);

    }
}



