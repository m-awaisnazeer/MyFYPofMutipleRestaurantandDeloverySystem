// Generated by view binder compiler. Do not edit!
package com.comunisolve.newmultiplerestaurantsapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import com.comunisolve.newmultiplerestaurantsapp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityPlaceOrderBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final Button btnAddNewAddress;

  @NonNull
  public final Button btnProceed;

  @NonNull
  public final CheckBox ckbDefaultAddress;

  @NonNull
  public final EditText edtDate;

  @NonNull
  public final RadioButton rdiCod;

  @NonNull
  public final RadioButton rdiOnlinePayment;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final TextView txtNewAddress;

  @NonNull
  public final TextView txtTotalCash;

  @NonNull
  public final TextView txtUserAddress;

  @NonNull
  public final TextView txtUserPhone;

  private ActivityPlaceOrderBinding(@NonNull RelativeLayout rootView,
      @NonNull Button btnAddNewAddress, @NonNull Button btnProceed,
      @NonNull CheckBox ckbDefaultAddress, @NonNull EditText edtDate, @NonNull RadioButton rdiCod,
      @NonNull RadioButton rdiOnlinePayment, @NonNull Toolbar toolbar,
      @NonNull TextView txtNewAddress, @NonNull TextView txtTotalCash,
      @NonNull TextView txtUserAddress, @NonNull TextView txtUserPhone) {
    this.rootView = rootView;
    this.btnAddNewAddress = btnAddNewAddress;
    this.btnProceed = btnProceed;
    this.ckbDefaultAddress = ckbDefaultAddress;
    this.edtDate = edtDate;
    this.rdiCod = rdiCod;
    this.rdiOnlinePayment = rdiOnlinePayment;
    this.toolbar = toolbar;
    this.txtNewAddress = txtNewAddress;
    this.txtTotalCash = txtTotalCash;
    this.txtUserAddress = txtUserAddress;
    this.txtUserPhone = txtUserPhone;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityPlaceOrderBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityPlaceOrderBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_place_order, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityPlaceOrderBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_add_new_address;
      Button btnAddNewAddress = rootView.findViewById(id);
      if (btnAddNewAddress == null) {
        break missingId;
      }

      id = R.id.btn_proceed;
      Button btnProceed = rootView.findViewById(id);
      if (btnProceed == null) {
        break missingId;
      }

      id = R.id.ckb_default_address;
      CheckBox ckbDefaultAddress = rootView.findViewById(id);
      if (ckbDefaultAddress == null) {
        break missingId;
      }

      id = R.id.edt_date;
      EditText edtDate = rootView.findViewById(id);
      if (edtDate == null) {
        break missingId;
      }

      id = R.id.rdi_cod;
      RadioButton rdiCod = rootView.findViewById(id);
      if (rdiCod == null) {
        break missingId;
      }

      id = R.id.rdi_online_payment;
      RadioButton rdiOnlinePayment = rootView.findViewById(id);
      if (rdiOnlinePayment == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = rootView.findViewById(id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.txt_new_address;
      TextView txtNewAddress = rootView.findViewById(id);
      if (txtNewAddress == null) {
        break missingId;
      }

      id = R.id.txt_total_cash;
      TextView txtTotalCash = rootView.findViewById(id);
      if (txtTotalCash == null) {
        break missingId;
      }

      id = R.id.txt_user_address;
      TextView txtUserAddress = rootView.findViewById(id);
      if (txtUserAddress == null) {
        break missingId;
      }

      id = R.id.txt_user_phone;
      TextView txtUserPhone = rootView.findViewById(id);
      if (txtUserPhone == null) {
        break missingId;
      }

      return new ActivityPlaceOrderBinding((RelativeLayout) rootView, btnAddNewAddress, btnProceed,
          ckbDefaultAddress, edtDate, rdiCod, rdiOnlinePayment, toolbar, txtNewAddress,
          txtTotalCash, txtUserAddress, txtUserPhone);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
