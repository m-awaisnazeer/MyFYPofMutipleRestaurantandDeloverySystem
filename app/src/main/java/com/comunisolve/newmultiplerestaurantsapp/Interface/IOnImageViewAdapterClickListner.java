package com.comunisolve.newmultiplerestaurantsapp.Interface;

import android.view.View;

public interface IOnImageViewAdapterClickListner {
    void onCalculatePriceListner(View view,int position,boolean isDecrease,boolean isDelete);
}
