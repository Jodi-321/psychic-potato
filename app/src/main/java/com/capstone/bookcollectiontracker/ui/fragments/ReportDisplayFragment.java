package com.capstone.bookcollectiontracker.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone.bookcollectiontracker.R;

public class ReportDisplayFragment extends Fragment {
    private static final String ARG_REPORT_DATA = "report_data";

    //private TextView reportTextView;
    private Button shareButton;

    public static ReportDisplayFragment newInstance(String reportData) {
        ReportDisplayFragment fragment = new ReportDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REPORT_DATA, reportData);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_display, container, false);
        TextView reportTextView = view.findViewById(R.id.report_text_view);


        reportTextView.setMovementMethod(new ScrollingMovementMethod());
        shareButton = view.findViewById(R.id.button_share);


        if (getArguments() != null) {
            String reportData = getArguments().getString(ARG_REPORT_DATA);
            if (reportData != null) {
                reportTextView.setText(reportData);
            } else {
                Log.d("ReportDisplayFragment", "Report data is null");
                reportTextView.setText("No report data available.");
            }
        } else {
            Log.d("ReportDisplayFragment", "Arguments are null");
            reportTextView.setText("No report data available.");
        }

        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, reportTextView.getText().toString());
            startActivity(Intent.createChooser(shareIntent, "Share Report"));
        });
        return view;
    }
}
