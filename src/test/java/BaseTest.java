import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

abstract public class BaseTest {
    protected static Map<String, String> headers = new HashMap<>();

    @BeforeAll
    static void beforeAll() throws IOException {
        Properties props = loadProperties();
        headers.put("Authorization", "Bearer " + props.getProperty("token"));

        RestAssured.baseURI = props.getProperty("base.url");
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
}
