package com.ecgreader;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> implements Filterable {
    private List<SearchModel> exampleList;
    private List<SearchModel> exampleListFull;
    class ExampleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        ExampleViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.histryImage);
            textView1 = itemView.findViewById(R.id.histryName);
            textView2 = itemView.findViewById(R.id.histryTag);
        }
    }
    ExampleAdapter(List<SearchModel> exampleList) {
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }
    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_histry,
                parent, false);
        return new ExampleViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        SearchModel currentItem = exampleList.get(position);
        //holder.imageView.setImageResource(currentItem.getImageResource());
        holder.textView1.setText(currentItem.getReportName());
        holder.textView2.setText(currentItem.getReport());
    }
    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SearchModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SearchModel item : exampleListFull) {
                    if (item.getReportName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}