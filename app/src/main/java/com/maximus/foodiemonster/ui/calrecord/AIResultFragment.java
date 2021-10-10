package com.maximus.foodiemonster.ui.calrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.MealData;
import com.maximus.foodiemonster.R;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class AIResultFragment extends Fragment {

    private static final String TAG = "AIResultFragment";
    private int TIME;// = 202108240;

    ArrayList<String> foodname= new ArrayList<String>();
    ArrayList<String> foodcal=new ArrayList<String>();
    MealData mealData;
    String text="";
    int total_cal=0;
    int mealtype=0;
    String[] detectedfoods;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cal_ai_result, container, false);
        NavController navCtrl = findNavController(this);
        TextView textview=root.findViewById(R.id.textView);
        TextView total=root.findViewById(R.id.total);
        Button save_button = root.findViewById(R.id.save_button);
        Button clear_button=root.findViewById(R.id.clear_button);

        @NonNull int amount = AIResultFragmentArgs.fromBundle(getArguments()).getMealType();;
        TIME = AIResultFragmentArgs.fromBundle(getArguments()).getSelectTime();
        Log.d(TAG, String.valueOf(amount));
        mealtype=amount;
        //mealData=new MealData(TIME*10+amount);
        text="";

        ((MainActivity) requireActivity()).clear_mealdata();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ArrayList<MealData> tmp2=((MainActivity) requireActivity()).get_mealdata(TIME);
        }, 100);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ArrayList<MealData> tmp2=((MainActivity) requireActivity()).get_mealdata(TIME);
        }, 300);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ArrayList<MealData> mealData_online=((MainActivity) requireActivity()).get_mealdata(TIME);
            Log.d(TAG, "Numbers of mealdata :"+ mealData_online.size());
            Log.d(TAG, "Numbers of mealdata :"+ mealData_online);
            for (int i=0;i<mealData_online.size();i++){
                if(mealData_online.get(i).time%10==amount){
                    mealData=mealData_online.get(i);
                    total_cal=mealData.totalcal;
                    total.setText("總計："+total_cal+"大卡");
                    for(int j=0;j<mealData.foodlist.size();j++){
                        text+=mealData.foodlist.get(j)+"："+mealData.foodcallist.get(j)+"大卡\n";
                    }
                    textview.setText(text);
                    break;
                }
            }
        }, 500);

        clear_button.setOnClickListener(view -> {
            textview.setText(" ");
            total_cal=0;
            total.setText("總計："+total_cal+"大卡");
            mealData=new MealData(TIME*10+amount);
        });
        save_button.setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(R.id.action_navigation_cal_ai_result_to_navigation_cal_main);
        });

        root.findViewById(R.id.bottom_manual).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(AIResultFragmentDirections.actionNavigationCalAiResultToNavigationCalManual(mealtype,TIME));
        });

        root.findViewById(R.id.bottom_eatout).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(AIResultFragmentDirections.actionNavigationCalAiResultToNavigationCalEatout(mealtype,TIME));
        });

        return root;

    }


}



