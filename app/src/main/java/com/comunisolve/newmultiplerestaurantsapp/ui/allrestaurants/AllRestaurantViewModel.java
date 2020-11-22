package com.comunisolve.newmultiplerestaurantsapp.ui.allrestaurants;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllRestaurantViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllRestaurantViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}