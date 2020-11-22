package com.comunisolve.newmultiplerestaurantsapp.ui.updateInformation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.comunisolve.newmultiplerestaurantsapp.R;

public class UpdateInfoFragment extends Fragment {

    private UpdateInfoViewModel updateInfoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        updateInfoViewModel =
                new ViewModelProvider(this).get(UpdateInfoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_update_information, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        updateInfoViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}