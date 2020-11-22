package com.comunisolve.newmultiplerestaurantsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.AddOnEventChange;
import com.comunisolve.newmultiplerestaurantsapp.Model.Addon;
import com.comunisolve.newmultiplerestaurantsapp.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyAddonAdapter extends RecyclerView.Adapter<MyAddonAdapter.ViewHolder> {

    List<Addon> addonList;
    Context context;

    public MyAddonAdapter(Context context, List<Addon> addonList) {
        this.addonList = addonList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_addon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.CheckBox.setText(new StringBuilder(addonList.get(position).getName())
                .append(" +(" + context.getString(R.string.money_sign))
                .append(addonList.get(position).getExtraPrice())
                .append(")"));

        holder.CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Common.addonList.add(addonList.get(position));
                   //EventBus.getDefault().postSticky(new AddOnEventChange(true,addonList.get(position)));
                    EventBus.getDefault().post(new AddOnEventChange(true,addonList.get(position)));


                } else {
                    Toast.makeText(context, "Remove", Toast.LENGTH_SHORT).show();
                    Common.addonList.remove(addonList.get(position));
                    //EventBus.getDefault().postSticky(new AddOnEventChange(false,addonList.get(position)));
                    EventBus.getDefault().postSticky(new AddOnEventChange(false,addonList.get(position)));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox CheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CheckBox = itemView.findViewById(R.id.ckb_addon);
        }
    }
}
