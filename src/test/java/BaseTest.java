import Response.AbstractResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

abstract public class BaseTest {
    protected static Map<String, String> headers = new HashMap<>();


    static ResponseSpecification successResponseSpecification = null;
    static ResponseSpecification failedResponseSpecification = null;
    static RequestSpecification requestAuthSpecification = null;

    @BeforeAll
    static void beforeAll() throws IOException {
        Properties props = loadProperties();
        headers.put("Authorization", "Bearer " + props.getProperty("token"));

        RestAssured.baseURI = props.getProperty("base.url");

        successResponseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .log(LogDetail.ALL)
                .build();

        failedResponseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .log(LogDetail.ALL)
                .build();

        requestAuthSpecification = new RequestSpecBuilder()
                .addHeaders(headers)
                .log(LogDetail.ALL)
                .build();
    }


    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("src/test/resources/application.properties"));
        return props;
    }

    protected File getResourceFile(String s) {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(s)).getFile());
    }

    protected void assertCommonSuccessResponse(AbstractResponse response) {
        assertThat(response.getSuccess(), equalTo(true));
        assertThat(response.getStatus(), equalTo(200));
    }

    protected void assertCommonFailedResponse(AbstractResponse response) {
        assertThat(response.getSuccess(), equalTo(false));
        assertThat(response.getStatus(), equalTo(400));
    }
}
