package com.comunisolve.newmultiplerestaurantsapp.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDao cartDao;

    public LocalCartDataSource(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String userPhone, int restaurantId) {
        return cartDao.getAllCart(userPhone,restaurantId);
    }

    @Override
    public Single<Integer> countItemInCart(String userPhone, int restaurantId) {
        return cartDao.countItemInCart(userPhone, restaurantId);
    }

    @Override
    public Single<Integer> sumPrice(String userPhone, int restaurantId) {
        return cartDao.sumPrice(userPhone, restaurantId);
    }

    @Override
    public Single<CartItem> getItemInCart(String foodId, String userPhone, int restaurantId) {
        return cartDao.getItemInCart(foodId, userPhone, restaurantId);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItems) {
        return cartDao.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCart(CartItem cartItem) {
        return cartDao.updateCart(cartItem);
    }

    @Override
    public Single<Integer> deleteCart(CartItem cart) {
        return cartDao.deleteCart(cart);
    }

    @Override
    public Single<Integer> cleanCart(String userPhone, int restaurantId) {
        return cartDao.cleanCart(userPhone, restaurantId);
    }
}
