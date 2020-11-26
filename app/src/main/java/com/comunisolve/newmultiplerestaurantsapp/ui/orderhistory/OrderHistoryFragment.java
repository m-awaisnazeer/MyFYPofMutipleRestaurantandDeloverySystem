package com.comunisolve.newmultiplerestaurantsapp.ui.orderhistory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyOrderAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Model.OrderModel;
import com.comunisolve.newmultiplerestaurantsapp.R;

import dmax.dialog.SpotsDialog;

public class OrderHistoryFragment extends Fragment {

    private OrderHistoryViewModel orderHistoryViewModel;
    AlertDialog dialog;
    RecyclerView recyclerView_order;

    @Override
    public void onDestroy() {
        super.onDestroy();
        orderHistoryViewModel.onDestroy();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderHistoryViewModel =
                new ViewModelProvider(this).get(OrderHistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_order_history, container, false);
        initView(root);

        dialog.show();
        orderHistoryViewModel.getOrderHistory().observe(getViewLifecycleOwner(), orderModel -> {
            if (orderModel.isSuccess()){
                dialog.dismiss();
                if (orderModel.getResult().size()>0){

                    MyOrderAdapter adapter = new MyOrderAdapter(requireContext(),orderModel.getResult());
                    recyclerView_order.setAdapter(adapter);

                }
            }else {
                dialog.dismiss();
            }
        });

        return root;
    }

    private void initView(View root) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView_order = root.findViewById(R.id.recyclerView_order);
        recyclerView_order.setLayoutManager(linearLayoutManager);
        recyclerView_order.addItemDecoration(new DividerItemDecoration(requireContext(),linearLayoutManager.getOrientation()));
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(requireContext()).build();
    }
}