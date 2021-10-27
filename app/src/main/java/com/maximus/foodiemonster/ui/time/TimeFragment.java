package com.maximus.foodiemonster.ui.time;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.MealData;
import com.maximus.foodiemonster.R;
import com.maximus.foodiemonster.ui.calrecord.CalRecordFragmentDirections;

import java.util.ArrayList;
import java.util.Date;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class TimeFragment extends Fragment {

    private static final String TAG = "TimeFragment";
    int date,time;
    boolean btn_switch=false;
    int IFcount;
    //private ProfileViewModel profileViewModel;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_time_main, container, false);
        NavController navCtrl = findNavController(this);

        //int current_date=Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));
        //int current_time=Integer.parseInt(new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime()));
        int tmp1=((MainActivity) requireActivity()).get_IFselected();
        tmp1=((MainActivity) requireActivity()).get_IFdate();
        tmp1=((MainActivity) requireActivity()).get_IFtime();
        TextView IF_text=root.findViewById(R.id.IF_text);
        TextView time_text=root.findViewById(R.id.time_text);
        TextView cur_time=root.findViewById(R.id.current_time);

        cur_time.setText("目前時間\n"+new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        Handler handler=new Handler();
        Thread thread = new Thread(() -> {
            try {
                //while (!thread.isInterrupted()) {
                while (true) {
                    Thread.sleep(1000);
                    handler.post(()->{
                        cur_time.setText("目前時間\n"+new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    });
                }
            } catch (InterruptedException e) {
            }
        });

        thread.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int tmp2=((MainActivity) requireActivity()).get_IFselected();
            tmp2=((MainActivity) requireActivity()).get_IFdate();
            tmp2=((MainActivity) requireActivity()).get_IFtime();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                int selected=((MainActivity) requireActivity()).get_IFselected();
                date=((MainActivity) requireActivity()).get_IFdate();
                time=((MainActivity) requireActivity()).get_IFtime();
                //Log.d(TAG, "current time:"+current_date+current_time);
                Log.d(TAG, "IF time:"+date+time);
                if(selected==1){
                    IF_text.setText("目前選擇的斷食法為\n1212斷食法");
                    IFcount=12;
                }else if(selected==2){
                    IF_text.setText("目前選擇的斷食法為\n1410斷食法");
                    IFcount=14;
                }else if(selected==3){
                    IF_text.setText("目前選擇的斷食法為\n168斷食法");
                    IFcount=16;
                }else if(selected==4){
                    IF_text.setText("目前選擇的斷食法為\n186斷食法");
                    IFcount=18;
                }else{
                    IF_text.setText("尚未選取斷食法");
                    btn_switch=true;
                }
                if(date==0){
                    time_text.setText("尚未開始計時");
                    btn_switch=false;
                }else{
                    if(time/10000+IFcount>23){
                        time_text.setText("開始斷食時間：\n"+
                                String.format("%02d", date%10000/100)+"/"+
                                String.format("%02d", date%100)+" "+
                                String.format("%02d", time/10000)+":"+
                                String.format("%02d", time%10000/100)+":"+
                                String.format("%02d", time%100)+"\n"+
                                "下次進食時間：\n"+
                                String.format("%02d", date%10000/100)+"/"+
                                String.format("%02d", date%100+1)+" "+
                                String.format("%02d", time/10000+IFcount-24)+":"+
                                String.format("%02d", time%10000/100)+":"+
                                String.format("%02d", time%100));
                    }else{
                        time_text.setText("開始斷食時間：\n"+
                                String.format("%02d", date%10000/100)+"/"+
                                String.format("%02d", date%100)+" "+
                                String.format("%02d", time/10000)+":"+
                                String.format("%02d", time%10000/100)+":"+
                                String.format("%02d", time%100)+"\n"+
                                "下次進食時間：\n"+
                                String.format("%02d", date%10000/100)+"/"+
                                String.format("%02d", date%100)+" "+
                                String.format("%02d", time/10000+IFcount)+":"+
                                String.format("%02d", time%10000/100)+":"+
                                String.format("%02d", time%100));
                    }
                    btn_switch=true;
                }
            }, 100);
        }, 100);

        ImageView time_button=root.findViewById(R.id.time_button);
        time_button.setOnClickListener(view ->{
            if(btn_switch){
                navCtrl.navigate(R.id.action_navigation_time_to_navigation_time_select);
            }else{
                Date time1 = Calendar.getInstance().getTime();
                int current_date=Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(time1));
                int current_time=Integer.parseInt(new SimpleDateFormat("HHmmss").format(time1));
                ((MainActivity) requireActivity()).save_IFtime(current_date,current_time);
                time_text.setText("開始斷食時間：\n"+new SimpleDateFormat("MM/dd HH:mm:ss").format(time1));
            }
        });
        ImageView time_chart=root.findViewById(R.id.time_chart);
        time_chart.setOnClickListener(view ->{
            navCtrl.navigate(R.id.action_navigation_time_to_navigation_time_select);
        });
        return root;
    }
}



