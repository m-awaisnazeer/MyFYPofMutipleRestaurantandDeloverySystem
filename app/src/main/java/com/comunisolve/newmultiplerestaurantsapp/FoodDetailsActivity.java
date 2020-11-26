package com.comunisolve.newmultiplerestaurantsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyAddonAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDatabase;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartItem;
import com.comunisolve.newmultiplerestaurantsapp.Database.LocalCartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.AddOnEventChange;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.AddOnLoadEvent;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.FoodDetailEvent;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.SizeLoadEvent;
import com.comunisolve.newmultiplerestaurantsapp.Model.Food;
import com.comunisolve.newmultiplerestaurantsapp.Model.Size;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.comunisolve.newmultiplerestaurantsapp.databinding.ActivityFoodDetailsBinding;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodDetailsActivity extends AppCompatActivity {

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    ActivityFoodDetailsBinding binding;
    CartDataSource cartDataSource;

    Food selectedFood;
    private Double originalPrice;
    private double sizePrice = 0.0;
    private String sizeSelected;
    private Double addonPrice;
    private double extraPrice;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initView();
    }

    private void initView() {

        //binding.fabAddToCart
        //binding.btnViewCart
        // binding.txtMoney
        //binding.rdiGroupSize
        //binding.recyclerAddon
        //binding.imgFoodDetails

        binding.fabAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(selectedFood.getId());
                cartItem.setFoodName(selectedFood.getName());
                cartItem.setFoodPrice(selectedFood.getPrice());
                cartItem.setFoodImage(selectedFood.getImage());
                cartItem.setFoodQuantity(1);
                cartItem.setUserPhone(Common.currentUser.getUserPhone());
                cartItem.setRestaurantId(Common.currentRestaurant.getId());
                cartItem.setFoodAddon(new Gson().toJson(Common.addonList));
                cartItem.setFoodSize(sizeSelected);
                cartItem.setFoodExtraPrice(extraPrice);
                cartItem.setFbid(Common.currentUser.getFbid());

                compositeDisposable.add(
                        cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() ->{
                                            Toast.makeText(FoodDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                        },
                                        throwable -> {
                                            Toast.makeText(FoodDetailsActivity.this, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                );
            }
        });
    }

    private void init() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDao());

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT)
                .create(IMyRestaurantAPI.class);
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
    public void displayFoodDetail(FoodDetailEvent event) {
        if (event.isSuccess()) {

            binding.toolbar.setTitle(event.getFood().getName());

            setSupportActionBar(binding.toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            selectedFood = event.getFood();
            originalPrice = event.getFood().getPrice();
            binding.txtDescription.setText(event.getFood().getDescription());
            binding.txtMoney.setText(String.valueOf(originalPrice));

            Picasso.get().load(Common.API_RESTAURANT_ENDPOINT+event.getFood().getImage()).into(binding.imgFoodDetails);

            if (event.getFood().isSize() && event.getFood().isAddon()) {

                dialog.show();
                compositeDisposable.add(
                        myRestaurantAPI.getSizeOfFood(Common.API_KEY, event.getFood().getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(sizeModel -> {

                                            // SEND LOCAL EVENT BUS
                                            EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));
                                            Toast.makeText(this, "Success SIZE "+sizeModel.getResult().get(0).getDescription(), Toast.LENGTH_SHORT).show();

                                            //Load addon after load size
                                            dialog.show();

                                            compositeDisposable.add(
                                                    myRestaurantAPI.getAddonOfFood(Common.API_KEY, event.getFood().getId())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(addonModel -> {
                                                                        Toast.makeText(this, "& Success ADDON"+addonModel.getResult().get(0).getName(), Toast.LENGTH_SHORT).show();
                                                                        dialog.dismiss();
                                                                        EventBus.getDefault().post(new AddOnLoadEvent(true, addonModel.getResult()));
                                                                    },
                                                                    throwable -> {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(this, "[LOAD ADDON]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    })
                                            );
                                        },
                                        throwable -> {
                                            dialog.dismiss();
                                            Toast.makeText(this, "[LOAD SIZE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                );

            } else {
                if (event.getFood().isSize()) {

                    compositeDisposable.add(
                            myRestaurantAPI.getSizeOfFood(Common.API_KEY, event.getFood().getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(sizeModel -> {
                                                dialog.dismiss();
                                                // SEND LOCAL EVENT BUS
                                                EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));
                                                Toast.makeText(this, "Success Size", Toast.LENGTH_SHORT).show();

                                                //Load addon after load size

                                            },
                                            throwable -> {
                                                dialog.dismiss();
                                                Toast.makeText(this, "[LOAD SIZE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            })
                    );
                }

                if (event.getFood().isAddon()) {

                    dialog.show();

                    compositeDisposable.add(
                            myRestaurantAPI.getAddonOfFood(Common.API_KEY, event.getFood().getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(addonModel -> {
                                                Toast.makeText(this, "Success ADDON", Toast.LENGTH_SHORT).show();

                                                dialog.dismiss();
                                                EventBus.getDefault().post(new AddOnLoadEvent(true, addonModel.getResult()));
                                            },
                                            throwable -> {
                                                dialog.dismiss();
                                                Toast.makeText(this, "[LOAD ADDON]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            })
                    );
                }
            }

        }
    }
    private void calculatePrice() {

         extraPrice = 0.0;
        double newPrice;

        extraPrice +=sizePrice;
        extraPrice +=addonPrice;

        newPrice = originalPrice + extraPrice;

        Toast.makeText(this, ""+newPrice, Toast.LENGTH_SHORT).show();
        binding.txtMoney.setText(String.valueOf(newPrice));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displaySize(SizeLoadEvent event) {
        if (event.isSuccess()) {
            //Create radio button base on size length
            for (Size size : event.getSizeList()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            sizePrice = size.getExtraPrice();
                        } else {
                            sizePrice = -size.getExtraPrice();
                        }
                        calculatePrice();
                        sizeSelected = size.getDescription();
                    }
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                radioButton.setLayoutParams(params);

                radioButton.setText(size.getDescription());
                radioButton.setTag(size.getExtraPrice());


                binding.rdiGroupSize.addView(radioButton);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayAddon(AddOnLoadEvent event){

        if (event.isSuccess()){
            binding.recyclerAddon.setHasFixedSize(true);
            binding.recyclerAddon.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerAddon.setAdapter(new MyAddonAdapter(FoodDetailsActivity.this,event.getAddonList()));
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPriceChange(AddOnEventChange event){

        if (event.isAdd()){
            Toast.makeText(this, "Price Increased", Toast.LENGTH_SHORT).show();
            addonPrice +=event.getAddon().getExtraPrice();
        }else {
            addonPrice -= event.getAddon().getExtraPrice();
            Toast.makeText(this, "Price Decreased", Toast.LENGTH_SHORT).show();

        }
        calculatePrice();


    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void priceChange(AddOnEventChange eventChange){
        if (eventChange.isAdd())
            addonPrice +=eventChange.getAddon().getExtraPrice();
        else {
            addonPrice -= eventChange.getAddon().getExtraPrice();
            Toast.makeText(this, "Price Decreased", Toast.LENGTH_SHORT).show();
        }
        calculatePrice();

    }

}