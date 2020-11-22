
package com.comunisolve.newmultiplerestaurantsapp.Retrofit;




import com.comunisolve.newmultiplerestaurantsapp.Model.AddonModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.FoodModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.MenuModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.RestaurantModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.SizeModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.UpdateUserModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.UserModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {

    @GET("user")
    Observable<UserModel> getUser(@Query("key") String apikey,
                                  @Query("fbid") String fbid);

    @GET("restaurant")
    Observable<RestaurantModel> getRestaurant(@Query("key") String apiKey);

    @GET("menu")
    Observable<MenuModel> getCatogories(@Query("key") String apiKey,
                                        @Query("restaurantId") int restaurantId);

    @GET("food")
    Observable<FoodModel> getFoodOfMenu(@Query("key") String apiKey,
                                        @Query("menuId") int menuId);

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("userPhone") String userPhone,
                                               @Field("userName") String userName,
                                               @Field("userAddress") String userAddress,
                                               @Field("fbid") String fbid);

    @GET("size")
    Observable<SizeModel> getSizeOfFood(@Query("key") String apiKey,
                                        @Query("foodId") int foodId);

    @GET("addon")
    Observable<AddonModel> getAddonOfFood(@Query("key") String apiKey,
                                          @Query("foodId") int foodId);
}
