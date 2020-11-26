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
    public Flowable<List<CartItem>> getAllCart(String fbid, int restaurantId) {
        return cartDao.getAllCart(fbid,restaurantId);
    }

    @Override
    public Single<Integer> countItemInCart(String fbid, int restaurantId) {
        return cartDao.countItemInCart(fbid, restaurantId);
    }

    @Override
    public Single<Long> sumPrice(String fbid, int restaurantId) {
        return cartDao.sumPrice(fbid, restaurantId);
    }

    @Override
    public Single<CartItem> getItemInCart(String foodId, String fbid, int restaurantId) {
        return cartDao.getItemInCart(foodId, fbid, restaurantId);
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
    public Single<Integer> cleanCart(String fbid, int restaurantId) {
        return cartDao.cleanCart(fbid, restaurantId);
    }
}
