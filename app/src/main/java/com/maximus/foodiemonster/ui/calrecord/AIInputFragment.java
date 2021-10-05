package com.maximus.foodiemonster.ui.calrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
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
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.ListenableFuture;
import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.MealData;
import com.maximus.foodiemonster.R;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class AIInputFragment extends Fragment {

    private static final String TAG = "AIInputFragment";
    private int TIME;// = 202108240;
    ProcessCameraProvider cameraProvider;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    MealData mealData;
    String text="";
    int total_cal=0;
    int mealtype=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cal_ai, container, false);
        NavController navCtrl = findNavController(this);


        @NonNull int amount = AIInputFragmentArgs.fromBundle(getArguments()).getMealType();;
        TIME = AIInputFragmentArgs.fromBundle(getArguments()).getSelectTime();
        Log.d(TAG, String.valueOf(amount));
        mealtype=amount;
        mealData=new MealData(TIME*10+amount);

        root.findViewById(R.id.bottom_manual).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(AIInputFragmentDirections.actionNavigationCalAiToNavigationCalManual(mealtype,TIME));
        });

        root.findViewById(R.id.bottom_eatout).setOnClickListener(view -> {
            ((MainActivity)getActivity()).save_mealdata(mealData);
            navCtrl.navigate(AIInputFragmentDirections.actionNavigationCalAiToNavigationCalEatout(mealtype,TIME));
        });

        //camera preview
        PreviewView previewView=root.findViewById(R.id.previewView);
        startCamera(previewView);

        return root;

    }


    private void startCamera(PreviewView previewView){
        ListenableFuture cameraProviderFuture=ProcessCameraProvider.getInstance((MainActivity)getActivity());
        cameraProviderFuture.addListener(()->{
            // Used to bind the lifecycle of cameras to the lifecycle owner
            try {
                cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //初始化Preview
            // Preview
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            ImageCapture imageCapture = new ImageCapture.Builder().build();

            ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder().build();
            /*imageAnalyzer.setAnalyzer(cameraExecutor, new LuminosityAnalyzer ( luma ->
                    Log.d(TAG, "Average luminosity: $luma")
            ));*/
            imageAnalyzer.setAnalyzer(cameraExecutor, new LuminosityAnalyzer());


            // Select back camera as a default
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview);

            } catch(Exception e) {

                Log.e(TAG, "Use case binding failed", e);
            }

        }, ContextCompat.getMainExecutor((MainActivity)getActivity()));

    }

}



