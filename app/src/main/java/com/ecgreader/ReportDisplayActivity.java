package com.ecgreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ReportDisplayActivity extends AppCompatActivity {
    Toolbar toolbar;
    private TextView tvReport;
    private LinearLayout report_lyt, home_bottom2;
    private ImageView resultEcgImageView;
    private TextView pAge, pGender, pPainType, pRestbp, pChol, p_blood_sugar,
            p_ecg_result, p_heart_rate, p_Exang, p_Oldpeak, p_Slop, p_CA, p_thal, report_hader;
    final Context context = this;
    private String age, Trestbp, Chol, Thalach, Oldpeak, Ca, Gender,
            cp, FBS, ecg, exang, slop, thal, reportName, report,imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_display);
        initialized();
    }

    private void initialized() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        report_hader = findViewById(R.id.report_hader);
        displayData();
    }

    private void displayData() {
        Intent intent = getIntent();
        report = intent.getStringExtra("report");
        reportName = intent.getStringExtra("reportName");
        imageLink = intent.getStringExtra("imageLink");
        age = intent.getStringExtra("age");
        Trestbp = intent.getStringExtra("Trestbp");
        Chol = intent.getStringExtra("Chol");
        Thalach = intent.getStringExtra("Thalach");
        Oldpeak = intent.getStringExtra("Oldpeak");
        Ca = intent.getStringExtra("Ca");
        Gender = intent.getStringExtra("Gender");
        cp = intent.getStringExtra("cp");
        FBS = intent.getStringExtra("FBS");
        ecg = intent.getStringExtra("ecg");
        exang = intent.getStringExtra("exang");
        slop = intent.getStringExtra("slop");
        thal = intent.getStringExtra("thal");

        if(report.equals("Heart Attack Positive")){
            report_lyt.setBackgroundColor(Color.parseColor("#FD625E"));
            report_hader.setBackgroundColor(Color.parseColor("#FD625E"));
        }
        else if(report.equals("Heart Attack Negative")){
            report_lyt.setBackgroundColor(Color.parseColor("#228c22"));
            report_hader.setBackgroundColor(Color.parseColor("#228C22"));
        }
        tvReport.setText(report);
        report_hader.setText(reportName);
        Picasso.get()
                .load(imageLink)
                .placeholder(R.drawable.appslogo)
                .error(R.drawable.appslogo)
                .into(resultEcgImageView);
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