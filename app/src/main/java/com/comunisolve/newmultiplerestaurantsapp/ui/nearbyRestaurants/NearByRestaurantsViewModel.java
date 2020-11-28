package com.comunisolve.newmultiplerestaurantsapp.ui.nearbyRestaurants;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.LoadingDialogClosingEvent;
import com.comunisolve.newmultiplerestaurantsapp.Model.RestaurantModel;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NearByRestaurantsViewModel extends AndroidViewModel {
    IMyRestaurantAPI myRestaurantAPI;
    MutableLiveData<RestaurantModel> modelMutableLiveData;
    MutableLiveData<RestaurantModel> getRestaurantByIdLiveData;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public NearByRestaurantsViewModel(@NonNull Application application) {
        super(application);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    public void onDestroy() {
        compositeDisposable.clear();
    }

    public MutableLiveData<RestaurantModel> getNearByRestaurants(double latitude, double longitude, int distance) {
        if (modelMutableLiveData == null) {
            modelMutableLiveData = new MutableLiveData();
            compositeDisposable.add(myRestaurantAPI.getNearByRestaurant(Common.API_KEY, latitude, longitude, distance)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(restaurantModel -> {

                        modelMutableLiveData.setValue(restaurantModel);
                    }, throwable -> {
                        EventBus.getDefault().postSticky(new LoadingDialogClosingEvent(true));
                        Toast.makeText(getApplication(), "[Near By Restaurant Error]", Toast.LENGTH_SHORT).show();
                    }));
        }

        return modelMutableLiveData;
    }

    public MutableLiveData<RestaurantModel> getRestaurantById(String id) {
        if (getRestaurantByIdLiveData == null) {
            getRestaurantByIdLiveData = new MutableLiveData();
            compositeDisposable.add(myRestaurantAPI.getRestaurantById(Common.API_KEY, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(restaurantModel -> {
                        getRestaurantByIdLiveData.setValue(restaurantModel);

                    }, throwable -> {
                        Toast.makeText(getApplication(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }
        return getRestaurantByIdLiveData;

    }


}
