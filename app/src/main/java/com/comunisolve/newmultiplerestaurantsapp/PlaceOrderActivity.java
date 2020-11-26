package com.comunisolve.newmultiplerestaurantsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.comunisolve.newmultiplerestaurantsapp.Common.Common;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartDatabase;
import com.comunisolve.newmultiplerestaurantsapp.Database.CartItem;
import com.comunisolve.newmultiplerestaurantsapp.Database.LocalCartDataSource;
import com.comunisolve.newmultiplerestaurantsapp.EventBus.SendTotalCashEvet;
import com.comunisolve.newmultiplerestaurantsapp.Model.BraintreeToken;
import com.comunisolve.newmultiplerestaurantsapp.Model.BraintreeTransaction;
import com.comunisolve.newmultiplerestaurantsapp.Model.CreateOrderModel;
import com.comunisolve.newmultiplerestaurantsapp.Model.UpdateOrderModel;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IBraintreeAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitBraintreeClient;
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient;
import com.comunisolve.newmultiplerestaurantsapp.databinding.ActivityPlaceOrderBinding;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PlaceOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_BRAINTREE_CODE = 777;
    ActivityPlaceOrderBinding binding;

    IMyRestaurantAPI myRestaurantAPI;
    IBraintreeAPI braintreeAPI;
    AlertDialog dialog;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    boolean isSelectedDate = false, isAddNewAddress = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initView();

    }

    private void initView() {

        binding.txtUserPhone.setText(Common.currentUser.getUserPhone());
        binding.txtUserAddress.setText(Common.currentUser.getAddress());

        binding.toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.btnAddNewAddress.setOnClickListener((View.OnClickListener) v -> {
            isAddNewAddress = true;
            binding.ckbDefaultAddress.setChecked(false);

            View layout_add_new_address = LayoutInflater.from(PlaceOrderActivity.this)
                    .inflate(R.layout.layout_add_new_address, null);

            EditText edt_add_new_address = (EditText) layout_add_new_address.findViewById(R.id.edt_ad_new_address);
            edt_add_new_address.setText(binding.txtNewAddress.getText().toString());

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaceOrderActivity.this)
                    .setTitle("Add New Address")
                    .setView(layout_add_new_address)
                    .setNegativeButton("CANCEL", ((dialog1, which) -> {
                                dialog1.dismiss();
                            })
                    ).setPositiveButton("Add", (((dialog1, which) -> {
                        binding.txtNewAddress.setText(edt_add_new_address.getText().toString());
                    })));

            androidx.appcompat.app.AlertDialog addNewDialog = builder.create();
            addNewDialog.show();

        });

        binding.edtDate.setOnClickListener(v -> {

            Calendar now = Calendar.getInstance();

            DatePickerDialog dpd = DatePickerDialog.newInstance(PlaceOrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));

            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });

        binding.btnProceed.setOnClickListener(v -> {

            if (!isSelectedDate) {
                Toast.makeText(this, "Please select Date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isAddNewAddress) {
                if (!binding.ckbDefaultAddress.isChecked()) {
                    Toast.makeText(this, "Please choose default Address or set new Address", Toast.LENGTH_SHORT).show();

                }
            }

            if (binding.rdiCod.isChecked()) {

                getOrderNumber(false);
            } else if (binding.rdiOnlinePayment.isChecked()) {

                getOrderNumber(true);

            }
        });
    }

    private void getOrderNumber(boolean isOnlinePayment) {
        dialog.show();
        if (!isOnlinePayment) {
            String address = binding.ckbDefaultAddress.isChecked() ? binding.txtUserAddress.getText().toString() : binding.txtNewAddress.getText().toString();

            compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid(),
                    Common.currentRestaurant.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cartItems -> {

                        compositeDisposable.add(myRestaurantAPI.createOrder(Common.API_KEY,
                                Common.currentUser.getFbid(),
                                Common.currentUser.getUserPhone(),
                                Common.currentUser.getName(),
                                address,
                                binding.edtDate.getText().toString(),
                                Common.currentRestaurant.getId(),
                                "NONE",
                                1,
                                Double.valueOf(binding.txtTotalCash.getText().toString()),
                                cartItems.size())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(createOrderModel -> {

                                            if (createOrderModel.isSuccess()) {
                                                //After have orderNumber , we will update all item of this order to orderDetails
                                                //First, Select Cart items

                                                compositeDisposable.add(myRestaurantAPI.updateOrder(Common.API_KEY,
                                                        String.valueOf(createOrderModel.getResult().get(0).getOrderNumber()),
                                                        new Gson().toJson(cartItems)
                                                        //""
                                                        )
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(updateOrderModel -> {

                                                            if (updateOrderModel.isSuccess()) {

                                                                //After updated item, we will clear cart and show message success

                                                                cartDataSource.cleanCart(Common.currentUser.getFbid(),
                                                                        Common.currentRestaurant.getId())
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                        .subscribe(new SingleObserver<Integer>() {
                                                                            @Override
                                                                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                                            }

                                                                            @Override
                                                                            public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                                                                Toast.makeText(PlaceOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                                Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                startActivity(homeActivity);
                                                                                finish();
                                                                            }

                                                                            @Override
                                                                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                                                Toast.makeText(PlaceOrderActivity.this, "[CLEAN CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });

                                                            }
                                                            if (dialog.isShowing())
                                                                dialog.dismiss();
                                                        }, throwable -> {
                                                            dialog.dismiss();
                                                            Toast.makeText(this, "[Update ORDER ]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                                        })
                                                );
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(this, "[CREATE ORDER Error]" + createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        throwable -> {
                                            dialog.dismiss();
                                            Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));
                    }, throwable -> {
                        Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }
        else {
            //if online payment

            compositeDisposable.add(braintreeAPI.getToken()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(braintreeToken -> {

                //if (braintreeToken.isSuccess()){

                    DropInRequest dropInRequest = new DropInRequest().clientToken(braintreeToken.getClientToken());
                    startActivityForResult(dropInRequest.getIntent(this),REQUEST_BRAINTREE_CODE);
//                }else {
//                    Toast.makeText(this, "Cant get Token", Toast.LENGTH_SHORT).show();
//                }
                dialog.dismiss();

            }, throwable -> {
                dialog.dismiss();
                Toast.makeText(this, "[GET TOKEN]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
            })
            );
        }
    }

    private void init() {
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDao());
        braintreeAPI = RetrofitBraintreeClient.getInstance(Common.API_RESTAURANT_Payment_ENDPOINT).create(IBraintreeAPI.class);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        isSelectedDate = true;

        binding.edtDate.setText(new StringBuilder("")
                .append(monthOfYear+1)
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BRAINTREE_CODE){
            if (resultCode == RESULT_OK)
            {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();

                if (!TextUtils.isEmpty(binding.txtTotalCash.toString())){
                    String amount = binding.txtTotalCash.getText().toString();

                    if (!dialog.isShowing()){
                        dialog.show();
                    }

                    String address = binding.ckbDefaultAddress.isChecked() ? binding.txtUserAddress.getText().toString(): binding.txtNewAddress.getText().toString();

                    compositeDisposable.add(braintreeAPI.submitPayment(amount,nonce.getNonce())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(braintreeTransaction -> {

                        if (braintreeTransaction.isSuccess()){

                            if (!dialog.isShowing())
                                dialog.show();
                            //After we have transaction , just make order like cod payment
                            compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getFbid(),
                                    Common.currentRestaurant.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(cartItems -> {

                                        compositeDisposable.add(myRestaurantAPI.createOrder(Common.API_KEY,
                                                Common.currentUser.getFbid(),
                                                Common.currentUser.getUserPhone(),
                                                Common.currentUser.getName(),
                                                address,
                                                binding.edtDate.getText().toString(),
                                                Common.currentRestaurant.getId(),
                                                braintreeTransaction.getTransaction().getId(),
                                                0,
                                                Double.valueOf(amount),
                                                cartItems.size())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(createOrderModel -> {

                                                            if (createOrderModel.isSuccess()) {
                                                                //After have orderNumber , we will update all item of this order to orderDetails
                                                                //First, Select Cart items

                                                                compositeDisposable.add(myRestaurantAPI.updateOrder(Common.API_KEY,
                                                                        String.valueOf(createOrderModel.getResult().get(0).getOrderNumber()),
                                                                        new Gson().toJson(cartItems)
                                                                        //""
                                                                        )
                                                                                .subscribeOn(Schedulers.io())
                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                .subscribe(updateOrderModel -> {

                                                                                    if (updateOrderModel.isSuccess()) {

                                                                                        //After updated item, we will clear cart and show message success

                                                                                        cartDataSource.cleanCart(Common.currentUser.getFbid(),
                                                                                                Common.currentRestaurant.getId())
                                                                                                .subscribeOn(Schedulers.io())
                                                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                                                .subscribe(new SingleObserver<Integer>() {
                                                                                                    @Override
                                                                                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                                                                                        Toast.makeText(PlaceOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                                                        Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                        startActivity(homeActivity);
                                                                                                        finish();
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                                                                        Toast.makeText(PlaceOrderActivity.this, "[CLEAN CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });

                                                                                    }
                                                                                    if (dialog.isShowing())
                                                                                        dialog.dismiss();
                                                                                }, throwable -> {
                                                                                    dialog.dismiss();
                                                                                    Toast.makeText(this, "[Update ORDER ]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                                                                })
                                                                );
                                                            } else {
                                                                dialog.dismiss();
                                                                Toast.makeText(this, "[CREATE ORDER Error]" + createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        },
                                                        throwable -> {
                                                            dialog.dismiss();
                                                            Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }));
                                    }, throwable -> {
                                        Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        }else {
                            Toast.makeText(this, "Transacion Failed", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }, throwable -> {

                        if (dialog.isShowing())
                            dialog.dismiss();
                        Toast.makeText(this, "[SUBMIT PAYMENT]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
                }
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setTotalCash(SendTotalCashEvet evet) {
        binding.txtTotalCash.setText(String.valueOf(evet.getCash()));
    }
}