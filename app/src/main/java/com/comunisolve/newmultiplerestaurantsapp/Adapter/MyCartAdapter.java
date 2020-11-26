package com.comunisolve.newmultiplerestaurantsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.CartListActivity;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDatabase;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartItem;
import com.comunisolve.newmultiplerestaurantsapp.Database.LocalCartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.CalculatePriceEvent;
import com.comunisolve.newmultiplerestaurantsapp.Interface.IOnImageViewAdapterClickListner;
import com.comunisolve.newmultiplerestaurantsapp.Interface.IOnRecyclerViewClickListner;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    List<CartItem> cartItemList;
    Context context;
    CartDataSource cartDataSource;

    public MyCartAdapter(CartListActivity cartListActivity, List<CartItem> cartItems) {
        context = cartListActivity;
        cartItemList = cartItems;
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDao());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get().load(Common.API_RESTAURANT_ENDPOINT + cartItemList.get(position).getFoodImage()).into(holder.img_food);
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        holder.txt_food_name.setText(cartItemList.get(position).getFoodName().toString());
        holder.txt_food_price.setText(cartItemList.get(position).getFoodPrice().toString());
        holder.txt_quantitiy.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));

        Double finalResult = cartItemList.get(position).getFoodPrice() * cartItemList.get(position).getFoodQuantity();
        holder.txt_price_new.setText(String.valueOf(finalResult));

        holder.txt_extra_price.setText(new StringBuilder("Extra Price($): +")
                .append(cartItemList.get(position).getFoodExtraPrice()));

        //Event
        holder.setListner((view, position1, isDecrease, isDelete) -> {
            if (!isDelete) {

                if (isDecrease) {
                    if (cartItemList.get(position).getFoodQuantity() > 1)
                        cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity() - 1);
                } else {
                    if (cartItemList.get(position).getFoodQuantity() < 99)
                        cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity() + 1);
                }

                //Update Cart
                cartDataSource.updateCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                holder.txt_quantitiy.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));

                                EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Toast.makeText(context, "[UPDATE Cart]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {

                //Delete item
                cartDataSource.deleteCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                notifyItemRemoved(position);
                                EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Toast.makeText(context, "[Delete Cart]" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_food, img_decrease, img_increase, img_delete_food;
        TextView txt_food_name, txt_food_price, txt_quantitiy, txt_price_new, txt_extra_price;

        IOnImageViewAdapterClickListner listner;

        public void setListner(IOnImageViewAdapterClickListner listner) {
            this.listner = listner;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_food = itemView.findViewById(R.id.img_food);
            txt_food_name = itemView.findViewById(R.id.txt_food_name);
            txt_food_price = itemView.findViewById(R.id.txt_food_price);
            img_decrease = itemView.findViewById(R.id.img_decrease);
            txt_quantitiy = itemView.findViewById(R.id.txt_quantitiy);
            img_increase = itemView.findViewById(R.id.img_increase);
            txt_price_new = itemView.findViewById(R.id.txt_price_new);
            txt_extra_price = itemView.findViewById(R.id.txt_extra_price);
            img_delete_food = itemView.findViewById(R.id.img_delete_food);
            img_food = itemView.findViewById(R.id.img_food);
            img_food = itemView.findViewById(R.id.img_food);
            img_food = itemView.findViewById(R.id.img_food);

            img_decrease.setOnClickListener(this);
            img_increase.setOnClickListener(this);
            img_delete_food.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v == img_decrease) {
                listner.onCalculatePriceListner(v, getAdapterPosition(), true, false);
            } else if (v == img_decrease) {
                listner.onCalculatePriceListner(v, getAdapterPosition(), false, false);
            } else if (v == img_decrease) {
                listner.onCalculatePriceListner(v, getAdapterPosition(), true, true);
            }
        }
    }
}
