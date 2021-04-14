package com.ecgreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // todo global variable
    String userId, userName;
    private LinearLayout lytLogin,signUpLyt, lytforgot;
    private  TextView tvSignUp,tvGoToLongIn, tvForgotPass, rePassMessage, fpassHad;
    private Button btnrecover;
    private Toolbar toolbar;
    ProgressBar progressBar;

    private EditText loginId, loginPass,edtName, edtMail, edtPassword, edtConPassword, edtFCode;
    private ImageView regProfileImage;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private static final int PICK_IMAGE = 1;
    //todo cameera global variable
    private static final int CAMERA_REQUEST = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        lytLogin.setVisibility(View.VISIBLE);
    }

    //todo initialized all use content
    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressBar);



        edtName = findViewById(R.id.edtName);
        edtMail = findViewById(R.id.edtMail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConPassword = findViewById(R.id.edtConPassword);
        loginId = findViewById(R.id.loginId);
        loginPass = findViewById(R.id.loginPass);
        regProfileImage = findViewById(R.id.regProfileImage);
        lytLogin = findViewById(R.id.lytLogin);
        signUpLyt = findViewById(R.id.signUpLyt);
        lytforgot = findViewById(R.id.lytforgot);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        edtFCode = findViewById(R.id.edtFCode);
        btnrecover = findViewById(R.id.btnrecover);
        rePassMessage = findViewById(R.id.rePassMessage);
        fpassHad = findViewById(R.id.fpassHad);


        //todo firebase element initialized
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        switch (view.getId()){
            case R.id.tvSignUp:
                signUpLyt.setVisibility(View.VISIBLE);
                lytLogin.setVisibility(View.GONE);
                break;
            case R.id.tvGoToLogin:
                signUpLyt.setVisibility(View.GONE);
                lytLogin.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSubmit:
                singUp();
                break;
            case R.id.btnLogin:
                singIn();
                break;
            case R.id.regProfileImage:
                openGallery();
                break;
                case R.id.tvForgotPass:
                    signUpLyt.setVisibility(View.GONE);
                    lytLogin.setVisibility(View.GONE);
                    lytforgot.setVisibility(View.VISIBLE);
                break;
            case R.id.btnrecover:
                Log.d("TAG recover padd ", "Clicked");
                    forgetPass();
                break;

        }
        

    }


    //todo forget pass word method
    private void forgetPass() {

        String recoverEmail = edtFCode.getText().toString();
        Log.d("TAG recover pass ", "called"+" email = "+recoverEmail);
        /*if (!isEmailValid(recoverEmail)){*/
            Log.d("TAG recover pass empty ", "called"+" email = "+recoverEmail);
            FirebaseAuth.getInstance().sendPasswordResetEmail(recoverEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                rePassMessage.setText("Please check your Email");
                                rePassMessage.setVisibility(View.VISIBLE);
                                fpassHad.setVisibility(View.GONE);
                            }
                            else{
                                rePassMessage.setText(task.getException().getMessage());
                            }
                        }
                    });
        //}
    }

    //todo login method
    private void singIn() {
        String email = loginId.getText().toString();
        String password = loginPass.getText().toString();
        if(verifyLoginData(email, password)){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this,
                            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "something wrong",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    private boolean verifyLoginData(String email, String password) {
        if(email.isEmpty()){
            loginId.setError("Email is empty");
            return false;
        }
        else if (!isEmailValid(email)){
            loginId.setError("Email is not valid");
            return false;
        }
        else if(password.isEmpty()){
            loginPass.setError("Password is empty");
            return false;
        }
        else if(password.length()<6){
            loginPass.setError("Invalid password");
            return false;
        }

        return true;
    }

    private void singUp() {

        String name = edtName.getText().toString();
        String email = edtMail.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPass = edtConPassword.getText().toString();

        if(verifyRegData(name, email, password, confirmPass)){
            if (imageUri!=null){
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                                Uri file = imageUri;
                                StorageReference storageRef = storage.getReference();
                                StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                                riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String imageLink = String.valueOf(uri);
                                                userId = mAuth.getCurrentUser().getUid();
                                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);
                                                Map<String, Object> userData = new HashMap<>();
                                                userData.put("name", name.toUpperCase());
                                                userData.put("Email", email);
                                                userData.put("profilePic", imageLink);
                                                documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("TAG", "User account has been created for: "+userId);
                                                    }
                                                });
                                                DocumentReference documentReference1 = firebaseFirestore.collection("Users").document(userId).collection("reports").document("initial");
                                                documentReference1.set(new HashMap<String, Object>());
                                                Log.d("Tag IMage Link ", imageLink);
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                });

                        }
                        else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(),"Image filed is empty",Toast.LENGTH_LONG).show();
            }


        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
                imageUri = data.getData();
                regProfileImage.setImageURI(imageUri);
            }

    }
    private void addToDatabase(String name, String email, String confirmPass, String imageLink) {
        /*userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("Email", email);
        userData.put("Password", confirmPass);
        userData.put("profilePic", imageLink);
        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "User account has been created for: "+userId);
            }
        });
        DocumentReference documentReference1 = firebaseFirestore.collection("Users").document(userId).collection("reports").document("initial");
*/
    }

    private boolean verifyRegData(String name, String email, String password, String confirmPass) {
        if(name.isEmpty()){
            edtName.setError("Name is empty");
            return false;
        }
        else if(email.isEmpty()){
            edtMail.setError("Email is empty");
            return false;
        }
        else if (!isEmailValid(email)){
            edtMail.setError("Email is not valid");
            return false;
        }
        else if(password.isEmpty()){
            edtPassword.setError("Password is empty");
            return false;
        }
        else if(password.length()<6){
            edtPassword.setError("Password must be 6 character long");
            return false;
        }
        else if(confirmPass.isEmpty()){
            edtConPassword.setError("Confirm password");
            return false;
        }
        else if (!password.equals(confirmPass)){
            edtConPassword.setError("password not matched");
            return false;
        }

        return true;
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                //onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}