package com.comunisolve.newmultiplerestaurantsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyFoodAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.FoodListEvent;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.comunisolve.newmultiplerestaurantsapp.databinding.ActivityFoodListBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodListActivity extends AppCompatActivity {

    ActivityFoodListBinding binding;
    private static final String TAG = "FoodListActivity";
    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    MyFoodAdapter adapter;


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        if (adapter !=null){
            adapter.onStop();
        }
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        binding.recyclerFoodList
        //binding.imgCategory
        init();
        initView();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerFoodList.setLayoutManager(linearLayoutManager);
        binding.recyclerFoodList.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(getApplicationContext()).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadFoodListByCategory(FoodListEvent event) {
        if (event.isSuccess()) {
            Log.d(TAG, "loadFoodListByCategory: ");
            Picasso.get().load(Common.API_RESTAURANT_ENDPOINT + event.getCategory().getImage()).into(binding.imgCategory);
            binding.toolbar.setTitle(event.getCategory().getName());

            setSupportActionBar(binding.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

           // dialog.show();

            compositeDisposable.add(myRestaurantAPI.getFoodOfMenu(Common.API_KEY,
                    event.getCategory().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {
                                if (foodModel.isSuccess()) {
                                   // Toast.makeText(this, "[GET FOOD SUCCESS]"+foodModel.getResult().get(0).getName(), Toast.LENGTH_SHORT).show();

                                     adapter = new MyFoodAdapter(this,foodModel.getResult());
                                    binding.recyclerFoodList.setAdapter(adapter);
                                }else {
                                  //  Toast.makeText(this, "[GET FOOD RESULT]"+foodModel.getResult().get(0).getName(), Toast.LENGTH_SHORT).show();

                                    Toast.makeText(this, "[GET FOOD RESULT]"+foodModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                //dialog.dismiss();
                            },
                            throwable -> {
                               // dialog.dismiss();
                                Toast.makeText(this, "[GET FOOD RESULT]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                            }));
        }
    }
}