package com.comunisolve.newmultiplerestaurantsapp.ui.allrestaurants;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyRestaurantAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Adapter.RestaurantSliderAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.RestaurantLoadEvent;
import com.comunisolve.newmultiplerestaurantsapp.Model.Restaurant;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.comunisolve.newmultiplerestaurantsapp.Services.PicassoImageLoadingService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class AllRestaurants extends Fragment {

    private AllRestaurantViewModel allRestaurantViewModel;
    Slider banner_slider;
    RecyclerView recycler_restaurant;
    private static final String TAG = "NearByRestaurant";


    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    //FragmentNearbyRestaurantBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allRestaurantViewModel =
                new ViewModelProvider(this).get(AllRestaurantViewModel.class);
        View root = inflater.inflate(R.layout.fragment_all_restaurant, container, false);

        banner_slider = root.findViewById(R.id.banner_slider);
        recycler_restaurant = root.findViewById(R.id.recycler_restaurant);

        //binding = FragmentNearbyRestaurantBinding.inflate(getLayoutInflater());

        init();
        initView();
        loadRestaurant();
        return root;
    }

    private void loadRestaurant() {
        dialog.show();
        compositeDisposable.add(myRestaurantAPI.getRestaurant(Common.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantModel -> {
                            Log.d(TAG, "loadRestaurant: "+restaurantModel.getResult().get(0).getName());
                            EventBus.getDefault().post(new RestaurantLoadEvent(true, restaurantModel.getResult()));
                        },
                        throwable -> {
                            Log.d(TAG, "loadRestaurant: "+throwable.getMessage());
                            EventBus.getDefault().post(new RestaurantLoadEvent(false, throwable.getMessage()));

                        }));
    }

    private void initView() {
        recycler_restaurant.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler_restaurant.addItemDecoration(new DividerItemDecoration(requireContext(), new LinearLayoutManager(requireContext()).getOrientation()));
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        Slider.init(new PicassoImageLoadingService());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processRestaurantLoadEvent(RestaurantLoadEvent event) {
        if (event.isSuccess()) {
            displayBanner(event.getRestaurantList());
            displayRestaurant(event.getRestaurantList());
        } else {
            Toast.makeText(requireContext(), "[RESTAURANT LOAD]" + event.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    private void displayRestaurant(List<Restaurant> restaurantList) {
        MyRestaurantAdapter adapter = new MyRestaurantAdapter(requireContext(),restaurantList);
        recycler_restaurant.setAdapter(adapter);
    }

    private void displayBanner(List<Restaurant> restaurantList) {
        Log.d(TAG, "displayBanner: success");
       // Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
        banner_slider.setAdapter(new RestaurantSliderAdapter(restaurantList));
    }
}