package com.comunisolve.newmultiplerestaurantsapp.ui.updateInformation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UpdateInfoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UpdateInfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}