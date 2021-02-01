package lesson5;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import retrofit2.Response;
import lesson5.dto.Product;
import lesson5.enums.Category;
import lesson5.service.ProductService;
import lesson5.util.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;

public class CreateProductTest {
    static ProductService productService;
    Product product;
    static Faker faker = new Faker();

    int id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle(Category.FOOD.title)
                .withPrice((int) (Math.random() * 10000));

        id = 0;
    }

    @Test
    @SneakyThrows
    void createProductInFoodCategoryTest() {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id = response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    @Test()
    @SneakyThrows
    void createProductWithNullIdSentTest() {
        Response<Product> response = productService.createProductByString(
                "{\"title\" : \""+faker.food().ingredient()+"\", \"price\" : 8983, \"categoryTitle\" : \"" + Category.FOOD.title + "\"}"
        )
                .execute();
        id = response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    static Stream<String> createProductWithIncorrectFormedBodyData() {
        List<String> data = new ArrayList<>();

        String title = faker.food().ingredient();
        String price = Integer.toString((int)(Math.random() * 10000));
        String category = Category.FOOD.title;

        // title as int, passes but shouldn't
        data.add("{\"title\" : 123, \"price\" : "+price+", \"categoryTitle\" : \"" + category + "\"}");

        // title as null, passes but shouldn't
        data.add("{\"title\" : null, \"price\" : "+price+", \"categoryTitle\" : \"" + category + "\"}");

        // empty title, passes but shouldn't
        data.add("{\"title\" : \"\", \"price\" : "+price+", \"categoryTitle\" : \"" + category + "\"}");

        return data.stream();
    }

    @ParameterizedTest()
    @MethodSource(value = "createProductWithIncorrectFormedBodyData")
    @SneakyThrows
    public void createProductWithIncorrectFormedBodyTest(String body) {
        Response<Product> response = productService.createProductByString(body)
                .execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"РусскоеНазвание", "with space", "我叔叔有最誠實的規定", "\uD83D\uDE0B", "Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"})
    @SneakyThrows
    void createProductInFoodCategoryWithCustomTitleTest(String title) {
        Response<Product> response = productService.createProduct(product.withTitle(title))
                .execute();
        id = response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    static Stream<String> createProductInFoodCategoryWithIncorrectPriceData() {
        List<String> data = new ArrayList<>();

        String title = faker.food().ingredient();
        String category = Category.FOOD.title;

        // title as int, passes but shouldn't
        data.add("{\"title\" : \"" + title + "\", \"price\" : 0, \"categoryTitle\" : \"" + category + "\"}");

        // title as null, passes but shouldn't
        data.add("{\"title\" : \"" + title + "\", \"price\" : -1, \"categoryTitle\" : \"" + category + "\"}");

        // empty title, passes but shouldn't
        data.add("{\"title\" : \"" + title + "\", \"price\" : 4294967296, \"categoryTitle\" : \"" + category + "\"}");

        return data.stream();
    }

    @ParameterizedTest()
    @MethodSource(value = "createProductInFoodCategoryWithIncorrectPriceData")
    @SneakyThrows
    void createProductInFoodCategoryWithIncorrectPriceTest(String body) {
        Response<Product> response = productService.createProductByString(body)
                .execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        if (id > 0) {
            Response<ResponseBody> response = productService.deleteProduct(id).execute();
            assertThat(response.isSuccessful(), CoreMatchers.is(true));
        }

    }
}