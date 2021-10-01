package com.maximus.foodiemonster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //public UserData userdata;
    // Firebase instance variables
    private SharedPreferences mSharedPreferences;
    private GoogleSignInClient mSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private String foodCal;
    private int IFseleceted;
    private ArrayList<MealData> mealData= new ArrayList<MealData>();
    // [START declare_database_ref]
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // [END declare_database_ref]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize Firebase Auth and check if the user is signed in
        /*mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);*/

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
    public String read_cal(String text){
        DocumentReference docRef = db.collection("manualfoods").document(text);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> docMap=document.getData();
                        Log.d(TAG, "Map data: " + docMap);
                        foodCal=String.valueOf(docMap.get("cal"));
                        Log.d(TAG, "Test: " + foodCal);
                    } else {
                        Log.d(TAG, "No such document");
                        foodCal="查無此食物";
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return foodCal;
    }
    public void clear_mealdata(){
        mealData= new ArrayList<MealData>();
    }

    public ArrayList<MealData> get_mealdata(int time){
        DocumentReference docRef = db.collection("users").document("test1");
        CollectionReference dataRef=docRef.collection("data");
        //int TIME=Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()));
        int TIME=time;
        Log.d(TAG,"Current Date :"+TIME);
        Query dataQuery=dataRef.whereGreaterThan("time",TIME*10).whereLessThan("time",(TIME+1)*10).orderBy("time");
        dataQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> docMap=document.getData();
                                Log.d(TAG, "Map data: " + docMap);
                                foodCal=String.valueOf(docMap.get("foodlist"));
                                ArrayList<String> foodl=new ArrayList<String>((Collection<? extends String>) docMap.get("foodlist"));
                                Log.d(TAG, "Length: " + foodl.size());
                                Log.d(TAG, "ArrayList data: " + foodl);
                                MealData tmp=new MealData();
                                tmp.time=Integer.parseInt(String.valueOf(docMap.get("time")));
                                tmp.foodlist=new ArrayList<String>((Collection<? extends String>) docMap.get("foodlist"));
                                tmp.totalcal=Integer.parseInt(String.valueOf(docMap.get("totalcal")));
                                mealData.add(tmp);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return mealData;
    }
    public void save_mealdata(MealData data){
        //System.out.println(data);
        DocumentReference docRef = db.collection("users").document("test1");
        CollectionReference ColRef =docRef.collection("data");
        Log.d(TAG, "Mealdata time: "+data.time);
        Query query = ColRef.whereEqualTo("time", data.time);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "Query size: "+task.getResult().size());
                        if (task.isSuccessful()) {
                            if(task.getResult().size()==0){
                                docRef.collection("data").add(data);
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DocumentReference tmpRef=document.getReference();
                                    Log.d(TAG, "if true: "+data);
                                    Log.d(TAG, "Error getting documents: "+data);
                                    tmpRef.set(data);
                                }
                            }
                        } else {
                            Log.d(TAG, "if false: "+data);
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            docRef.collection("data").add(data);
                        }
                    }
                });
        query.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d(TAG, "Query get Failure: " + data);
                        docRef.collection("data").add(data);
                    }
                });
    }
    /*public void mealdata_refresh(TextView cal_breakfast_text,TextView cal_lunch_text,TextView cal_dinner_text,TextView cal_total_text,int TIME){
        clear_mealdata();
        DocumentReference docRef = db.collection("users").document("test1");
        CollectionReference dataRef=docRef.collection("data");
        Log.d(TAG,"Current Date :"+TIME);
        Query dataQuery=dataRef.whereGreaterThan("time",TIME*10).whereLessThan("time",(TIME+1)*10).orderBy("time");
        dataQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> docMap=document.getData();
                                Log.d(TAG, "Map data: " + docMap);
                                foodCal=String.valueOf(docMap.get("foodlist"));
                                ArrayList<String> foodl=new ArrayList<String>((Collection<? extends String>) docMap.get("foodlist"));
                                Log.d(TAG, "Length: " + foodl.size());
                                Log.d(TAG, "ArrayList data: " + foodl);
                                MealData tmp=new MealData();
                                tmp.time=Integer.parseInt(String.valueOf(docMap.get("time")));
                                tmp.foodlist=new ArrayList<String>((Collection<? extends String>) docMap.get("foodlist"));
                                tmp.totalcal=Integer.parseInt(String.valueOf(docMap.get("totalcal")));
                                mealData.add(tmp);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        int cal1=0,cal2=0,cal3=0,total_cal=0;
        Log.d(TAG, "Numbers of mealdata :"+ mealData.size());
        if(mealData.size()==0){
            cal_breakfast_text.setText("尚無資料");
            cal_lunch_text.setText("尚無資料");
            cal_dinner_text.setText("尚無資料");
            cal_total_text.setText("今日總熱量：0 大卡");
        }else{
            for (int i=0;i<mealData.size();i++){
                if(mealData.get(i).time%10==1){
                    cal_breakfast_text.setText(mealData.get(i).totalcal +"大卡");
                    cal1=mealData.get(i).totalcal;
                }
                else if(mealData.get(i).time%10==2){
                    cal_lunch_text.setText(mealData.get(i).totalcal +"大卡");
                    cal2=mealData.get(i).totalcal;
                }
                else if(mealData.get(i).time%10==3){
                    cal_dinner_text.setText(mealData.get(i).totalcal +"大卡");
                    cal3=mealData.get(i).totalcal;
                }
                total_cal=cal1+cal2+cal3;
            }
            if(cal_breakfast_text.getText().equals("讀取資料中")||cal_breakfast_text.getText().equals("0大卡")) cal_breakfast_text.setText("尚無資料");
            if(cal_lunch_text.getText().equals("讀取資料中")||cal_lunch_text.getText().equals("0大卡")) cal_lunch_text.setText("尚無資料");
            if(cal_dinner_text.getText().equals("讀取資料中")||cal_dinner_text.getText().equals("0大卡")) cal_dinner_text.setText("尚無資料");
            cal_total_text.setText("今日總熱量："+total_cal+" 大卡");
        }
    }*/
    public int get_IFselected(){
        DocumentReference docRef = db.collection("users").document("test1");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> docMap=document.getData();
                        Log.d(TAG, "Map data: " + docMap);
                        try{
                            IFseleceted=Integer.parseInt(String.valueOf(docMap.get("IFselected")));
                        }catch(Exception e){
                            e.printStackTrace();
                            IFseleceted=0;
                        }
                        Log.d(TAG, "IFseleceted: " + IFseleceted);
                    } else {
                        Log.d(TAG, "No such document");
                        IFseleceted=0;
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return IFseleceted;
    }

    public void save_IFselected(int IFseleceted){
        Map<String, Integer> IF = new HashMap<>();
        IF.put("IFselected",IFseleceted);
        //DocumentReference docRef = db.collection("users").document("test1");
        db.collection("users").document("test1")
                .set(IF)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}