package com.maximus.foodiemonster.ui.calrecord;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.MealData;
import com.maximus.foodiemonster.R;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class CalRecordFragment extends Fragment {
    private static final String TAG = "CalRecordFragment";
    private static final int TIME = 20210824;
    //private ArrayList<MealData> mealData= new ArrayList<MealData>();
    //private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cal_main, container, false);
        NavController navCtrl = findNavController(this);

        TextView cal_breakfast_text=root.findViewById(R.id.cal_breakfast_text);
        TextView cal_lunch_text=root.findViewById(R.id.cal_lunch_text);
        TextView cal_dinner_text=root.findViewById(R.id.cal_dinner_text);

        ((MainActivity) requireActivity()).clear_mealdata();
        ArrayList<MealData> tmp1=((MainActivity) requireActivity()).get_mealdata();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ArrayList<MealData> mealData=((MainActivity) requireActivity()).get_mealdata();
            Log.d(TAG, "Numbers of mealdata :"+ mealData.size());
            for (int i=0;i<mealData.size();i++){
                if(mealData.get(i).time%10==1)cal_breakfast_text.setText(mealData.get(i).totalcal +"大卡");
                else if(mealData.get(i).time%10==2)cal_lunch_text.setText(mealData.get(i).totalcal +"大卡");
                else if(mealData.get(i).time%10==3)cal_dinner_text.setText(mealData.get(i).totalcal +"大卡");
            }
        }, 1000);


        ImageView cal_breakfast=root.findViewById(R.id.cal_breakfast);
        cal_breakfast.setOnClickListener(view -> {
            CalRecordFragmentDirections.ActionNavigationCalMainToNavigationCalManual action=CalRecordFragmentDirections.actionNavigationCalMainToNavigationCalManual(1);
            navCtrl.navigate(action);
        });
        ImageView cal_lunch=root.findViewById(R.id.cal_lunch);
        cal_lunch.setOnClickListener(view -> {
            CalRecordFragmentDirections.ActionNavigationCalMainToNavigationCalManual action=CalRecordFragmentDirections.actionNavigationCalMainToNavigationCalManual(2);
            navCtrl.navigate(action);
        });
        ImageView cal_dinner=root.findViewById(R.id.cal_dinner);
        cal_dinner.setOnClickListener(view -> {
            /*CalRecordFragmentDirections.ActionNavigationCalMainToNavigationCalManual action=CalRecordFragmentDirections.actionNavigationCalMainToNavigationCalManual(3);
            navCtrl.navigate(action);*/
            /*MealData tmp=((MainActivity) requireActivity()).get_mealdata();
            try{
                cal_breakfast_text.setText(String.valueOf(tmp.totalcal));
                Log.d(TAG,  String.valueOf(tmp.totalcal));
            }catch (Exception e){
                e.printStackTrace();
            }*/

        });


        return root;
    }
}



