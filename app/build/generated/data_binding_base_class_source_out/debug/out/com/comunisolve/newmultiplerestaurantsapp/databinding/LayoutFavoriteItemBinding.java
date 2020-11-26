// Generated by view binder compiler. Do not edit!
package com.comunisolve.newmultiplerestaurantsapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import com.comunisolve.newmultiplerestaurantsapp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutFavoriteItemBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView imgFood;

  @NonNull
  public final TextView txtFoodName;

  @NonNull
  public final TextView txtFoodPrice;

  @NonNull
  public final TextView txtRestaurantName;

  private LayoutFavoriteItemBinding(@NonNull CardView rootView, @NonNull ImageView imgFood,
      @NonNull TextView txtFoodName, @NonNull TextView txtFoodPrice,
      @NonNull TextView txtRestaurantName) {
    this.rootView = rootView;
    this.imgFood = imgFood;
    this.txtFoodName = txtFoodName;
    this.txtFoodPrice = txtFoodPrice;
    this.txtRestaurantName = txtRestaurantName;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutFavoriteItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutFavoriteItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_favorite_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutFavoriteItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.img_food;
      ImageView imgFood = rootView.findViewById(id);
      if (imgFood == null) {
        break missingId;
      }

      id = R.id.txt_food_name;
      TextView txtFoodName = rootView.findViewById(id);
      if (txtFoodName == null) {
        break missingId;
      }

      id = R.id.txt_food_price;
      TextView txtFoodPrice = rootView.findViewById(id);
      if (txtFoodPrice == null) {
        break missingId;
      }

      id = R.id.txt_restaurant_name;
      TextView txtRestaurantName = rootView.findViewById(id);
      if (txtRestaurantName == null) {
        break missingId;
      }

      return new LayoutFavoriteItemBinding((CardView) rootView, imgFood, txtFoodName, txtFoodPrice,
          txtRestaurantName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
