package com.comunisolve.newmultiplerestaurantsapp.Adapter;

import android.util.Log;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Model.Restaurant;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class RestaurantSliderAdapter extends SliderAdapter {

    List<Restaurant> restaurantList;
    private static final String TAG = "RestaurantSliderAdapter";

    public RestaurantSliderAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(Common.API_RESTAURANT_ENDPOINT+restaurantList.get(position).getImage());
        Log.d(TAG, "onBindImageSlide: success");
    }
}
