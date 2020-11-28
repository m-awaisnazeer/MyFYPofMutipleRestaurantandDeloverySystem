package com.comunisolve.newmultiplerestaurantsapp.ui.orderhistory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyOrderAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Interface.ILoadMore;
import com.comunisolve.newmultiplerestaurantsapp.Model.Order;
import com.comunisolve.newmultiplerestaurantsapp.R;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OrderHistoryFragment extends Fragment implements ILoadMore {

    private OrderHistoryViewModel orderHistoryViewModel;
    AlertDialog dialog;
    RecyclerView recyclerView_order;

    MyOrderAdapter adapter;
    List<Order> orderList;

    LayoutAnimationController layoutAnimationController;


    int maxData = 0;

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
        //getAllOrder();
        getMaxOrder();


        return root;
    }

    private void getMaxOrder() {
        dialog.show();
        orderHistoryViewModel.getMaxNumberOrder().observe(getViewLifecycleOwner(), orderModel -> {
            if (orderModel.isSuccess()) {

                orderHistoryViewModel.getMaxNumberOrder().observe(getViewLifecycleOwner(), maxOrderModel -> {
                    if (maxOrderModel.isSuccess()) {
                        maxData = maxOrderModel.getResult().get(0).getMaxRowNum();
                        dialog.dismiss();


                        getAllOrder(0, 10);
                    }
                });
            } else {
                dialog.dismiss();
            }
        });
    }

    private void getAllOrder(int from, int to) {
        dialog.show();
        orderHistoryViewModel.getOrderHistory(from, to).observe(getViewLifecycleOwner(), orderModel -> {
            if (orderModel.isSuccess()) {
                dialog.dismiss();
                if (orderModel.getResult().size() > 0) {

                    if (adapter == null) {
                        orderList = new ArrayList<>();
                        orderList = (orderModel.getResult());
                        adapter = new MyOrderAdapter(requireContext(), orderList, recyclerView_order);
                        adapter.setiLoadMore(this);
                        recyclerView_order.setAdapter(adapter);
                        recyclerView_order.setLayoutAnimation(layoutAnimationController);

                    } else {
                        orderList.remove(orderList.size() - 1); // here we will remove null item after loiad more
                        // if you don't remove ot, loading view still available
                        orderList = orderModel.getResult();
                        adapter.addItem(orderList);
                    }
                }
            } else {
                dialog.dismiss();
            }
        });
    }

    private void initView(View root) {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_item_from_left);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView_order = root.findViewById(R.id.recyclerView_order);
        recyclerView_order.setLayoutManager(linearLayoutManager);
        recyclerView_order.addItemDecoration(new DividerItemDecoration(requireContext(), linearLayoutManager.getOrientation()));
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(requireContext()).build();
    }

    @Override
    public void OnLoadMore() {
//When loadMore being call
        //First, we will check data count with max data
        if (adapter.getItemCount() < maxData) {

            //Add null object to List to tell adapter known show loading state
            orderList.add(null);
            adapter.notifyItemInserted(orderList.size() - 1);

            getAllOrder(adapter.getItemCount() + 1, adapter.getItemCount() + 10);

            adapter.notifyDataSetChanged();
            adapter.setLoaded();

        } else {
            Toast.makeText(requireContext(), "Max Data to load", Toast.LENGTH_SHORT).show();
        }
    }
}