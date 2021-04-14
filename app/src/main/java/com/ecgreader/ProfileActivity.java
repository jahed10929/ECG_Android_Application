package com.ecgreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    ImageView userImage, changeName, changeProfile;
    TextView userName, userEmail, userPass, noUdata;
    Button updateUserData;
    LinearLayout userDataLyt;


    String uName;
    String oldImage;
    boolean changeNameClicked = false;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;


    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialized();
    }

    private void initialized() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        progressBar = findViewById(R.id.progressBar);

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userDataLyt = findViewById(R.id.userDataLyt);
        noUdata = findViewById(R.id.noUdata);
        updateUserData = findViewById(R.id.updateUserData);
        changeName = findViewById(R.id.changeName);
        changeProfile = findViewById(R.id.changeProfile);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        setData();
    }

    public void OnClickBtn(View view) {
        int id = view.getId();
        switch (id){
            case R.id.changeProfile:
                Log.d("Tag =========", " image clicked");
                openGallery();
                break;
            case R.id.userImage:
                Log.d("Tag =========", " image icon clicked");
                openGallery();
                break;
            case R.id.changeName:
                if (!changeNameClicked){
                    Log.d("Tag =========", " name clicked");
                    userName.setEnabled(true);
                    userName.setText("");
                    updateUserData.setVisibility(View.VISIBLE);
                    changeNameClicked = true;
                }
                else{
                    userName.setText(uName);
                    userName.setEnabled(false);
                    changeNameClicked = false;
                }
                break;
            case R.id.updateUserData:
                Log.d("Tag =========", " update clicked");
                chackData();
                break;


        }
    }

    private void chackData() {
        boolean image = false,
                both = false,
                name = false;
        if (imageUri!=null){
            image = true;
            if(!userName.getText().toString().equals(uName))
                both = true;
        }
        else if(!userName.getText().toString().equals(uName)){
            name = true;
        }
        if (!image&&!name&&!both){
            Toast.makeText(getApplicationContext(), "No change", Toast.LENGTH_SHORT).show();
        }
        else if(both){
            Log.d("Tag =========", " update both");
            updateAllData();
        }else if(name){
            updateName();
        }else if(image){
            updateImage();
        }

    }

    private void updateImage() {
        progressBar.setVisibility(View.VISIBLE);
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
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", userName.getText().toString().toUpperCase());
                        userData.put("profilePic", imageLink);
                        userData.put("Email", userEmail.getText().toString());
                        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "User account has been created for: "+userId);
                            }
                        });

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    private void updateName() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", userName.getText().toString().toUpperCase());
        userData.put("profilePic", oldImage);
        userData.put("Email", userEmail.getText().toString());
        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "User account has been created for: "+userId);
            }
        });

        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }

    private void updateAllData() {
        progressBar.setVisibility(View.VISIBLE);
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
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userId);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", userName.getText().toString().toUpperCase());
                        userData.put("profilePic", imageLink);
                        userData.put("Email", userEmail.getText().toString());
                        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "User account has been created for: "+userId);
                            }
                        });

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            userImage.setImageURI(imageUri);
            updateUserData.setVisibility(View.VISIBLE);
        }

    }

    private void setData() {
        if (mAuth.getCurrentUser() != null) {
            userDataLyt.setVisibility(View.VISIBLE);
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference docRef = firebaseFirestore.collection("Users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        try {
                            oldImage = document.get("profilePic").toString();
                            uName = document.get("name").toString();
                            userName.setText(document.get("name").toString());
                            Picasso.get()
                                    .load(document.get("profilePic").toString())
                                    .placeholder(R.drawable.ic_circle)
                                    .error(R.drawable.ic_circle)
                                    .into(userImage);
                            userEmail.setText(document.get("Email").toString());
                            userPass.setText(document.get("Password").toString());
                        }
                        catch (Exception e){
                            Log.d("TAg------",e.getMessage() );
                        }
                    } else {
                        Log.d("TAG", "Cached get failed: ", task.getException());
                    }
                }
            });
        } else {
            noUdata.setVisibility(View.VISIBLE);
        }
    }
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