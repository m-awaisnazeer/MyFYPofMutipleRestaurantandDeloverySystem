package com.comunisolve.newmultiplerestaurantsapp.ui.favorites;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyFavoriteAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Adapter.MyFoodAdapter;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.comunisolve.newmultiplerestaurantsapp.databinding.ActivityFoodListBinding;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FavoritesFragment extends Fragment {

    ActivityFoodListBinding binding;
    private static final String TAG = "FoodListActivity";
    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    MyFavoriteAdapter adapter;

    ImageView img_category;
    RecyclerView recyclerView_fav_list;

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        if (adapter != null) {
            adapter.onDestroy();
        }
        super.onDestroy();
    }


    private FavoritesViewModel mViewModel;

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_fragment, container, false);
        recyclerView_fav_list = view.findViewById(R.id.recyclerView_fav_list);

        init();
        initView();

        loadFavoriteItems();

        return view;
    }

    private void loadFavoriteItems() {
        dialog.show();

        compositeDisposable.add(myRestaurantAPI.getFavoriteByUser(Common.API_KEY,
                Common.currentUser.getFbid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteModel -> {

                            if (favoriteModel.isSuccess()) {
                                adapter = new MyFavoriteAdapter(requireContext(),favoriteModel.getResult());
                                recyclerView_fav_list.setAdapter(adapter);
                            } else {
                                Toast.makeText(requireContext(), "[GET FAV RESULT]" + favoriteModel.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                            dialog.dismiss();
                        },
                        throwable -> {
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "[GET FAV]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
    }


    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView_fav_list.setLayoutManager(linearLayoutManager);
        recyclerView_fav_list.addItemDecoration(new DividerItemDecoration(requireContext(), linearLayoutManager.getOrientation()));
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        // TODO: Use the ViewModel
    }

}