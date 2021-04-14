package com.ecgreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

public class DrawerActivity extends AppCompatActivity {

    // todo declare all global variable here

    public NavigationView navigationView;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle drawerToggle;
    protected FrameLayout frameLayout;
    public TextView tvMobile;
    ImageView imageView;
    public static TextView tvName;
    LinearLayout lytProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        // todo call initial method here
        initialize();
        setUpProfile();


        //todo call navigation drawer
        setupNavigationDrawer();
    }

    //todo setup profileData
    private void setUpProfile() {

    }


    // todo initialized all used variable
    private void initialize() {
        frameLayout = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        View header = navigationView.getHeaderView(0);
        tvName = header.findViewById(R.id.header_name);
        imageView = header.findViewById(R.id.imageView);


        tvMobile = header.findViewById(R.id.tvMobile);
        lytProfile = header.findViewById(R.id.lytProfile);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    /*
     * create a method for setup Navigation Drawer
     */
    //todo setupNavigationDrawer here
    private void setupNavigationDrawer() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.menu_profile ).setVisible(true);


        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference docRef = firebaseFirestore.collection("Users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        try {
                            tvMobile.setText(document.get("name").toString());
                            Picasso.get()
                                    .load(document.get("profilePic").toString())
                                    .placeholder(R.drawable.ic_circle)
                                    .error(R.drawable.ic_circle)
                                    .into(imageView);
                        }
                        catch (Exception e){
                            Log.d("TAg------",e.getMessage() );
                        }

                        tvMobile.setVisibility(View.VISIBLE);
                        tvName.setVisibility(View.GONE);
                        nav_Menu.findItem(R.id.menu_logout).setVisible(true);
                    } else {
                        Log.d("TAG", "Cached get failed: ", task.getException());
                    }
                }
            });
        } else {
            tvMobile.setVisibility(View.GONE);
            tvName.setVisibility(View.VISIBLE);
            nav_Menu.findItem(R.id.menu_logout).setVisible(false);
        }


        /*System.out.println(tvName.getText().toString());
        if (tvName.getText().toString().equals("Login")) {
            nav_Menu.findItem(R.id.menu_logout).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.menu_logout).setVisible(false);
        }

        *//*if (session.isUserLoggedIn()) {
            nav_Menu.findItem(R.id.menu_profile).setVisible(true);
            nav_Menu.findItem(R.id.menu_logout).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.menu_logout).setVisible(false);
            nav_Menu.findItem(R.id.menu_profile).setVisible(false);
        }*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawer.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        //todo stop and call notification activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;

                    case R.id.menu_profile:
                        //todo call profile activity
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        finish();
                        break;

                    case R.id.changePass:
                        //todo call change password activity
                        startActivity(new Intent(getApplicationContext(), ChangePassActivity.class));
                        finish();
                        break;

                    case R.id.menu_share:
                        //todo call menu share activity
                        break;
                    //todo singOut
                    case R.id.menu_logout:
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }


}