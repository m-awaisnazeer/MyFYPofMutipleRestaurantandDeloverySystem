package com.comunisolve.newmultiplerestaurantsapp.ui.nearbyRestaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.LoadingDialogClosingEvent;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.MenuItemEvent;
import com.comunisolve.newmultiplerestaurantsapp.MenuActivity;
import com.comunisolve.newmultiplerestaurantsapp.Model.Restaurant;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class NearByRestaurantsFragment extends Fragment {

    NearByRestaurantsViewModel viewModel;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    AlertDialog dialog;
    GoogleMap mMap;
    Marker userMarker;

    boolean isFirstLoad = false;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            try {
                boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
                if (!success) {
                    Log.e("ERROR_MAP", "LOAD STYLE error");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("ERROR_MAP", "Resource not found");
            }

            mMap.setOnInfoWindowClickListener(marker -> {
                String id = marker.getTitle().substring(0, marker.getTitle().indexOf("."));

                if (!TextUtils.isEmpty(id)) {
                    viewModel.getRestaurantById(id).observe(getViewLifecycleOwner(), restaurantModel -> {
                        Toast.makeText(getContext(), "" + restaurantModel.getMessage(), Toast.LENGTH_SHORT).show();

                        if (restaurantModel.isSuccess()) {

                            Common.currentRestaurant = restaurantModel.getResult().get(0);
                            EventBus.getDefault().postSticky(new MenuItemEvent(true, Common.currentRestaurant));
                            startActivity(new Intent(requireContext(), MenuActivity.class));
                            requireActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "" + restaurantModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            if (currentLocation!=null)
                requestNearByRestaurant(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), 20);
        }
    };

    @Override
    public void onDestroy() {
        viewModel.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_by_restaurants, container, false);
        viewModel = new ViewModelProvider(this).get(NearByRestaurantsViewModel.class);

        init();
        initView();
        return view;
    }

    private void initView() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(requireContext()).build();
        buildLocationRequest();
        buildLocationCallBack();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                addMarkerAndMoveCamera(locationResult.getLastLocation());

                if (!isFirstLoad) {
                    isFirstLoad = !isFirstLoad;
                    Toast.makeText(getContext(), "restaurant", Toast.LENGTH_SHORT).show();

                    requestNearByRestaurant(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude(), 20);
                }
            }
        };
    }

    private void requestNearByRestaurant(double latitude, double longitude, int distance) {

        viewModel.getNearByRestaurants(latitude, longitude, distance).observe(getViewLifecycleOwner(), restaurantModel -> {

            if (restaurantModel.isSuccess()) {
                Toast.makeText(getContext(), ""+restaurantModel.getResult().size(), Toast.LENGTH_SHORT).show();
                addRestaurantMarker(restaurantModel.getResult());
            } else {
                Toast.makeText(getContext(), "" + restaurantModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();

        });
    }

    private void addRestaurantMarker(List<Restaurant> result) {
        Toast.makeText(getContext(), "add marker", Toast.LENGTH_SHORT).show();
        for (Restaurant restaurant : result) {
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker))
                    .position(new LatLng(restaurant.getLat(), restaurant.getLng()))
                    .snippet(restaurant.getAddress())
                    .title(new StringBuilder()
                            .append(restaurant.getId())
                            .append(".")
                            .append(restaurant.getName()).toString()));
        }
    }

    private void addMarkerAndMoveCamera(Location lastLocation) {

        if (userMarker != null)
            userMarker.remove();

        LatLng userLAtLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        userMarker = mMap.addMarker(new MarkerOptions().position(userLAtLng).title(Common.currentUser.getName()));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(userLAtLng, 17);

        mMap.animateCamera(yourLocation);


    }

    private void buildLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void DialogClos(LoadingDialogClosingEvent event) {
        if (event.isDialogclose()) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}