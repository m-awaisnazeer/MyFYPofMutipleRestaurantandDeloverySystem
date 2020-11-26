package com.comunisolve.newmultiplerestaurantsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyCategoryAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDatabase;
import com.comunisolve.newmultiplerestaurantsapp.Database.LocalCartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.MenuItemEvent;
import com.comunisolve.newmultiplerestaurantsapp.Model.FavoriteOnlyIdModel;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.comunisolve.newmultiplerestaurantsapp.Utils.SpaceItemDecoration;
import com.comunisolve.newmultiplerestaurantsapp.databinding.ActivityMenuBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MenuActivity extends AppCompatActivity {
    ActivityMenuBinding binding;


    MyCategoryAdapter adapter;
    RecyclerView category_recyclerView;


    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    CartDataSource cartDataSource;


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        countCartByRestaurant();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        //category_recyclerView = findViewById(R.id.recycler_category);
        //binding.imgRestaurant
        //binding.recyclerCategory
        //binding.toolbar
        //binding.badge

        init();
        initView();
        countCartByRestaurant();
        loadFavoriteByRestaurant();
    }

    private void loadFavoriteByRestaurant() {
        compositeDisposable.add(myRestaurantAPI.getFavoriteByRestaurant(Common.API_KEY,
                Common.currentUser.getFbid(),
                Common.currentRestaurant.getId()
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteOnlyIdModel -> {

                            if (favoriteOnlyIdModel.isSuccess()) {
                                if (favoriteOnlyIdModel.getResult() != null && favoriteOnlyIdModel.getResult().size() > 0) {

                                    Common.currentFavOfRestaurant = favoriteOnlyIdModel.getResult();

                                } else {
                                    Common.currentFavOfRestaurant = new ArrayList<>();

                                }
                            } else {
                                Toast.makeText(this, "[GET FAVORITE]" + favoriteOnlyIdModel.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "[GET FAVORITE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
    }

    private void countCartByRestaurant() {
        cartDataSource.countItemInCart(Common.currentUser.getFbid(),
                Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                        binding.badge.setText(integer.toString());
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toast.makeText(MenuActivity.this, "[COUNT CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initView() {
        binding.fab.setOnClickListener(view -> {
            startActivity(new Intent(MenuActivity.this,CartListActivity.class));

        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //set viewtype
        // if item is last, it will set full width om Grid layout

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter != null) {
                    switch (adapter.getItemViewType(position)) {
                        case Common.DEFAULT_COLUMN_COUNT:
                            return 1;
                        case Common.FULL_WIDTH_COLUMN:
                            return 2;
                        default:
                            return -1;
                    }
                } else
                    return -1;
            }
        });

        binding.recyclerCategory.setLayoutManager(layoutManager);
        binding.recyclerCategory.addItemDecoration(new SpaceItemDecoration(8));
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(getApplicationContext()).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDao());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
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
    public void loadMenuRestaurant(MenuItemEvent event) {
        if (event.isSuccess()) {
            Picasso.get().load(Common.API_RESTAURANT_ENDPOINT + event.getRestaurant().getImage()).into(binding.imgRestaurant);
            binding.toolbar.setTitle(event.getRestaurant().getName());

            setSupportActionBar(binding.toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            compositeDisposable.add(
                    myRestaurantAPI.getCatogories(Common.API_KEY, event.getRestaurant().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(menuModel -> {
                                        // Toast.makeText(this, "[Success Category]" + menuModel.getResult().get(0).getName(), Toast.LENGTH_SHORT).show();


                                        adapter = new MyCategoryAdapter(MenuActivity.this, menuModel.getResult());
                                        binding.recyclerCategory.setAdapter(adapter);
                                    },
                                    throwable -> {
                                        Toast.makeText(this, "[Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
            );

        } else {

        }

    }

}