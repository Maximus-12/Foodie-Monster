package com.maximus.foodiemonster.ui.time;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.MealData;
import com.maximus.foodiemonster.R;
import com.maximus.foodiemonster.ui.calrecord.CalRecordFragmentDirections;

import java.util.ArrayList;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class TimeFragment extends Fragment {


    //private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_time_main, container, false);
        NavController navCtrl = findNavController(this);

        /*ImageButton btn1 = root.findViewById(R.id.imageButton1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navCtrl.navigate(R.id.nav_mailpaper_choose);
            }
        });*/

        /*ImageButton btn2 = root.findViewById(R.id.imageButton2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "測試用", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                navCtrl.navigate(R.id.nav_mail);
            }
        });*/
        int tmp1=((MainActivity) requireActivity()).get_IFselected();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int tmp2=((MainActivity) requireActivity()).get_IFselected();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                int selected=((MainActivity) requireActivity()).get_IFselected();
                TextView time_clock_text=root.findViewById(R.id.time_clock_text);
                if(selected==1){
                    time_clock_text.setText("1212");
                }else if(selected==2){
                    time_clock_text.setText("1410");
                }else if(selected==3){
                    time_clock_text.setText("168");
                }else if(selected==4){
                    time_clock_text.setText("186");
                }else{
                    time_clock_text.setText("尚未選取斷食法");
                }
                }, 100);
            }, 100);

        ImageView time_chart=root.findViewById(R.id.time_chart);
        time_chart.setOnClickListener(view ->{
            navCtrl.navigate(R.id.action_navigation_time_to_navigation_time_select);
        });
        return root;
    }
}



