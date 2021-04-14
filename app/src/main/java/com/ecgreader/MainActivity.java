package com.ecgreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends  DrawerActivity {

    //todo global variable
    private String name;
    TextView header_name;
    Toolbar toolbar;
    ProgressBar progressBar;
    View view;
    Uri imageUri;
    private String option="";
    Menu menu;
    TextView noData;
    boolean doubleBackToExitPressedOnce = false;
    //todo load data global variable
    private ArrayList<HistryModel> histryModelArrayList;
    private HistryAdapter histryAdapter;

    //todo global variable for layout
    public RelativeLayout layoutSearch,lytBottom1, lytBottom2;
    private RecyclerView historyView;

    //todo gallery global variable
    private static final int PICK_IMAGE = 1;


    //todo cameera global variable
    private static final int CAMERA_REQUEST = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    //todo ML variable
    private int mInputSize = 224;
    private String mModelPath="ecg_image_detect.tflite";
    private String mLabelPath="labels.txt";
    private Classifier classifier;

    //todo firebase variable
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;


    //todo ocCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        //todo call apps common variable initialize method
        initialize();

        LoadHistry();
        drawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawer, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {};
    }


    //todo this method for initialize xml content
    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*getSupportActionBar().setTitle(getResources().getString(R.string.app_name));*/

        //todo ml initialized
        try {
            initClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }


        noData = findViewById(R.id.noData);
        progressBar = findViewById(R.id.progressBar);
        lytBottom1 = findViewById(R.id.home_bottom1);
        lytBottom2 = findViewById(R.id.home_bottom2);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutSearch.setVisibility(View.VISIBLE);

        //todo firebase initialized
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }
    private void initClassifier() throws IOException {
        classifier = new Classifier(getAssets(), mModelPath, mLabelPath, mInputSize);
    }


    private void LoadHistry(){
        histryModelArrayList = new ArrayList<>();
        historyView = findViewById(R.id.historyView);
        historyView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
        GetHistryView();
    }



    private void GetHistryView(){
        if(mAuth.getCurrentUser() != null){
            progressBar.setVisibility(View.VISIBLE);
            ArrayList<HistryModel> List = new ArrayList<HistryModel>();
            histryAdapter = new HistryAdapter(MainActivity.this, List);
            historyView.setAdapter(histryAdapter);
            String userId = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(userId)
                    .collection("reports").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (!document.getId().toString().equals("initial")){
                                        noData.setVisibility(View.VISIBLE);
                                        historyView.setVisibility(View.GONE);
                                        if(document.get("status").toString().equals("true")){
                                            noData.setVisibility(View.GONE);
                                            historyView.setVisibility(View.VISIBLE);
                                            List.add(new HistryModel(
                                                    document.get("report").toString(),
                                                    document.get("report name").toString(),
                                                    document.get("image link").toString(),
                                                    document.get("age").toString(),
                                                    document.get("Trestbp").toString(),
                                                    document.get("Chol").toString(),
                                                    document.get("Thalach").toString(),
                                                    document.get("Oldpeak").toString(),
                                                    document.get("Ca").toString(),
                                                    document.get("Gender").toString(),
                                                    document.get("cp").toString(),
                                                    document.get("FBS").toString(),
                                                    document.get("ecg").toString(),
                                                    document.get("exang").toString(),
                                                    document.get("slop").toString(),
                                                    document.get("thal").toString()
                                            ));
                                        }
                                    }



                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                }

                                histryAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "Error getting documents: ", task.getException());
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
            if (List.size()==0)
                noData.setVisibility(View.VISIBLE);
            else
                historyView.setVisibility(View.VISIBLE);

        }
        else{
            noData.setVisibility(View.VISIBLE);
            historyView.setVisibility(View.GONE);
        }

    }

    //todo catch all click here
    public void OnClickBtn(View view) {
        switch (view.getId()){
            // todo start image select layout
            case R.id.seclectImageButton:
                lytBottom1.setVisibility(View.INVISIBLE);
                lytBottom2.setVisibility(View.VISIBLE);
                break;

            //todo open camera for capture picture
                case R.id.camera_button:
                    option = "Camera";
                    System.out.println("camera clicked");
                    openCamera();
                break;

            // todo open gallery for select image
                case R.id.gallery_button:
                    option = "Gallery";
                    openGallery();
                break;

            // todo goto hode option menu
                case R.id.cancel_button:
                    lytBottom2.setVisibility(View.INVISIBLE);
                    lytBottom1.setVisibility(View.VISIBLE);
                break;
            case R.id.header_name:
                if(tvName.getText().toString().equals("Login")){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
                /*case R.id.layoutSearch:
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    break;*/
            // todo goto main home page

        }
    }


    //todo get image from gallery
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    //todo get image from camera
    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }




    //todo camera and gallery permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    //todo for input image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(option.equals("Gallery")){
            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
                imageUri = data.getData();
                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<Classifier.Recognition> result = classifier.recognizeImage(bitmap);
                Log.d("Tag---------------", result.get(0).toString());
                if(!result.get(0).toString().equals("Title = 0 Ecg, Confidence = 0.0"))
                    Toast.makeText(this, "Please insert an ECG image", Toast.LENGTH_LONG).show() ;
                else{
                    Intent newIntent = new Intent(this, ReportActivity.class);
                    newIntent.putExtra("option", option);
                    newIntent.putExtra(Constent.IMAGE_URI_KEY, imageUri);
                    startActivity(newIntent);
                    finish();
                }
            }
        }
    }




    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    //todo this method for prepared header menu item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        *//*menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.menu_sort).setVisible(true);
        menu.findItem(R.id.menu_cart).setVisible(true);*//*

        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navigationView))
            drawer.closeDrawers();
        else
            doubleBack();
    }

    public void doubleBack() {
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            super.finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}