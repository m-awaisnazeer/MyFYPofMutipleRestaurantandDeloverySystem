// Generated by view binder compiler. Do not edit!
package com.comunisolve.newmultiplerestaurantsapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.comunisolve.newmultiplerestaurantsapp.R;
import com.google.android.material.appbar.AppBarLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCardListBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final AppBarLayout appBar;

  @NonNull
  public final Button btnOrder;

  @NonNull
  public final LinearLayout layoutPrice;

  @NonNull
  public final RecyclerView recyclerCart;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final TextView txtFinalOrice;

  private ActivityCardListBinding(@NonNull RelativeLayout rootView, @NonNull AppBarLayout appBar,
      @NonNull Button btnOrder, @NonNull LinearLayout layoutPrice,
      @NonNull RecyclerView recyclerCart, @NonNull Toolbar toolbar,
      @NonNull TextView txtFinalOrice) {
    this.rootView = rootView;
    this.appBar = appBar;
    this.btnOrder = btnOrder;
    this.layoutPrice = layoutPrice;
    this.recyclerCart = recyclerCart;
    this.toolbar = toolbar;
    this.txtFinalOrice = txtFinalOrice;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCardListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCardListBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_card_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCardListBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.app_bar;
      AppBarLayout appBar = rootView.findViewById(id);
      if (appBar == null) {
        break missingId;
      }

      id = R.id.btn_order;
      Button btnOrder = rootView.findViewById(id);
      if (btnOrder == null) {
        break missingId;
      }

      id = R.id.layout_price;
      LinearLayout layoutPrice = rootView.findViewById(id);
      if (layoutPrice == null) {
        break missingId;
      }

      id = R.id.recycler_cart;
      RecyclerView recyclerCart = rootView.findViewById(id);
      if (recyclerCart == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = rootView.findViewById(id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.txt_final_orice;
      TextView txtFinalOrice = rootView.findViewById(id);
      if (txtFinalOrice == null) {
        break missingId;
      }

      return new ActivityCardListBinding((RelativeLayout) rootView, appBar, btnOrder, layoutPrice,
          recyclerCart, toolbar, txtFinalOrice);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
