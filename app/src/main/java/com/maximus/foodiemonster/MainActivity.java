package com.maximus.foodiemonster;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_cal_record, R.id.navigation_time, R.id.navigation_showla)
                .build();*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setItemIconTintList(null);

        FloatingActionButton menu_fab=findViewById(R.id.menu_fab);
        ImageView menu=findViewById(R.id.menu);
        AtomicReference<Boolean> tmp= new AtomicReference<>(true);
        menu_fab.setOnClickListener(view -> {
            if(tmp.get()){
                menu.setVisibility(View.VISIBLE);
                tmp.set(false);
            }else{
                menu.setVisibility(View.INVISIBLE);
                tmp.set(true);
            }
        });
        /*BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(com.google.android.material.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }*/
    }

}