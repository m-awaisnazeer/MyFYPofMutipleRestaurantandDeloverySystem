package com.comunisolve.newmultiplerestaurantsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDatabase;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartItem;
import com.comunisolve.newmultiplerestaurantsapp.Database.LocalCartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.FoodDetailEvent;
import com.comunisolve.newmultiplerestaurantsapp.FoodDetailsActivity;
import com.comunisolve.newmultiplerestaurantsapp.Interface.IFoodDetailOrCartClickListner;
import com.comunisolve.newmultiplerestaurantsapp.Model.FavoriteModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.FavoriteOnlyId;
import com.comunisolve.newmultiplerestaurantsapp.Model.Food;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.ViewHolder> {

    Context context;
    List<Food> foodList;
    CompositeDisposable compositeDisposable;
    CartDataSource cartDataSource;
    IMyRestaurantAPI myRestaurantAPI;

    public void onStop() {
        compositeDisposable.clear();
    }

    public MyFoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
        compositeDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDao());
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food fooditem = foodList.get(position);

        Picasso.get().load(Common.API_RESTAURANT_ENDPOINT + fooditem.getImage())
                .placeholder(R.drawable.app_icon).into(holder.img_food);

        holder.food_name.setText(fooditem.getName());
        holder.food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(fooditem.getPrice().toString()));

        //check Favorite
        if (Common.currentFavOfRestaurant != null && Common.currentFavOfRestaurant.size() > 0) {

            if (Common.checkFavorite(fooditem.getId())) {

                holder.img_fav.setImageResource(R.drawable.ic_favorite);
                holder.img_fav.setTag(true);
            } else {
                holder.img_fav.setImageResource(R.drawable.ic_un_favorite);
                holder.img_fav.setTag(false);
            }
        } else {
            holder.img_fav.setTag(false);
        }

        //Event
        holder.img_fav.setOnClickListener(v -> {
            ImageView fav = (ImageView) v;
            if ((Boolean) fav.getTag()) {
                // if tag = true -> favorite item clicked
                compositeDisposable.add(myRestaurantAPI.removeFavorite(
                        Common.API_KEY,
                        Common.currentUser.getFbid(),
                        fooditem.getId(),
                        Common.currentRestaurant.getId()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(favoriteModel -> {

                            if (favoriteModel.isSuccess() && favoriteModel.getMessage().contains("Success")) {
                                fav.setImageResource(R.drawable.ic_un_favorite);
                                fav.setTag(false);

                                if (Common.currentFavOfRestaurant != null)
                                    Common.removeFavorite(fooditem.getId());
                            }
                        }, throwable -> {
                            Toast.makeText(context, "[REMOVE FAV]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
            } else {
                compositeDisposable.add(myRestaurantAPI.insertFavorite(
                        Common.API_KEY,
                        Common.currentUser.getFbid(),
                        fooditem.getId(),
                        Common.currentRestaurant.getId(),
                        Common.currentRestaurant.getName(),
                        fooditem.getName(),
                        fooditem.getImage(),
                        fooditem.getPrice()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(favoriteModel -> {

                            if (favoriteModel.isSuccess() && favoriteModel.getMessage().contains("Success")) {
                                fav.setImageResource(R.drawable.ic_favorite);
                                fav.setTag(true);

                                if (Common.currentFavOfRestaurant != null)
                                    Common.currentFavOfRestaurant.add(new FavoriteOnlyId(fooditem.getId()));
                            }
                        }, throwable -> {
                            Toast.makeText(context, "[Add FAV]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
            }
        });
        holder.setListner((view, position1, isDetail) -> {
            if (isDetail) {
                context.startActivity(new Intent(context, FoodDetailsActivity.class));
                EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodList.get(position)));

            } else {

                //Cart create
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(fooditem.getId());
                cartItem.setFoodName(fooditem.getName());
                cartItem.setFoodPrice(fooditem.getPrice());
                cartItem.setFoodImage(fooditem.getImage());
                cartItem.setFoodQuantity(1);
                cartItem.setUserPhone(Common.currentUser.getUserPhone());
                cartItem.setRestaurantId(Common.currentRestaurant.getId());
                cartItem.setFoodAddon("NORMAL");
                cartItem.setFoodSize("NORMAL");
                cartItem.setFoodExtraPrice(0.0);
                cartItem.setFbid(Common.currentUser.getFbid());

                compositeDisposable.add(
                        cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                        },
                                        throwable -> {
                                            Toast.makeText(context, "[ADD CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                );
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_food, img_detail, img_cart, img_fav;
        TextView food_name, food_price;

        IFoodDetailOrCartClickListner listner;

        public void setListner(IFoodDetailOrCartClickListner listner) {
            this.listner = listner;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_food = itemView.findViewById(R.id.img_food);
            img_fav = itemView.findViewById(R.id.img_fav);
            img_detail = itemView.findViewById(R.id.img_detail);
            img_cart = itemView.findViewById(R.id.img_cart);
            food_name = itemView.findViewById(R.id.food_name);
            food_price = itemView.findViewById(R.id.food_price);

            img_detail.setOnClickListener(this);
            img_cart.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.img_detail) {
                listner.onFoodItemClickListner(view, getAdapterPosition(), true);
            } else if (view.getId() == R.id.img_cart) {
                listner.onFoodItemClickListner(view, getAdapterPosition(), false);
            }
        }
    }
}
