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

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class ManualInputFragment extends Fragment {

    private static final String TAG = "ManualInputFragment";
    private int TIME;// = 202108240;

    ArrayList<String> foodname= new ArrayList<String>();
    ArrayList<String> foodcal=new ArrayList<String>();
    ArrayList<String> searchlist=new ArrayList<String>();
    MealData mealData;
    String text="";
    int total_cal=0;
    int mealtype=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TIME=Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));
        Log.d(TAG,"Current Date :"+TIME);

        View root = inflater.inflate(R.layout.fragment_cal_manual, container, false);
        NavController navCtrl = findNavController(this);
        TextView textview=root.findViewById(R.id.textView);
        TextView total=root.findViewById(R.id.total);
        TextInputEditText textinput=root.findViewById(R.id.text_input);
        Button search_button = root.findViewById(R.id.search_button);
        Button clear_button=root.findViewById(R.id.clear_button);

        @NonNull int amount = ManualInputFragmentArgs.fromBundle(getArguments()).getMealType();;
        Log.d(TAG, String.valueOf(amount));
        mealtype=amount;
        mealData=new MealData(TIME*10+amount);
        text="";
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.foodname)));
            int i = 0;
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                foodname.add(nextLine[0]);
                i++;
            }
        } catch (IOException e) {
            // reader初始化時可能。使用try/catch處理例外情形。
            e.printStackTrace();
        }

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.foodcal)));
            int i = 0;
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                foodcal.add(nextLine[0]);
                i++;
            }
        } catch (IOException e) {
            // reader在初始化時可能遭遇問題。記得使用try/catch處理例外情形。
            e.printStackTrace();
        }
        ((MainActivity) requireActivity()).clear_mealdata();
        ArrayList<MealData> tmp2=((MainActivity) requireActivity()).get_mealdata(TIME);
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
                        //Double cal=Double.parseDouble(foodcal.get(j));
                        for(int k=0;k<foodname.size();k++){
                            if(foodname.get(k).equals(mealData.foodlist.get(j))){
                                String cal=foodcal.get(k+1);
                                text+=mealData.foodlist.get(j)+"："+cal+"大卡\n";
                                break;
                            }
                        }

                    }
                    textview.setText(text);
                }
            }
        }, 300);




        search_button.setOnClickListener(view -> {

            searchlist.clear();
            for(int i=0;i<1793;i++)if(foodname.get(i).contains(String.valueOf(textinput.getText()))) searchlist.add(foodname.get(i));
            for(int j=0;j<searchlist.size();j++) Log.d("Input", "searched : " + searchlist.get(j));

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("搜尋結果 (一份為100公克)");
            String[] types = new String[searchlist.size()];
            for(int k=0;k<searchlist.size();k++) {
                types[k]=searchlist.get(k);
            }
            alert.setItems(types, (dialog, which) -> {
                dialog.dismiss();
                for(int j=0;j<foodname.size();j++){
                    if(foodname.get(j).equals(searchlist.get(which))){
                        String cal=foodcal.get(j+1);
                        text= (String) textview.getText();
                        text+=String.valueOf(searchlist.get(which))+"："+cal+"大卡\n";
                        mealData.foodlist.add(searchlist.get(which));
                        textview.setText(text);
                        Log.d(TAG,"input string"+cal);
                        total_cal+=Integer.parseInt(cal.trim());
                        mealData.totalcal=total_cal;
                        total.setText("總計："+total_cal+"大卡");
                    }
                }

            });
            alert.show();


            /*try{
                textview.setText(foodname.get(Integer.parseInt(String.valueOf(textinput.getText()))));//0~1792
            } catch (Exception e){
                e.printStackTrace();
            }*/
        });
        clear_button.setOnClickListener(view -> {
            textview.setText(" ");
            total_cal=0;
            total.setText("總計："+total_cal+"大卡");
            mealData=new MealData(TIME*10+amount);
        });
        Button save_button=root.findViewById(R.id.save_button);
        save_button.setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(R.id.action_navigation_cal_manual_to_navigation_cal_main);
        });

        /*root.findViewById(R.id.bottom_ai).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(ManualInputFragmentDirections.actionNavigationCalManualToNavigationCalAi(mealtype));
        });*/

        root.findViewById(R.id.bottom_eatout).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(ManualInputFragmentDirections.actionNavigationCalManualToNavigationCalEatout(mealtype,TIME));
        });

        return root;

    }


}



