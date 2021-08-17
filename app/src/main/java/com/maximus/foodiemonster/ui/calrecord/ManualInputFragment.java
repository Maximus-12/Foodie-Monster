package com.maximus.foodiemonster.ui.calrecord;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.android.material.textfield.TextInputEditText;
import com.maximus.foodiemonster.R;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class ManualInputFragment extends Fragment {

    //private ProfileViewModel profileViewModel;
    ArrayList<String> foodname= new ArrayList<String>();
    String text;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cal_manual, container, false);
        NavController navCtrl = findNavController(this);
        TextView textview=root.findViewById(R.id.textView);
        TextView total=root.findViewById(R.id.total);
        TextInputEditText textinput=root.findViewById(R.id.text_input);
        Button button = root.findViewById(R.id.button);
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

        button.setOnClickListener(view -> {
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
            try{
                textview.setText(foodname.get(Integer.parseInt(String.valueOf(textinput.getText()))));//0~1792
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        clear.setOnClickListener(view -> {
            textview.setText(" ");
        });
        return root;
    }
}



