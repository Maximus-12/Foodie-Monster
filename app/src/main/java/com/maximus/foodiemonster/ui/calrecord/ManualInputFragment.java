package com.maximus.foodiemonster.ui.calrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class ManualInputFragment extends Fragment {

    private static final String TAG = "ManualInputFragment";
    private static final int TIME = 202108240;

    //private ProfileViewModel profileViewModel;
    //private ArrayList<MealData> mealData_online= new ArrayList<MealData>();

    ArrayList<String> foodname= new ArrayList<String>();
    ArrayList<String> searchlist=new ArrayList<String>();
    MealData mealData=new MealData(TIME);
    String text;
    int total_cal=0;
    int tmpvar;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cal_manual, container, false);
        NavController navCtrl = findNavController(this);
        TextView textview=root.findViewById(R.id.textView);
        TextView total=root.findViewById(R.id.total);
        TextInputEditText textinput=root.findViewById(R.id.text_input);
        Button search_button = root.findViewById(R.id.search_button);
        Button clear_button=root.findViewById(R.id.clear_button);

        @NonNull int amount = ManualInputFragmentArgs.fromBundle(getArguments()).getMealType();;
        Log.d(TAG, String.valueOf(amount));
        mealData.time+=amount;

        ((MainActivity) requireActivity()).clear_mealdata();
        ArrayList<MealData> tmp1=((MainActivity) requireActivity()).get_mealdata();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ArrayList<MealData> mealData_online=((MainActivity) requireActivity()).get_mealdata();
            Log.d(TAG, "Numbers of mealdata :"+ mealData_online.size());
            for (int i=0;i<mealData_online.size();i++){
                if(mealData_online.get(i).time%10==amount){
                    mealData=mealData_online.get(i);

                    total_cal=mealData.totalcal;
                    total.setText("總計："+total_cal+"大卡");
                    for(int j=0;j<mealData.foodlist.size();j++){
                        String tmp12=((MainActivity)getActivity()).read_cal(mealData.foodlist.get(j));
                        int finalJ = j;
                        new Handler(Looper.getMainLooper()).postDelayed(()-> {
                            String cal=((MainActivity)getActivity()).read_cal(mealData.foodlist.get(finalJ));
                            text+=mealData.foodlist.get(finalJ)+"："+cal+"大卡\n";
                        }, 1000);

                        /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tmpvar=j;
                                String cal=((MainActivity)getActivity()).read_cal(mealData.foodlist.get(j));
                                text+=mealData.foodlist.get(j)+"："+cal+"大卡\n";
                                textview.setText(text);
                            }
                        }, 100);*/
                    }
                    textview.setText(text);
                }
            }
        }, 1000);


        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.foodname)));
            int i = 0;
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                foodname.add(nextLine[0]);
                i++;
            }
        } catch (IOException e) {
            // reader在初始化時可能遭遇問題。記得使用try/catch處理例外情形。
            e.printStackTrace();
        }

        search_button.setOnClickListener(view -> {

            searchlist.clear();
            for(int i=0;i<1793;i++)if(foodname.get(i).contains(String.valueOf(textinput.getText()))) searchlist.add(foodname.get(i));
            for(int j=0;j<searchlist.size();j++) Log.d("Input", "searched : " + searchlist.get(j));

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("搜尋結果 (一份為100公克)");
            String[] types = new String[searchlist.size()];
            for(int k=0;k<searchlist.size();k++) types[k]=searchlist.get(k);
            alert.setItems(types, (dialog, which) -> {

                dialog.dismiss();
                //textview.setText(searchlist.get(which));
                String tmp12=((MainActivity)getActivity()).read_cal(searchlist.get(which));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    String cal=((MainActivity)getActivity()).read_cal(searchlist.get(which));
                    if(cal==null||cal=="查無此食物"){
                        Snackbar.make(view, "輸入有誤！", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        text= (String) textview.getText();
                        text+=String.valueOf(searchlist.get(which))+"："+cal+"大卡\n";
                        mealData.foodlist.add(searchlist.get(which));
                        textview.setText(text);
                        total_cal+=Integer.parseInt(cal);
                        mealData.totalcal=total_cal;
                        total.setText("總計："+total_cal+"大卡");
                    }
                }, 1000);
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
        });
        Button save_button=root.findViewById(R.id.save_button);
        save_button.setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(R.id.action_navigation_cal_manual_to_navigation_cal_main);
        });
        return root;

    }

}



