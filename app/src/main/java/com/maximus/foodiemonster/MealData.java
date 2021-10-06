package com.maximus.foodiemonster;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

// [START rtdb_user_class]
@IgnoreExtraProperties
public class MealData {
    public int time;//e.g.,202108241->2021/08/24 breakfast 1->breakfast 2->lunch 3->dinner
    public int totalcal;
    public ArrayList<String> foodlist=new ArrayList<String>();
    public ArrayList<Integer> foodcallist=new ArrayList<Integer>();


    public MealData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public MealData(int time) {
        this.time = time;
    }
    //public UserData(String username, String email) {
    //public UserData(int headshot, String username, int gender, String city) {
    public MealData(int time,ArrayList<String> foodlist) {
        this.time = time;
        this.foodlist = foodlist;
    }
    public MealData(int time,int totalcal,ArrayList<String> foodlist) {
        this.time = time;
        this.totalcal = totalcal;
        this.foodlist = foodlist;
    }

    public MealData(int time,int totalcal,ArrayList<String> foodlist,ArrayList<Integer> foodcallist) {
        this.time = time;
        this.totalcal = totalcal;
        this.foodlist = foodlist;
        this.foodcallist = foodcallist;
    }
}
// [END rtdb_meal_data_class]