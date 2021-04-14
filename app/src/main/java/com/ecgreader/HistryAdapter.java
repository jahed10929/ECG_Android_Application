package com.ecgreader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HistryAdapter extends RecyclerView.Adapter<HistryAdapter.ViewHolder> {
    private ArrayList<HistryModel> histryModelArrayList;
    private Activity activity;
    public HistryAdapter(Activity activity, ArrayList<HistryModel> histryModelArrayList) {
        this.histryModelArrayList = histryModelArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_histry,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String report = histryModelArrayList.get(position).getReport();
        String reportName = histryModelArrayList.get(position).getReportName();
        String imageLink = histryModelArrayList.get(position).getImageLink();
        String age = histryModelArrayList.get(position).getAge();
        String Trestbp = histryModelArrayList.get(position).getTrestbp();
        String Chol = histryModelArrayList.get(position).getChol();
        String Thalach = histryModelArrayList.get(position).getThalach();
        String Oldpeak = histryModelArrayList.get(position).getOldpeak();
        String Ca = histryModelArrayList.get(position).getCa();
        String Gender = histryModelArrayList.get(position).getGender();
        String cp = histryModelArrayList.get(position).getCp();
        String FBS = histryModelArrayList.get(position).getFBS();
        String ecg = histryModelArrayList.get(position).getEcg();
        String exang = histryModelArrayList.get(position).getExang();
        String slop = histryModelArrayList.get(position).getSlop();
        String thal = histryModelArrayList.get(position).getThal();
        if(report.equals("Heart Attack Positive")){
            holder.histryTag.setTextColor(Color.parseColor("#FD625E"));

        }
        else if(report.equals("Heart Attack Negative")||
                report.equals("Heart Attack Negative \nconsult a doctor for confirmation")){
            holder.histryTag.setTextColor(Color.parseColor("#228C22"));
        }

        holder.setCategoryImage(imageLink);
        holder.setCategoryName(reportName);
        holder.setCategoryTeg(report);

        holder.reportItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(activity, ReportDisplayActivity.class);
                intent.putExtra("report", report);
                intent.putExtra("reportName", reportName);
                intent.putExtra("imageLink", imageLink);
                intent.putExtra("age", age);
                intent.putExtra("Trestbp", Trestbp);
                intent.putExtra("Chol", Chol);
                intent.putExtra("Thalach", Thalach);
                intent.putExtra("Oldpeak", Oldpeak);
                intent.putExtra("Ca", Ca);
                intent.putExtra("Gender", Gender);
                intent.putExtra("cp", cp);
                intent.putExtra("FBS", FBS);
                intent.putExtra("ecg", ecg);
                intent.putExtra("exang", exang);
                intent.putExtra("slop", slop);
                intent.putExtra("thal", thal);
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return histryModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout reportItem;
        ImageView histryImage;
        TextView histryName;
        TextView histryTag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reportItem = itemView.findViewById(R.id.reportItem);
            histryImage = itemView.findViewById(R.id.histryImage);
            histryName = itemView.findViewById(R.id.histryName);
            histryTag = itemView.findViewById(R.id.histryTag);
        }
        private void setCategoryImage(String image){
            //todo set category image
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.center_logo)
                    .error(R.drawable.center_logo)
                    .into(histryImage);
        }
        private void setCategoryName(String name){
            //todo set category image
            histryName.setText(name);
        }
        private void setCategoryTeg(String teg){
            //todo set category image
            histryTag.setText(teg);
        }
    }
}
