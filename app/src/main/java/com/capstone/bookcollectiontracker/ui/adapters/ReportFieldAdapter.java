package com.capstone.bookcollectiontracker.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.capstone.bookcollectiontracker.R;
import com.capstone.bookcollectiontracker.data.model.ReportField;

import java.util.List;

public class ReportFieldAdapter extends RecyclerView.Adapter<ReportFieldAdapter.ViewHolder> {
    private List<ReportField> fields;
    private OnFieldSelectedListener listener;
    private RecyclerView recyclerView;

    public ReportFieldAdapter(List<ReportField> fields, OnFieldSelectedListener listener) {
        this.fields = fields;
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.d("ReportFieldAdapter", "onAttachedToRecyclerView called");
        this.recyclerView = recyclerView;
    }

    public void updateFields(List<ReportField> fields) {
        Log.d("ReportFieldAdapter", "Updating fields in adapter with: " + fields.size() + " fields");
        this.fields = fields;
        //notifyDataSetChanged();
        if (recyclerView != null) {
            recyclerView.post(() -> notifyDataSetChanged());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setFields(List<ReportField> fields) {
        this.fields.clear();
        this.fields.addAll(fields);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ReportFieldAdapter", "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_field, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReportField field = fields.get(position);
        Log.d("ReportFieldAdapter", "Binding field: " + field.getName());

        holder.fieldCheckBox.setOnCheckedChangeListener(null);
        holder.fieldCheckBox.setText(field.getName());
        holder.fieldCheckBox.setChecked(field.isSelected());

        Log.d("ReportFieldAdapter", "Field name: " + field.getName() + " is selected: " + field.isSelected());



        holder.fieldCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("ReportFieldAdapter", "Field selected: " + position + ", isSelected: " + isChecked);
            field.setSelected(isChecked);
            listener.onFieldSelected(position, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox fieldCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            fieldCheckBox = itemView.findViewById(R.id.fieldCheckBox);
        }
    }


    public interface OnFieldSelectedListener {
        void onFieldSelected(int position, boolean isSelected);
    }
}
