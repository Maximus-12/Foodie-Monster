package com.maximus.foodiemonster.ui.calrecord;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.ListenableFuture;
import com.maximus.foodiemonster.MainActivity;
import com.maximus.foodiemonster.MealData;
import com.maximus.foodiemonster.R;
import com.opencsv.CSVReader;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class AIInputFragment extends Fragment {

    private static final String TAG = "AIInputFragment";
    private int TIME;// = 202108240;
    ProcessCameraProvider cameraProvider;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    ImageCapture imageCapture;
    MealData mealData;
    String text="";
    int total_cal=0;
    int mealtype=0;
    PreviewView mPreviewView;
    ImageView captureImage,imageView;
    private Handler handler;
    Bitmap bitmaptmp;
    private Float MAX_FONT_SIZE = 96F;

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
        outputDirectory = getActivity().getExternalFilesDir("camera");
        mPreviewView=root.findViewById(R.id.previewView);
        captureImage=root.findViewById(R.id.capture_button);
        imageView=root.findViewById(R.id.imagePreview);
        handler=new Handler();
        //startCamera(previewView,imageView);
        /*ImageButton btn=root.findViewById(R.id.capture_button);
        btn.setOnClickListener(view->{
            takePicture(imageView);
        });*/
        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        return root;

    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(getActivity(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                //getActivity().finish();
            }
        }
    }
    private Executor executor = Executors.newSingleThreadExecutor();

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        //HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        //if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
        //    hdrImageCaptureExtender.enableExtension(cameraSelector);
        //}

        final ImageCapture imageCapture = builder
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        //preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);



        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick: " );
                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                //File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
                File file = new File(getContext().getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        //Toast.makeText(getActivity(), "Image Saved successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onImageSaved: " );
                        Uri savedUri = Uri.fromFile(file);
                        //Bitmap bitmap = null;
                        try {
                            bitmaptmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), savedUri);
                            Log.d(TAG, "onBitmapSaved: " );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //翻轉圖片
                        if (bitmaptmp.getWidth() > bitmaptmp.getHeight()) {
                            Matrix matrix = new Matrix();
                            matrix.setRotate(90f);
                            bitmaptmp = Bitmap.createBitmap(bitmaptmp, 0, 0, bitmaptmp.getWidth(), bitmaptmp.getHeight(), matrix, true);
                        }
                        /*imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setOnClickListener(view->{
                            imageView.setVisibility(View.GONE);
                        });*/
                        handler.post(()->{
                            imageView.setImageBitmap(bitmaptmp);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setOnClickListener(view->{
                                imageView.setVisibility(View.GONE);
                            });
                            try {
                                Log.d(TAG, "tryObjectDetection: " );
                                runObjectDetection();
                            } catch (IOException e) {
                                Log.d(TAG, "ObjectDetectionerror: " );
                                e.printStackTrace();
                            }
                        });
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        Log.d(TAG, "onerr: " );
                        error.printStackTrace();
                    }
                });
            }
        });
    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

    private void runObjectDetection() throws IOException {
        // Step 1: 創建圖像物件
        TensorImage image = TensorImage.fromBitmap(bitmaptmp);
        Log.d(TAG, "TensorImage image: " );
        // Step 2: 初始化檢測器
        ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(5)
                .setScoreThreshold(0.3f)
                .build();
        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(
                getActivity(),
                "models.tflite",
                options
        );
        Log.d(TAG, "ObjectDetector.create: " );

        // Step 3: 將圖像饋送到檢測器
        List<Detection> results = detector.detect(image);
        Log.d(TAG, "Results: "+results);
        // Step 4: 檢測結果
        List<DetectionResult> resultToDisplay=new ArrayList<DetectionResult>();
        for(int i=0;i<results.size();i++){
            Log.d(TAG, "Results"+i+"："+results.get(i));
            Detection tmp=results.get(i);
            Log.d(TAG, "Results BoundingBox"+i+"："+tmp.getBoundingBox());
            Log.d(TAG, "Results getCategories"+i+"："+tmp.getCategories());
            Log.d(TAG, "Results getCategories.get0"+i+"："+tmp.getCategories().get(0));
            //tmp.getCategories().get(0).getScore()
            resultToDisplay.add(new DetectionResult(tmp.getBoundingBox(),tmp.getCategories().get(0).getLabel()+tmp.getCategories().get(0).getScore()*100+"%"));
        }
        /*List<DetectionResult> resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            val text = "${category.label}, ${category.score.times(100).toInt()}%"

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, text)
        }*/
        // Draw the detection result on the bitmap and show it.
        Bitmap imgWithResult = drawDetectionResult(bitmaptmp, resultToDisplay);
        handler.post(()->{
            imageView.setImageBitmap(imgWithResult);
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(view->{
                imageView.setVisibility(View.GONE);
            });
        });

    }

    /**
     * drawDetectionResult(bitmap: Bitmap, detectionResults: List<DetectionResult>
     *      Draw a box around each objects and show the object's name.
     */
    private Bitmap drawDetectionResult(
            Bitmap bitmap,
            List<DetectionResult> detectionResults
    ){
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint pen = new Paint();
        pen.setTextAlign(Paint.Align.LEFT);

        detectionResults.forEach((it)->{
            // draw bounding box
            pen.setColor(Color.RED);
            pen.setStrokeWidth(8F);
            pen.setStyle(Paint.Style.STROKE);
            RectF box = it.boundingBox;
            canvas.drawRect(box, pen);

            Rect tagSize = new Rect(0, 0, 0, 0);

            // calculate the right font size
            pen.setStyle(Paint.Style.FILL_AND_STROKE);
            pen.setColor(Color.YELLOW);
            pen.setStrokeWidth(2F);

            pen.setTextSize(MAX_FONT_SIZE);
            pen.getTextBounds(it.text, 0, it.text.length(), tagSize);
            Float fontSize = pen.getTextSize() * box.width() / tagSize.width();

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.getTextSize()) pen.setTextSize(fontSize);

            Float margin = (box.width() - tagSize.width()) / 2.0F;
            if (margin < 0F) margin = 0F;
            canvas.drawText(
                    it.text, box.left + margin,
                    box.top + tagSize.height()*1F, pen
            );

        });



        return outputBitmap;
    }
}

/**
 * DetectionResult
 *      A class to store the visualization info of a detected object.
 */
class DetectionResult {

    public RectF boundingBox;
    public String text;

    public DetectionResult(RectF boundingBox, String text) {
        this.boundingBox=boundingBox;
        this.text=text;
    }
}


    /*private void startCamera(PreviewView previewView,ImageView imageView){
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
            // Preview*/
            /*Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            //imageCapture = new ImageCapture.Builder().build();
            Log.d(TAG, "takePicture: " );

            //ImageCapture imageCapture = new ImageCapture.Builder().build();

            ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder().build();
            //imageAnalyzer.setAnalyzer(cameraExecutor, new LuminosityAnalyzer ( luma ->
            //        Log.d(TAG, "Average luminosity: $luma")
            //));
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
        //檔案名稱與儲存路徑
        File photoFile = new File(outputDirectory, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.TAIWAN).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor((MainActivity)getActivity()),new ImageCapture.OnImageSavedCallback(){
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d(TAG, "onImageSaved: " );
                Uri savedUri = Uri.fromFile(photoFile);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), savedUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //翻轉圖片
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90f);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(view->{
                    imageView.setVisibility(View.GONE);
                });
            }
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "onError: " ,exception);

            }

        });
    }*/

    /*private void takePicture(ImageView imageView){
        Log.d(TAG, "takePicture: " );

        //ImageCapture imageCapture = new ImageCapture.Builder().build();

        //檔案名稱與儲存路徑
        File photoFile = new File(outputDirectory, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.TAIWAN).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(getActivity()),new ImageCapture.OnImageSavedCallback(){
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d(TAG, "onImageSaved: " );
                Uri savedUri = Uri.fromFile(photoFile);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), savedUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //翻轉圖片
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90f);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(view->{
                    imageView.setVisibility(View.GONE);
                });
            }
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "onError: " ,exception);

            }

        });

    }*/



