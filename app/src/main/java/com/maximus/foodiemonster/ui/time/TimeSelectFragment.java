package com.maximus.foodiemonster.ui.time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.R;
import com.maximus.foodiemonster.ui.calrecord.CalRecordFragmentDirections;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class TimeSelectFragment extends Fragment {


    //private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_time_select, container, false);
        NavController navCtrl = findNavController(this);
        ImageView time_1212=root.findViewById(R.id.time_1212);
        ImageView time_1410=root.findViewById(R.id.time_1410);
        ImageView time_168=root.findViewById(R.id.time_168);
        ImageView time_186=root.findViewById(R.id.time_186);
        time_1212.setOnClickListener(view ->{
            TimeSelectFragmentDirections.ActionNavigationTimeSelectToNavigationTimeSelcetAsk action=TimeSelectFragmentDirections.actionNavigationTimeSelectToNavigationTimeSelcetAsk(1);
            navCtrl.navigate(action);
        });
        time_1410.setOnClickListener(view ->{
            TimeSelectFragmentDirections.ActionNavigationTimeSelectToNavigationTimeSelcetAsk action=TimeSelectFragmentDirections.actionNavigationTimeSelectToNavigationTimeSelcetAsk(2);
            navCtrl.navigate(action);
        });
        time_168.setOnClickListener(view ->{
            TimeSelectFragmentDirections.ActionNavigationTimeSelectToNavigationTimeSelcetAsk action=TimeSelectFragmentDirections.actionNavigationTimeSelectToNavigationTimeSelcetAsk(3);
            navCtrl.navigate(action);
        });
        time_186.setOnClickListener(view ->{
            TimeSelectFragmentDirections.ActionNavigationTimeSelectToNavigationTimeSelcetAsk action=TimeSelectFragmentDirections.actionNavigationTimeSelectToNavigationTimeSelcetAsk(4);
            navCtrl.navigate(action);
        });

        return root;
    }
}



