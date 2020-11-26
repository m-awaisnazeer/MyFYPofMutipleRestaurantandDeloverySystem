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
import com.comunisolve.newmultiplerestaurantsapp.EventBus.FoodListEvent;
import com.comunisolve.newmultiplerestaurantsapp.Interface.IOnRecyclerViewClickListner;
import com.comunisolve.newmultiplerestaurantsapp.Model.Category;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.comunisolve.newmultiplerestaurantsapp.FoodListActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder> {

    Context context;
    List<Category> categoryList;

    public MyCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Picasso.get().load(Common.API_RESTAURANT_ENDPOINT + categoryList.get(position).getImage()).into(holder.img_category);

        holder.txt_category.setText(categoryList.get(position).getName());
        holder.setLstner(new IOnRecyclerViewClickListner() {
            @Override
            public void onClick(View view, int position) {
                EventBus.getDefault().postSticky(new FoodListEvent(true, categoryList.get(position)));
                context.startActivity(new Intent(context, FoodListActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_category;
        TextView txt_category;

        IOnRecyclerViewClickListner lstner;

        public void setLstner(IOnRecyclerViewClickListner lstner) {
            this.lstner = lstner;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category = itemView.findViewById(R.id.img_category);
            txt_category = itemView.findViewById(R.id.txt_category);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            lstner.onClick(view, getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (categoryList.size() == 1)
            return Common.DEFAULT_COLUMN_COUNT;
        else {
            if (categoryList.size() % 2 == 0)
                return Common.DEFAULT_COLUMN_COUNT;
            else
                return (position > 1 && position == categoryList.size() - 1) ? Common.FULL_WIDTH_COLUMN : Common.DEFAULT_COLUMN_COUNT;
        }
    }
}
