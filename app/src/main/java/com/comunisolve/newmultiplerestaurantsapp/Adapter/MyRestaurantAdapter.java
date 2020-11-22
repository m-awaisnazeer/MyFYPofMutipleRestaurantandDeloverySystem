package com.comunisolve.newmultiplerestaurantsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.MenuItemEvent;
import com.comunisolve.newmultiplerestaurantsapp.Interface.IOnRecyclerViewClickLstner;
import com.comunisolve.newmultiplerestaurantsapp.MenuActivity;
import com.comunisolve.newmultiplerestaurantsapp.Model.Restaurant;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.MyRestaurantAdapterViewHolder> {
    Context context;
    List<Restaurant> restaurantList;

    public MyRestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public MyRestaurantAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyRestaurantAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyRestaurantAdapterViewHolder holder, int position) {

        holder.txt_restaurant_address.setText(restaurantList.get(position).getAddress());
        holder.txt_restaurant_name.setText(restaurantList.get(position).getName());
        Picasso.get().load(Common.API_RESTAURANT_ENDPOINT + restaurantList.get(position).getImage()).into(holder.img_restaurant);

        holder.setLstner((view, position1) -> {

            Common.currentRestaurant = restaurantList.get(position);
            //here use postSticky. that mean this event will be listen from other activity
            //it will different with just 'post'
            EventBus.getDefault().postSticky(new MenuItemEvent(true, restaurantList.get(position)));
            context.startActivity(new Intent(context, MenuActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class MyRestaurantAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_restaurant;
        TextView txt_restaurant_name, txt_restaurant_address;
        IOnRecyclerViewClickLstner lstner;

        public MyRestaurantAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            img_restaurant = itemView.findViewById(R.id.img_restaurant);
            txt_restaurant_name = itemView.findViewById(R.id.txt_restaurant_name);
            txt_restaurant_address = itemView.findViewById(R.id.txt_restaurant_address);

            itemView.setOnClickListener(this);


        }

        public void setLstner(IOnRecyclerViewClickLstner lstner) {
            this.lstner = lstner;
        }

        @Override
        public void onClick(View view) {
            lstner.onClick(view, getAdapterPosition());
        }
    }
}
