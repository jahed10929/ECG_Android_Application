package com.ecgreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePassActivity extends AppCompatActivity {
    Toolbar toolbar;
    private TextView chPassMessage;
    private EditText oldPass, newPass, conNewPass;


    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        initialized();
    }

    private void initialized() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
        chPassMessage = findViewById(R.id.chPassMessage);
        newPass = findViewById(R.id.newPass);
        conNewPass = findViewById(R.id.conNewPass);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnchanger:
                changePass();
        }
    }

    private void changePass() {

        String nPass="";
        String cNPass="";
        nPass=newPass.getText().toString();
        cNPass=conNewPass.getText().toString();


        if (chechPassLen(nPass, cNPass)&&chechPassmatch(nPass, cNPass)){
            changePassToDatabase(cNPass);
        }
    }

    private void changePassToDatabase(String cNPass) {

        FirebaseUser user =mAuth.getCurrentUser();
        if (user!=null){
            user.updatePassword(cNPass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password changed Successful", Toast.LENGTH_SHORT);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                            else{
                                chPassMessage.setVisibility(View.VISIBLE);
                                chPassMessage.setText(task.getException().getMessage());
                            }
                        }
                    });
        }

    }

    private boolean chechPassmatch(String nPass, String cNPass) {
        if (nPass.equals(cNPass))
            return true;
        else
            return false;
    }

    private boolean chechPassLen(String nPasss, String cNPass) {
        if (nPasss.length()<6){
            newPass.setError("Minimum 6 character required");
            return false;
        }

        else if (cNPass.length()<6){
            oldPass.setError("Minimum 6 character required");
            return false;
        }
        else
            return true;
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