import Resources.Images;
import Response.ImageResponse;
import Response.ImageUpdateResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UpdateTest extends BaseTest {

    String uploadedImageId;
    String uploadedImageHashCode;

    @BeforeEach
    void setUp() {
        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .multiPart("image", getResourceFile(Images.JPEG_NORMAL))
                .when()
                .post(Endpoints.IMAGE_UPLOAD)
                .prettyPeek()
                .then()
                .spec(successResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);
        assertCommonSuccessResponse(response);
        assertThat(response.getData().getId(), not(emptyOrNullString()));
        assertThat(response.getData().getLink(), not(emptyOrNullString()));

        uploadedImageId = response.getData().getId();
        uploadedImageHashCode = response.getData().getDeletehash();
    }

    @AfterEach
    void afterEach() {
        given()
                .spec(requestAuthSpecification)
                .when()
                .delete(Endpoints.IMAGE, uploadedImageHashCode)
                .then()
                .spec(successResponseSpecification);
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
        ImageResponse response = given()
                .spec(requestAuthSpecification)
                .when()
                .get(Endpoints.IMAGE, uploadedImageId)
                .prettyPeek()
                .then()
                .spec(successResponseSpecification)
                .extract()
                .response()
                .as(ImageResponse.class);

        assertCommonSuccessResponse(response);
        assertThat(response.getData().getTitle(), is(title));
    }

    private void updateTitle(String title) {
        ImageUpdateResponse response = given()
                .spec(requestAuthSpecification)
                .param("title", title)
                .when()
                .post(Endpoints.IMAGE, uploadedImageId)
                .prettyPeek()
                .then()
                .spec(successResponseSpecification)
                .extract()
                .response()
                .as(ImageUpdateResponse.class);

        assertCommonSuccessResponse(response);
    }
}



