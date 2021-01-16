import Resources.Images;
import Response.ImageResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
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
                    .spec(requestAuthSpecification)
                    .when()
                    .delete(Endpoints.IMAGE, uploadedImageHashCode)
                    .then()
                    .spec(successResponseSpecification);
        }
    }

    @Test
    void uploadFileByUrlTest() {
        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .param("image", Images.PNG_URL)
                .when()
                .post(Endpoints.IMAGE_UPLOAD)
                .prettyPeek()
                .then()
                .spec(successResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);
        assertUploadSuccess(response);

        uploadedImageHashCode = response.getData().getDeletehash();
    }

    private void assertUploadSuccess(ImageResponse response) {
        assertCommonSuccessResponse(response);
        assertThat(response.getData().getId(), not(emptyOrNullString()));
        assertThat(response.getData().getLink(), not(emptyOrNullString()));
    }

    @ParameterizedTest()
    @ValueSource(strings = {Images.PNG_NORMAL, Images.JPEG_NORMAL,})
    void uploadFileBase64Test(String image) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(getResourceFile(image));
        String fileContentBase64 = Base64.encodeBase64String(fileContent);

        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .param("image", fileContentBase64)
                .when()
                .post(Endpoints.IMAGE_UPLOAD)
                .prettyPeek()
                .then()
                .spec(successResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);
        assertUploadSuccess(response);

        uploadedImageHashCode = response.getData().getDeletehash();
    }

    @ParameterizedTest()
    @ValueSource(strings = {Images.PNG_NORMAL, Images.JPEG_NORMAL,})
    void uploadFileTest(String image) throws IOException {
        File file = getResourceFile(image);

        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .multiPart("image", file)
                .when()
                .post(Endpoints.IMAGE_UPLOAD)
                .prettyPeek()
                .then()
                .spec(successResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);
        assertUploadSuccess(response);

        uploadedImageHashCode = response.getData().getDeletehash();
    }

    @Test
    void uploadEmptyDataTest() {
        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .param("image", "")
                .when()
                .post(Endpoints.IMAGE_UPLOAD)
                .prettyPeek()
                .then()
                .spec(failedResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);
        assertCommonFailedResponse(response);
        ;
    }

    @Test
    void uploadTextFileTest() throws IOException {
        File file = getResourceFile(Images.TEXT_FILE);

        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .multiPart("image", file)
                .when()
                .post(Endpoints.IMAGE_UPLOAD)
                .prettyPeek()
                .then()
                .spec(failedResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);
        assertCommonFailedResponse(response);
    }

    @Test
    void uploadBigFileTest() throws IOException {
        File file = getResourceFile(Images.JPEG_BIG_10MB);

        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .multiPart("image", file)
                .when()
                .post(Endpoints.IMAGE_UPLOAD)
                .prettyPeek()
                .then()
                .spec(failedResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);
        assertCommonFailedResponse(response);

    }
}



