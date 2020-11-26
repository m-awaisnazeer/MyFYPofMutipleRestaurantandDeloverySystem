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
import com.comunisolve.newmultiplerestaurantsapp.EventBus.FoodDetailEvent;
import com.comunisolve.newmultiplerestaurantsapp.FoodDetailsActivity;
import com.comunisolve.newmultiplerestaurantsapp.Interface.IOnRecyclerViewClickListner;
import com.comunisolve.newmultiplerestaurantsapp.Model.Favorite;
import com.comunisolve.newmultiplerestaurantsapp.Model.Restaurant;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.ViewHolder> {

    Context context;
    List<Favorite> favoriteList;
    CompositeDisposable compositeDisposable;
    IMyRestaurantAPI myRestaurantAPI;

    public MyFavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
        compositeDisposable = new CompositeDisposable();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    public void onDestroy() {
        compositeDisposable.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_favorite_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);
        Picasso.get().load(Common.API_RESTAURANT_ENDPOINT + favorite.getFoodImage()).into(holder.img_food);

        holder.txt_food_name.setText(favorite.getFoodName());

        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign))
                .append(favorite.getPrice()));

        holder.txt_restaurant_name.setText(favorite.getRestaurantName());

        //Event
        holder.setListner((view, position1) -> {

            compositeDisposable.add(myRestaurantAPI.getFoodById(Common.API_KEY,
                    favorite.getFoodId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {

                                if (foodModel.isSuccess()) {

                                    //when user click to favorite start FoodDetailsActivity
                                    context.startActivity(new Intent(context, FoodDetailsActivity.class));
                                    if (Common.currentRestaurant == null)
                                        Common.currentRestaurant = new Restaurant();

                                    Common.currentRestaurant.setId(favorite.getRestaurantId());
                                    Common.currentRestaurant.setName(favorite.getRestaurantName());


                                    EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodModel.getResult().get(0)));

                                } else {
                                    Toast.makeText(context, "[GET FOOD BY ID]" + foodModel.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            },
                            throwable -> {
                                Toast.makeText(context, "[GET FOOD BY ID]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_food;
        TextView txt_food_name, txt_food_price, txt_restaurant_name;

        IOnRecyclerViewClickListner listner;


        public void setListner(IOnRecyclerViewClickListner listner) {
            this.listner = listner;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_food = itemView.findViewById(R.id.img_food);
            txt_food_name = itemView.findViewById(R.id.txt_food_name);
            txt_food_price = itemView.findViewById(R.id.txt_food_price);
            txt_restaurant_name = itemView.findViewById(R.id.txt_restaurant_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listner.onClick(v, getAdapterPosition());
        }
    }
}
