package com.capstone.bookcollectiontracker.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.bookcollectiontracker.R;
import com.capstone.bookcollectiontracker.ui.adapters.ReportFieldAdapter;
import com.capstone.bookcollectiontracker.ui.viewmodels.BookViewModel;
import com.capstone.bookcollectiontracker.ui.viewmodels.ReportViewModel;
import com.capstone.bookcollectiontracker.ui.viewmodels.ReportViewModelFactory;
import com.capstone.bookcollectiontracker.util.InputSanitizer;

import java.util.ArrayList;

public class ReportGenerationFragment extends Fragment {
    private static final int MAX_TITLE_LENGTH = 30;

    private ReportViewModel viewModel;
    private BookViewModel bookViewModel;
    private int userId;

    private RecyclerView fieldSelectionRecyclerView;
    private TextView previewTextView;
    private Button generateReportButton;

    private EditText editTextReportTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_generation, container, false);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("userId")) {
            userId = getArguments().getInt("userId");

            editTextReportTitle = view.findViewById(R.id.editTextReportTitle);
            fieldSelectionRecyclerView = view.findViewById(R.id.fieldSelectionRecyclerView);
            previewTextView = view.findViewById(R.id.previewTextView);
            generateReportButton = view.findViewById(R.id.generateReportButton);

            bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

            bookViewModel.getAllBooksForUser(userId).observe(getViewLifecycleOwner(), books -> {
                //viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
                if (books != null && !books.isEmpty()) {
                    ReportViewModelFactory factory = new ReportViewModelFactory(books);
                    viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
                        @NonNull
                        @Override
                        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                            return (T) new ReportViewModel(books);
                        }
                    }).get(ReportViewModel.class);
                    setupUI(view);
                } else {
                    Log.d("ReportGenerationFragment", "No books found for user ID: " + userId);
                }
            });
        } else {
            Log.e("ReportGenerationFragment", "Arguments bundle or UserId not found");
            previewTextView = view.findViewById(R.id.previewTextView);
            previewTextView.setText("User ID not found.");
        }

        return view;
    }

    private void setupUI(View view) {
        ReportFieldAdapter adapter = new ReportFieldAdapter(new ArrayList<>(), this::onFieldSelected);
        fieldSelectionRecyclerView.setAdapter(adapter);
        fieldSelectionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getReportFields().observe(getViewLifecycleOwner(), fields -> {
            Log.d("ReportGenerationFragment", "Report preview: " + fields);
            if (fields != null && !fields.isEmpty()) {
                adapter.updateFields(fields);
            } else {
                Log.d("ReportGenerationFragment", "Report field is null");
            }
        });

        viewModel.getReportPreview().observe(getViewLifecycleOwner(), preview -> {
            Log.d("ReportGenerationFragment", "Report preview: " + preview);
            if (preview != null) {
                previewTextView.setText(preview);
            } else {
                Log.d("ReportGenerationFragment", "Report preview is null");
            }
        });

        generateReportButton.setOnClickListener(v -> {
            Log.d("ReportGenerationFragment", "Generate report button clicked");
            String reportTitle = editTextReportTitle.getText().toString();
            if (reportTitle.isEmpty()) {
                showError("Report title is required");
                editTextReportTitle.requestFocus();
                return;
            }

            if (reportTitle.length() > MAX_TITLE_LENGTH) {
                showError("Report title cannot exceed " + MAX_TITLE_LENGTH + " characters");
                editTextReportTitle.requestFocus();
                return;
            }

            reportTitle = InputSanitizer.sanitizeInput(reportTitle);

            if (InputSanitizer.containsSqlKeywords(reportTitle)) {
                showError("Invalid characters in report title");
                editTextReportTitle.requestFocus();
                return;
            }

            if (!isValidTitle(reportTitle)) {
                showError("Invalid characters in report title");
                return;
            }

            viewModel.setReportTitle(reportTitle);
            String generatedReport = viewModel.generateReport();

            Bundle bundle = new Bundle();
            bundle.putString("report_data", generatedReport);
            Navigation.findNavController(v).navigate(R.id.action_reportGenerationFragment_to_reportDisplayFragment, bundle);
        });
    }

    private boolean isValidTitle(String title) {
        String regex = "^[a-zA-Z0-9\\s._-]+$";
        return title.matches(regex);
    }

    private void onFieldSelected(int position, boolean isSelected) {
        Log.d("ReportGenerationFragment", "Field selected: " + position + ", isSelected: " + isSelected);
        viewModel.updateFieldSelection(position, isSelected);

        viewModel.generatePreview();
    }
    private void showError(String message) {
        Log.e("ReportGenerationFragment", message);
        previewTextView.setText(message);
    }
}

