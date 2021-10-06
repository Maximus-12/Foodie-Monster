package com.maximus.foodiemonster.ui.showla;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.maximus.foodiemonster.R;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class ShowLaShopFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_showla_shop, container, false);
        NavController navCtrl = findNavController(this);

        ImageView shop=root.findViewById(R.id.showla_shop_list);
        shop.setOnClickListener(view->{
            navCtrl.navigate(R.id.navigation_showla_main);
        });
        return root;
    }
}



