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
import com.maximus.foodiemonster.R;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class ManualInputFragment extends Fragment {

    //private ProfileViewModel profileViewModel;
    ArrayList<String> foodname= new ArrayList<String>();
    ArrayList<String> searchlist=new ArrayList<String>();
    String text;
    int total_cal=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cal_manual, container, false);
        NavController navCtrl = findNavController(this);
        TextView textview=root.findViewById(R.id.textView);
        TextView total=root.findViewById(R.id.total);
        TextInputEditText textinput=root.findViewById(R.id.text_input);
        Button search_button = root.findViewById(R.id.search_button);
        Button clear=root.findViewById(R.id.clear);


        try {
            /*Context context = InstrumentationRegistry.getTargetContext();
            CSVReader reader = new CSVReader(new InputStreamReader(
                    context.getAssets().open("myfile.csv")
            ));*/
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.foodname)));
            int i = 0;
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                foodname.add(nextLine[0]);
                //System.out.println("Reader read file content- Line " + i + ": " + nextLine[0] + "," + "etc...");
                i++;
            }
        } catch (IOException e) {
            // reader在初始化時可能遭遇問題。記得使用try/catch處理例外情形。
            e.printStackTrace();
        }

        search_button.setOnClickListener(view -> {
            /*String tmp= String.valueOf(textinput.getText());
            if(tmp.isEmpty())Snackbar.make(view, "輸入不能為空！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            else{
                String tmp1=((MainActivity)getActivity()).read_cal(String.valueOf(textinput.getText()));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    String cal=((MainActivity)getActivity()).read_cal(String.valueOf(textinput.getText()));
                    if(cal==null||cal=="查無此食物"){
                        Snackbar.make(view, "輸入有誤！", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        text= (String) textview.getText();
                        text+=String.valueOf(textinput.getText())+"："+cal+"大卡\n";
                        textview.setText(text);
                    }
                }, 1000);

            }*/
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
                String tmp1=((MainActivity)getActivity()).read_cal(searchlist.get(which));
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    String cal=((MainActivity)getActivity()).read_cal(searchlist.get(which));
                    if(cal==null||cal=="查無此食物"){
                        Snackbar.make(view, "輸入有誤！", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        text= (String) textview.getText();
                        text+=String.valueOf(searchlist.get(which))+"："+cal+"大卡\n";
                        textview.setText(text);
                        total_cal+=Integer.parseInt(cal);
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
        clear.setOnClickListener(view -> {
            textview.setText(" ");
            total_cal=0;
            total.setText("總計："+total_cal+"大卡");
        });
        return root;

    }
    void test(){
        Snackbar.make(getView(), "輸入不能為空！", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    /*

    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Example");
        String[] types = {"By Zip", "By Category"};
        b.setItems(types, (dialog, which) -> {

            dialog.dismiss();
            switch(which){
                case 0:
                    //onZipRequested();
                    test();
                    break;
                case 1:
                    //onCategoryRequested();
                    break;
            }
        });
        b.show();

     */
}



