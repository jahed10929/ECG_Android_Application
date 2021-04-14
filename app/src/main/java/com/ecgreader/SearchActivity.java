package com.ecgreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ExampleAdapter adapter;
    private List<SearchModel> exampleList;
    //todo firebase variable
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = findViewById(R.id.searchview);
        //todo firebase initialized
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fillExampleList();
        setUpRecyclerView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    private void fillExampleList() {
        exampleList = new ArrayList<>();
        String userId = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(userId)
                .collection("reports").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getId().toString().equals("initial")){
                                    if(document.get("status").toString().equals("true")){
                                        exampleList.add(new SearchModel(
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


                        }

                    }
                });
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new ExampleAdapter(exampleList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_search).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }
}


