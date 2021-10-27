package com.maximus.foodiemonster.ui.time;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.R;
import com.maximus.foodiemonster.ui.calrecord.ManualInputFragmentArgs;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class TimeSelectAskFragment extends Fragment {


    //private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_time_select_ask, container, false);
        NavController navCtrl = findNavController(this);

        @NonNull int selected = TimeSelectAskFragmentArgs.fromBundle(getArguments()).getSelected();
        ImageView time_title=root.findViewById(R.id.time_title);
        TextView time_text=root.findViewById(R.id.time_text);
        if(selected==1){
            time_title.setImageResource(getResources().getIdentifier("time_1212", "drawable", ((MainActivity) requireActivity()).getPackageName()));
            time_text.setText("12:12 間歇性斷食適合：\n\n\n\n以往幾乎每天有消夜習慣者\n或剛初步進行減重者。");
        }else if(selected==2){
            time_title.setImageResource(getResources().getIdentifier("time_1410", "drawable", ((MainActivity) requireActivity()).getPackageName()));
            time_text.setText("14:10 間歇性斷食適合：\n\n\n\n想進一步規律自己飲食\n以達到減重效果者。");
        }else if(selected==3){
            time_title.setImageResource(getResources().getIdentifier("time_168", "drawable", ((MainActivity) requireActivity()).getPackageName()));
            time_text.setText("16:8 間歇性斷食適合：\n\n\n\n對於想要認真減重\n有一定毅力者。");
        }else if(selected==4){
            time_title.setImageResource(getResources().getIdentifier("time_186", "drawable", ((MainActivity) requireActivity()).getPackageName()));
            time_text.setText("18:6 間歇性斷食適合：\n\n\n\n對於自身的飢餓忍耐力\n及毅力有極大信心者。");
        }
        ImageView time_accept=root.findViewById(R.id.time_accept);
        time_accept.setOnClickListener(view->{
            //Toast.makeText(view.getContext(), "testing", Toast.LENGTH_LONG).show();
            ((MainActivity) requireActivity()).save_IFselected(selected);
            ((MainActivity) requireActivity()).save_IFtime(0,0);
            navCtrl.navigate(R.id.action_navigation_time_selcet_ask_to_navigation_time);
        });
        ImageView time_ignore=root.findViewById(R.id.time_ignore);
        time_ignore.setOnClickListener(view->{
            navCtrl.navigate(R.id.action_navigation_time_selcet_ask_to_navigation_time_select);
        });
        return root;
    }
}



