package com.ecgreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import static com.ecgreader.Constent.IMAGE_URI_KEY;

public class ReportActivity extends AppCompatActivity {
    //todo apps common global variable
    Toolbar toolbar;
    Menu menu;
    Intent intent;
    ProgressBar progressBar;
    //todo global variable for layout
    RelativeLayout lytBottom1,lytBottom2,lytBottom3;
    LinearLayout lyt_user_data, lyt_report_result;

    //todo global variable for views

    TextView reportText,report_hader;


    //todo extra global variable
    private String option="";
    Uri imageUri;
    Bitmap photo;


    //todo gallery global variable
    private static final int PICK_IMAGE = 1;


    //todo cameera global variable
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    //todo report layout variable
    Uri receivedImageUri;
    private TextView tvReport;
    private LinearLayout report_lyt, home_bottom2;
    private ImageView resultEcgImageView;
    private TextView pAge, pGender, pPainType, pRestbp, pChol, p_blood_sugar,
            p_ecg_result, p_heart_rate, p_Exang, p_Oldpeak, p_Slop, p_CA, p_thal;
    final Context context = this;
    private String age, Trestbp, Chol, Thalach, Oldpeak, Ca, Gender,
            cp, FBS, ecg, exang, slop, thal, reportName;

    //todo user data layout variable
    private ImageView ecgImage;
    private EditText uAge, trestbp, chol, thalach, oldpeak, ca;
    private RadioGroup radioGroupGender, radioGroupCP, radioGroupFBS, radioRestecg, radioExang, radioSlope, radiothal;


    //todo firebase variable
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;

    // todo ML variables
    Interpreter interpreter;
    private float[][] floats = new float[1][13];


    //todo onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        //todo call variable initialize method heare
        initialize();

        //todo call setImage method for display ecg image
        setImage();
    }

    //todo this method for initialize xml content
    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        report_hader = findViewById(R.id.report_hader);
        progressBar = findViewById(R.id.progressBar);
        Toast.makeText(getApplicationContext(), "Fill up this patient information form to get report", Toast.LENGTH_LONG).show();
        //todo user data layout
        lytBottom1 = findViewById(R.id.report_bottom);
        lyt_user_data = findViewById(R.id.user_data);
        ecgImage = findViewById(R.id.ecgImageView);
        uAge = findViewById(R.id.age);
        trestbp = findViewById(R.id.trestbp);
        chol = findViewById(R.id.chol);
        thalach = findViewById(R.id.thalach);
        oldpeak = findViewById(R.id.oldpeak);
        ca = findViewById(R.id.ca);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupCP = findViewById(R.id.radioGroupCP);
        radioGroupFBS = findViewById(R.id.radioGroupFBS);
        radioRestecg = findViewById(R.id.radioRestecg);
        radioExang = findViewById(R.id.radioExang);
        radioSlope = findViewById(R.id.radioSlope);
        radiothal = findViewById(R.id.radiothal);
        userInputInterface();


        //todo report  layout
        lytBottom2 = findViewById(R.id.report_bottom2);
        lyt_report_result = findViewById(R.id.report_result);
        tvReport = findViewById(R.id.report);
        report_lyt = findViewById(R.id.report_lyt);
        resultEcgImageView = findViewById(R.id.resultEcgImageView);
        pAge = findViewById(R.id.pAge);
        pGender = findViewById(R.id.pGender);
        pPainType = findViewById(R.id.pPainType);
        pChol = findViewById(R.id.pChol);
        p_blood_sugar = findViewById(R.id.p_blood_sugar);
        p_ecg_result = findViewById(R.id.p_ecg_result);
        p_heart_rate = findViewById(R.id.p_heart_rate);
        p_Exang = findViewById(R.id.p_Exang);
        p_Oldpeak = findViewById(R.id.p_Oldpeak);
        p_Slop = findViewById(R.id.p_Slop);
        p_CA = findViewById(R.id.p_CA);
        p_thal = findViewById(R.id.p_thal);
        pRestbp = findViewById(R.id.pRestbp);
        home_bottom2 = findViewById(R.id.home_bottom2);


        //todo firebase element initialized
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        //todo Interpreter initialized
        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void userInputInterface() {
        lyt_user_data.setVisibility(View.VISIBLE);
        lytBottom1.setVisibility(View.VISIBLE);
    }

    //todo load ML model
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor =  this.getAssets().openFd("ht2.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long length = assetFileDescriptor.getLength();
        return         fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);
    }

    //todo set image in ecg image view
    private void setImage(){
        intent = getIntent();
        String option = intent.getStringExtra("option");
        if(option.equals("Gallery")){

            receivedImageUri = getIntent().getParcelableExtra(Constent.IMAGE_URI_KEY);

            System.out.println("------------------------------"+receivedImageUri);
            ecgImage.setImageURI(receivedImageUri);
            resultEcgImageView.setImageURI(receivedImageUri);

        }
        else if(option.equals("Camera")){
            Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
            System.out.println("------------------------------"+bitmap);
            ecgImage.setImageBitmap(bitmap);
            resultEcgImageView.setImageBitmap(bitmap);
        }
    }

    //todo catch all click here
    public void OnClickBtn(View view) {
        int id = view.getId();
        /*if(id == R.id.camera_button){
            //todo open camera for capture picture
            System.out.println("camera clicked");
        }*/
        if(id == R.id.report_button){
            //todo open camera for capture picture
            getUserData();

        }
        /*else if(id == R.id.gallery_button){
            // todo open gallery for select image
            System.out.println("gallery clicked");
            option = "Gallery";
            openGallery();
        }*/
        /*else if(id == R.id.cancel_button){
            // todo goto main report page
            lytBottom2.setVisibility(View.INVISIBLE);
            lytBottom1.setVisibility(View.VISIBLE);
            lytBottom3.setVisibility(View.INVISIBLE);
        }*/
        else if(id == R.id.save_button){
            // todo goto main report page
            if(mAuth.getCurrentUser() != null){
                saveDataToDB();
            }
            else{
                Toast.makeText(getApplicationContext(), "Login first to save report", Toast.LENGTH_SHORT);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }

    }

    //todo save report data to database
    private void saveDataToDB() {
        Log.d("Tag save", "clicked");
        LayoutInflater layoutinflater = LayoutInflater.from(context);
        View promptUserView = layoutinflater.inflate(R.layout.dilog_lyt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptUserView);

        final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);

        alertDialogBuilder.setTitle("Enter Report Name");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // and display the username on main activity layout
                String reportName = userAnswer.getText().toString();
                if (reportName.isEmpty()){
                    saveDataToDB();
                    Toast.makeText(getApplicationContext(), "Invalid Name", Toast.LENGTH_LONG).show();
                    Log.d("Tag save empty", "userAnswer.getText().toString()");
                }
                else{
                    storeToDB(userAnswer.getText().toString());
                    Log.d("Tag save", userAnswer.getText().toString());
                }
            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Report Not saved", Toast.LENGTH_LONG).show();
            }
        });

        // all set and time to build and show up!
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void storeToDB(String name) {
        reportName = name;
        requestRead();
    }
    //todo request permission
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            storeImage();
        }
    }
    private void storeImage() {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("ecgImage/"+receivedImageUri.getLastPathSegment());
        riversRef.putFile(receivedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageLink = String.valueOf(uri);
                        storereport(imageLink);

                    }
                });
            }
        });
    }
    private void storereport(String imageLink) {
        String userId = mAuth.getCurrentUser().getUid();

        Log.d("TAG-------------", "report added to : "+ reportName);
        Log.d("TAG-------------", "report added to : "+imageLink);
        Log.d("TAG-------------", "report added to : "+age);
        Log.d("TAG-------------", "report added to : "+Trestbp);
        Log.d("TAG-------------", "report added to : "+Chol);
        Log.d("TAG-------------", "report added to : "+Thalach);
        Log.d("TAG-------------", "report added to : "+Oldpeak);
        Log.d("TAG-------------", "report added to : "+Ca);
        Log.d("TAG-------------", "report added to : "+Gender);
        Log.d("TAG-------------", "report added to : "+cp);
        Log.d("TAG-------------", "report added to : "+FBS);
        Log.d("TAG-------------", "report added to : "+ecg);
        Log.d("TAG-------------", "report added to : "+exang);
        Log.d("TAG-------------", "report added to : "+slop);
        Log.d("TAG-------------", "report added to : "+thal);

        DocumentReference documentReference = firebaseFirestore
                .collection("Users").document(userId)
                .collection("reports").document();

        Map<String, Object> favProduct = new HashMap<>();

        favProduct.put("report", tvReport.getText().toString());
        favProduct.put("report name", reportName.toUpperCase());
        favProduct.put("image link", imageLink);
        favProduct.put("age", age);
        favProduct.put("Trestbp", Trestbp);
        favProduct.put("Chol", Chol);
        favProduct.put("Thalach", Thalach);
        favProduct.put("Oldpeak", Oldpeak);
        favProduct.put("Ca", Ca);
        favProduct.put("Gender", Gender);
        favProduct.put("cp", cp);
        favProduct.put("FBS", FBS);
        favProduct.put("ecg", ecg);
        favProduct.put("exang", exang);
        favProduct.put("slop", slop);
        favProduct.put("thal", thal);
        favProduct.put("status", "true");
        documentReference.set(favProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Saved successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

        });
    }


    //todo getUserData
    private void getUserData() {
        age = uAge.getText().toString();
        Trestbp = trestbp.getText().toString();
        Chol = chol.getText().toString();
        Thalach = thalach.getText().toString();
        Oldpeak = oldpeak.getText().toString();
        Ca = ca.getText().toString();
        Gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
        cp = ((RadioButton) findViewById(radioGroupCP.getCheckedRadioButtonId())).getText().toString();
        FBS = ((RadioButton) findViewById(radioGroupFBS.getCheckedRadioButtonId())).getText().toString();
        ecg = ((RadioButton) findViewById(radioRestecg.getCheckedRadioButtonId())).getText().toString();
        exang = ((RadioButton) findViewById(radioExang.getCheckedRadioButtonId())).getText().toString();
        slop = ((RadioButton) findViewById(radioSlope.getCheckedRadioButtonId())).getText().toString();
        thal = ((RadioButton) findViewById(radiothal.getCheckedRadioButtonId())).getText().toString();
        if (checkUserData( age, Trestbp, Chol, Thalach, Oldpeak, Ca)){
            floats[0][0] = Float.valueOf(age);

            if(Gender.equals("  Male"))
                floats[0][1] =	1;
            else
                floats[0][1] =	0;


            if (cp.equals("  typical angina"))
                floats[0][2] = 	0	;
            else if (cp.equals("   atypical angina"))
                floats[0][2] = 	1	;
            else if (cp.equals("  non-anginal pain"))
                floats[0][2] = 	2	;
            else
                floats[0][2] = 	3	;

            floats[0][3] = Float.valueOf(Trestbp);

            floats[0][4] = Float.valueOf(Chol);

            if (FBS.equals("  True"))
                floats[0][5] = 	1;
            else
                floats[0][5] = 	0;

            if(ecg.equals("normal"))
                floats[0][6] = 	0	;
            else if(ecg.equals("having ST-T wave abnormality"))
                floats[0][6] = 	1;
            else
                floats[0][6] = 	2	;

            floats[0][7] = 	Float.valueOf(Thalach)	;

            if (exang.equals("  Yes"))
                floats[0][8] = 	1;
            else
                floats[0][8] = 	0;

            floats[0][9] = Float.valueOf(Oldpeak);

            if (slop.equals("  upsloping"))
                floats[0][10] =0;
            else if (slop.equals("  flat"))
                floats[0][10] =1;
            else
                floats[0][10] =2;

            floats[0][11] = Float.valueOf(Ca)	;

            if (thal.equals("   normal"))
                floats[0][12] = 1;
            else if (thal.equals("  fixed defect"))
                floats[0][12] = 2;
            else
                floats[0][12] = 3;

            pAge.setText(age);
            pGender.setText(Gender);
            pPainType.setText(cp);
            pChol.setText(Chol);
            p_blood_sugar.setText(FBS);
            p_ecg_result.setText(ecg);
            p_heart_rate.setText(Thalach);
            p_Exang.setText(exang);
            p_Oldpeak.setText(Oldpeak);
            p_Slop.setText(slop);
            p_CA.setText(Ca);
            p_thal.setText(thal);
            pRestbp.setText(Trestbp);
            getHartReport();
            reportInterface();
        }
    }

    private boolean checkUserData(String age, String Trestbp, String Chol, String Thalach, String Oldpeak, String Ca) {
        if(age.isEmpty()){
            Toast.makeText(getApplicationContext(), "Empty age", Toast.LENGTH_SHORT);
            uAge.setError("Empty age");
            return false;
        }
        else if(Trestbp.isEmpty()){
            Toast.makeText(getApplicationContext(), "Empty bp", Toast.LENGTH_SHORT);
            trestbp.setError("Empty bp");
            return false;
        }
        else if(Chol.isEmpty()){
            Toast.makeText(getApplicationContext(), "Empty chol", Toast.LENGTH_SHORT);
            chol.setError("Empty chol");
            return false;
        }
        else if(Thalach.isEmpty()){
            Toast.makeText(getApplicationContext(), "Empty Thalach", Toast.LENGTH_SHORT);
            trestbp.setError("Empty Thalach");
            return false;
        }
        else if(Oldpeak.isEmpty()){
            Toast.makeText(getApplicationContext(), "Empty Old peak", Toast.LENGTH_SHORT);
            oldpeak.setError("Empty Old peak");
            return false;
        }
        else if(Ca.isEmpty()){
            Toast.makeText(getApplicationContext(), "Empty CA", Toast.LENGTH_SHORT);
            ca.setError("Empty CA");
            return false;
        }
        else
            return true;
    }

    //todo create report interface
    private void reportInterface() {
        lyt_user_data.setVisibility(View.GONE);
        lyt_report_result.setVisibility(View.VISIBLE);
        lytBottom1.setVisibility(View.GONE);
        lytBottom2.setVisibility(View.VISIBLE);
    }
    //todo get hart Report
    private void getHartReport() {

       /* floats[0][1] =	1	;
        floats[0][2] = 	2	;
        floats[0][3] = 	129		;
        floats[0][4] = 196	;
        floats[0][5] = 	0		;
        floats[0][6] = 	1	;
        floats[0][7] = 	163	;
        floats[0][8] = 	0	;

        floats[0][9] = (float) 0.0		;
        floats[0][10] = 		2	;
        floats[0][11] = 0	;
        floats[0][12] = 2;*/

        String report ;
        float res = doInference(floats);
        if(res==1.0){
            report_lyt.setBackgroundColor(Color.parseColor("#EE3124"));
            tvReport.setBackgroundColor(Color.parseColor("#EE3124"));
            report="Heart Attack Positive";
        }

        else if(res==0.0){
            report_lyt.setBackgroundColor(Color.parseColor("#228C22"));
            tvReport.setBackgroundColor(Color.parseColor("#228C22"));
            report="Heart Attack Negative";
        }

        else{
            report_lyt.setBackgroundColor(Color.parseColor("#EC808D"));
            tvReport.setBackgroundColor(Color.parseColor("#EC808D"));
            report="Heart Attack Negative \nconsult a doctor for confirmation";
        }
        //reportText.setText(res+" "+report);
        tvReport.setText(report);
    }

    public float doInference(float[][] input) {
        float[][] output = new float[1][1];

        interpreter.run(input, output);

        return output[0][0];
    }

    /*private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }*/

    // todo take image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(option.equals("Gallery")){
            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
                imageUri = data.getData();
                ecgImage.setImageURI(imageUri);
                lytBottom2.setVisibility(View.INVISIBLE);
                lytBottom1.setVisibility(View.VISIBLE);
                lytBottom3.setVisibility(View.INVISIBLE);
            }
        }

    }

    //todo read permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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


    //todo handle back pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                // onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}