package lesson5.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import lesson5.dto.Product;

public interface ProductService {
    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @POST("products")
    Call<Product> createProduct(@Body Product createProductRequest);

    @Headers("Content-Type: application/json")
    @POST("products")
    Call<Product> createProductByString(@Body String body);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") int id);
}
