package com.comunisolve.newmultiplerestaurantsapp.Model;

public class FavoriteOnlyId {
    private int foodId;

    public int getFoodId() {
        return foodId;
    }

    public FavoriteOnlyId(int foodId) {
        this.foodId = foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
}
