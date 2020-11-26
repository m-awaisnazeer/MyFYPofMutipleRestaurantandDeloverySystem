package com.comunisolve.newmultiplerestaurantsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Model.Order;
import com.comunisolve.newmultiplerestaurantsapp.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    Context context;
    List<Order> orders;
    SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        simpleDateFormat = new SimpleDateFormat("MM/DD/YYYY");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order orderItem = orders.get(position);

        holder.txt_order_number.setText("Order Number :#"+String.valueOf(orderItem.getOrderId()));
        holder.txt_order_processing.setText(Common.convertStatusToString(orderItem.getOrderStatus()));
        holder.txt_order_phone.setText(orderItem.getOrderPhone());
        holder.txt_order_address.setText(orderItem.getOrderAddress());
        holder.txt_order_date.setText(simpleDateFormat.format(orderItem.getOrderDate()));
        if (orderItem.getTotalPrice()!=null) {
        holder.txt_order_total_price.setText(context.getString(R.string.money_sign)+orderItem.getTotalPrice().toString());
        }
        holder.txt_num_of_item.setText("Num of Items:  " + orderItem.getNumOfItem());

        if (orderItem.isCod())
            holder.txt_payment_method.setText(new StringBuilder("Cash On Delivery"));
        else
            holder.txt_payment_method.setText(new StringBuilder("TransID:").append(orderItem.getTransactionId()));


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_order_number, txt_order_processing, txt_order_phone, txt_order_address, txt_payment_method, txt_order_date, txt_order_total_price, txt_num_of_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_order_number = itemView.findViewById(R.id.txt_order_number);
            txt_order_processing = itemView.findViewById(R.id.txt_order_processing);
            txt_order_phone = itemView.findViewById(R.id.txt_order_phone);
            txt_order_address = itemView.findViewById(R.id.txt_order_address);
            txt_payment_method = itemView.findViewById(R.id.txt_payment_method);
            txt_order_date = itemView.findViewById(R.id.txt_order_date);
            txt_order_total_price = itemView.findViewById(R.id.txt_order_total_price);
            txt_num_of_item = itemView.findViewById(R.id.txt_num_of_item);

        }
    }
}
