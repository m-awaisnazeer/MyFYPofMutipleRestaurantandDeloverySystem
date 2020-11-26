package com.comunisolve.newmultiplerestaurantsapp.ui.orderhistory;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Model.OrderModel;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrderHistoryViewModel extends AndroidViewModel {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    MutableLiveData<OrderModel> modelMutableLiveData;

    public void onDestroy() {
        compositeDisposable.clear();
    }

    public OrderHistoryViewModel(@NonNull Application application) {
        super(application);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);


    }

    public MutableLiveData<OrderModel> getOrderHistory() {

        if (modelMutableLiveData == null) {
            modelMutableLiveData = new MutableLiveData();
            compositeDisposable.add(myRestaurantAPI.getOrder(Common.API_KEY
                    , Common.currentUser.getFbid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(orderModel -> {
                        modelMutableLiveData.setValue(orderModel);

                    }, throwable -> {
                        Toast.makeText(getApplication(), "[GET ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }

        return modelMutableLiveData;
    }

}