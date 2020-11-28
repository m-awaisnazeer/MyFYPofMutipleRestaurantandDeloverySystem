
package com.comunisolve.newmultiplerestaurantsapp.Retrofit;


import com.comunisolve.newmultiplerestaurantsapp.Model.AddonModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.CreateOrderModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.FavoriteModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.FavoriteOnlyIdModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.FoodModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.MenuModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.OrderModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.RestaurantModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.SizeModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.UpdateOrderModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.UpdateUserModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.UserModel;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {

    /*
             ############# GET ################
  */
    @GET("user")
    Observable<UserModel> getUser(@Query("key") String apikey,
                                  @Query("fbid") String fbid);

    @GET("restaurant")
    Observable<RestaurantModel> getRestaurant(@Query("key") String apiKey);

    @GET("restaurantById")
    Observable<RestaurantModel> getRestaurantById(@Query("key") String apiKey,
                                                  @Query("restaurantId") String id);

    @GET("nearbyrestaurant")
    Observable<RestaurantModel> getNearByRestaurant(@Query("key") String apiKey,
                                                    @Query("lat") Double lat,
                                                    @Query("lng") Double lng,
                                                    @Query("distance") int distance);

    @GET("menu")
    Observable<MenuModel> getCatogories(@Query("key") String apiKey,
                                        @Query("restaurantId") int restaurantId);

    @GET("food")
    Observable<FoodModel> getFoodOfMenu(@Query("key") String apiKey,
                                        @Query("menuId") int menuId);

    @GET("foodById")
    Observable<FoodModel> getFoodById(@Query("key") String apiKey,
                                      @Query("foodId") int menuId);

    @GET("size")
    Observable<SizeModel> getSizeOfFood(@Query("key") String apiKey,
                                        @Query("foodId") int foodId);

    @GET("favorite")
    Observable<FavoriteModel> getFavoriteByUser(@Query("key") String apiKey,
                                                @Query("fbid") String fbid);

    @GET("favoriteByRestaurant")
    Observable<FavoriteOnlyIdModel> getFavoriteByRestaurant(@Query("key") String apiKey,
                                                            @Query("fbid") String fbid,
                                                            @Query("restaurantId") int restaurantId);


    @GET("addon")
    Observable<AddonModel> getAddonOfFood(@Query("key") String apiKey,
                                          @Query("foodId") int foodId);

    @GET("order")
    Observable<OrderModel> getOrder(@Query("key") String key,
                                    @Query("orderFBID") String orderFBID);

    /*
                ############# POST ################
     */

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("userPhone") String userPhone,
                                               @Field("userName") String userName,
                                               @Field("userAddress") String userAddress,
                                               @Field("fbid") String fbid);

    @POST("favorite")
    @FormUrlEncoded
    Observable<UpdateUserModel> insertFavorite(@Field("key") String apiKey,
                                               @Field("fbid") String fbid,
                                               @Field("foodId") int foodId,
                                               @Field("restaurantId") int restaurantId,
                                               @Field("restaurantName") String restaurantName,
                                               @Field("foodName") String foodName,
                                               @Field("foodImage") String foodImage,
                                               @Field("price") double price);


    @POST("createOrder")
    @FormUrlEncoded
    Observable<CreateOrderModel> createOrder(@Field("key") String key,
                                             @Field("orderFBID") String orderFBID,
                                             @Field("orderPhone") String orderPhone,
                                             @Field("orderName") String orderName,
                                             @Field("orderAddress") String orderAddress,
                                             @Field("orderDate") String orderDate,
                                             @Field("restaurantId") int restaurantId,
                                             @Field("transactionId") String transactiontId,
                                             @Field("cod") int cod,
                                             @Field("totalPrice") Double totalPrice, ////issue
                                             @Field("numOfItem") int numOfItem

    );

    @POST("updateOrder")
    @FormUrlEncoded
    Observable<UpdateOrderModel> updateOrder(@Field("key") String key,
                                             @Field("orderId") String orderId,
                                             @Field("orderDetail") String orderDetail);

    /*
              ############# DELETE ################
   */

    @DELETE("favorite")
    Observable<FavoriteModel> removeFavorite(
            @Query("key") String key,
            @Query("fbid") String fbid,
            @Query("foodId") int foodId,
            @Query("restaurantId") int restaurantId
    );

}
