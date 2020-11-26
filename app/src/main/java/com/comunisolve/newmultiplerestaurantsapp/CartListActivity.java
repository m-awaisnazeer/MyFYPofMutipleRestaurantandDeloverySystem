package com.comunisolve.newmultiplerestaurantsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyCartAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDatabase;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartItem;
import com.comunisolve.newmultiplerestaurantsapp.Database.LocalCartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.CalculatePriceEvent;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.SendTotalCashEvet;
import com.comunisolve.newmultiplerestaurantsapp.databinding.ActivityCardListBinding;
import com.comunisolve.newmultiplerestaurantsapp.databinding.ActivityPlaceOrderBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartListActivity extends AppCompatActivity {

    @NonNull
    ActivityCardListBinding binding;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initView();

        getAllItemInCart();
    }

    private void getAllItemInCart() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid(),
                Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {

                    if (cartItems.isEmpty()) {
                        binding.btnOrder.setText("Empty Cart");
                        binding.btnOrder.setEnabled(false);
                        binding.btnOrder.setBackgroundResource(android.R.color.darker_gray);
                    } else {
                        binding.btnOrder.setText(getString(R.string.place_order));
                        binding.btnOrder.setEnabled(true);
                        binding.btnOrder.setBackgroundResource(R.color.colorPrimary);

                        MyCartAdapter adapter = new MyCartAdapter(CartListActivity.this, cartItems);
                        binding.recyclerCart.setAdapter(adapter);

                        calculateCartTotalPrice();
                    }
                }, throwable -> {
                    Toast.makeText(this, "[GET CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })

        );
    }

    private void calculateCartTotalPrice() {
        cartDataSource.sumPrice(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Long aLong) {

                        if (aLong == 0) {
                            binding.btnOrder.setText("Empty Cart");
                            binding.btnOrder.setEnabled(false);
                            binding.btnOrder.setBackgroundResource(android.R.color.darker_gray);
                        } else {
                            binding.btnOrder.setText("Place Order");
                            binding.btnOrder.setEnabled(true);
                            binding.btnOrder.setBackgroundResource(R.color.colorPrimary);
                        }

                        binding.txtFinalOrice.setText(String.valueOf(aLong));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (e.getMessage().contains("Query returned empty"))
                            binding.txtFinalOrice.setText("0");
                        else
                            Toast.makeText(CartListActivity.this, "[SUM PRICE]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDao());

    }

    private void initView() {
        binding.toolbar.setTitle(getString(R.string.cart));
        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerCart.setLayoutManager(linearLayoutManager);

        binding.recyclerCart.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));

        binding.btnOrder.setOnClickListener(v -> {

            EventBus.getDefault().postSticky(new SendTotalCashEvet(binding.txtFinalOrice.getText().toString()));
            startActivity(new Intent(CartListActivity.this, PlaceOrderActivity.class));
        });
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
    public void calculatePrice(CalculatePriceEvent event) {
        if (event != null) {
            calculateCartTotalPrice();
        }
    }
}