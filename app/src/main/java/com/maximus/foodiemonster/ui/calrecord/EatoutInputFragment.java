package com.maximus.foodiemonster.ui.calrecord;

import android.app.AlertDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.android.material.textfield.TextInputEditText;
import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.MealData;
import com.maximus.foodiemonster.R;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class EatoutInputFragment extends Fragment {

    private static final String TAG = "EatoutInputFragment";
    private int TIME;// = 202108240;
    ArrayList<String> storelist=new ArrayList<>();
    ArrayList<String> storecsvlist=new ArrayList<>();
    ArrayList<String> foodname= new ArrayList<String>();
    ArrayList<String> foodcal=new ArrayList<String>();
    ArrayList<String> searchlist=new ArrayList<String>();
    ArrayList<String> searchedstore=new ArrayList<String>();
    ArrayList<String> searchedstorecsv=new ArrayList<String>();
    TextView textview;
    TextView total;

    MealData mealData;
    String text="";
    int total_cal=0;
    int mealtype=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //TIME=Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));
        //Log.d(TAG,"Current Date :"+TIME);

        View root = inflater.inflate(R.layout.fragment_cal_eatout, container, false);
        NavController navCtrl = findNavController(this);
        textview=root.findViewById(R.id.textView);
        total=root.findViewById(R.id.total);
        TextInputEditText textinput=root.findViewById(R.id.text_input);
        Button search_button = root.findViewById(R.id.search_button);
        Button clear_button=root.findViewById(R.id.clear_button);

        @NonNull int amount = EatoutInputFragmentArgs.fromBundle(getArguments()).getMealType();;
        TIME = ManualInputFragmentArgs.fromBundle(getArguments()).getSelectTime();
        Log.d(TAG, String.valueOf(amount));
        mealtype=amount;
        mealData=new MealData(TIME*10+amount);
        text="";

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.listofstore)));
            int i = 0;
            String[] nextLine;
            storelist.clear();
            storecsvlist.clear();
            while ((nextLine = reader.readNext()) != null) {
                Log.d(TAG, "nextLine :"+ nextLine);
                storelist.add(nextLine[0]);
                storecsvlist.add(nextLine[1]);
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
                        /*for(int k=0;k<foodname.size();k++){
                            if(foodname.get(k).equals(mealData.foodlist.get(j))){
                                String cal=foodcal.get(k+1);
                                text+=mealData.foodlist.get(j)+"："+cal+"大卡\n";
                                break;
                            }
                        }*/
                        //mealData.foodcallist.add(Integer.parseInt(cal));
                        text+=mealData.foodlist.get(j)+"："+mealData.foodcallist.get(j)+"大卡\n";

                    }
                    textview.setText(text);
                    break;
                }
            }
        }, 300);




        search_button.setOnClickListener(view -> {

            textinput.getText();
            searchedstore.clear();
            searchedstorecsv.clear();
            for(int i=0;i<storelist.size();i++)if(storelist.get(i).contains(String.valueOf(textinput.getText()))) {
                searchedstore.add(storelist.get(i));
                searchedstorecsv.add(storecsvlist.get(i));
            }
            for(int j=0;j<searchedstore.size();j++) Log.d("Input store", "searched : " + searchedstore.get(j)+",csv:"+searchedstorecsv.get(j));

            AlertDialog.Builder storealert = new AlertDialog.Builder(getActivity());
            storealert.setTitle("店名搜尋結果");
            String[] storetypes = new String[searchedstore.size()];
            String[] storecsvtypes = new String[searchedstorecsv.size()];
            for(int k=0;k<searchedstore.size();k++) {
                storetypes[k]=searchedstore.get(k);
            }
            storealert.setItems(storetypes, (dialog, which) -> {
                dialog.dismiss();
                try {
                    //CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.burgerking)));
                    CSVReader reader = new CSVReader(new InputStreamReader(
                            getContext().getAssets().open(searchedstorecsv.get(which)+".csv")
                    ));
                    int i = 0;
                    String[] nextLine;
                    foodname.clear();
                    foodcal.clear();
                    while ((nextLine = reader.readNext()) != null) {
                        Log.d(TAG, "nextLine :"+ nextLine);
                        foodname.add(nextLine[0]);
                        foodcal.add(nextLine[2]);
                        i++;
                    }
                    search_alert(searchedstore.get(which));
                } catch (IOException e) {
                    // reader在初始化時可能遭遇問題。記得使用try/catch處理例外情形。
                    e.printStackTrace();
                }


            });
            storealert.show();



            /*try {
                CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.burgerking)));
                int i = 0;
                String[] nextLine;
                foodname.clear();
                foodcal.clear();
                while ((nextLine = reader.readNext()) != null) {
                    Log.d(TAG, "nextLine :"+ nextLine);
                    foodname.add(nextLine[0]);
                    foodcal.add(nextLine[2]);
                    i++;
                }
            } catch (IOException e) {
                // reader在初始化時可能遭遇問題。記得使用try/catch處理例外情形。
                e.printStackTrace();
            }*/




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
            navCtrl.navigate(R.id.action_navigation_cal_eatout_to_navigation_cal_main);
        });

        root.findViewById(R.id.bottom_ai).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(EatoutInputFragmentDirections.actionNavigationCalEatoutToNavigationCalAi(mealtype,TIME));
        });

        root.findViewById(R.id.bottom_manual).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(EatoutInputFragmentDirections.actionNavigationCalEatoutToNavigationCalManual(mealtype,TIME));
        });
        return root;

    }

    public void search_alert(String storename){
        searchlist.clear();
        for(int i=0;i<foodname.size();i++) searchlist.add(foodname.get(i));

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(storename+"食品目錄");
        String[] types = new String[searchlist.size()];
        for(int k=0;k<searchlist.size();k++) {
            types[k]=searchlist.get(k);
        }
        alert.setItems(types, (dialog, which) -> {
            dialog.dismiss();
            for(int j=0;j<foodname.size();j++){
                if(foodname.get(j).equals(searchlist.get(which))){
                    String cal=foodcal.get(j);
                    text= (String) textview.getText();
                    text+=String.valueOf(searchlist.get(which))+"："+cal+"大卡\n";
                    mealData.foodlist.add(searchlist.get(which));
                    mealData.foodcallist.add(Integer.parseInt(cal));
                    textview.setText(text);
                    Log.d(TAG,"input string"+cal);
                    total_cal+=Integer.parseInt(cal.trim());
                    mealData.totalcal=total_cal;
                    total.setText("總計："+total_cal+"大卡");
                }
            }

        });
        alert.show();
    }
}



