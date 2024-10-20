package com.capstone.bookcollectiontracker.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.capstone.bookcollectiontracker.data.model.Book;
import com.capstone.bookcollectiontracker.data.model.ReportField;

import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReportViewModel extends ViewModel {
    private MutableLiveData<List<ReportField>> reportFields = new MutableLiveData<>();
    private MutableLiveData<String> reportPreview = new MutableLiveData<>();
    private List<Book> bookList;

    private MutableLiveData<String> reportTitle = new MutableLiveData<>();
    private MutableLiveData<String> reportDateTime = new MutableLiveData<>();

    public ReportViewModel(List<Book> bookList) {
        this.bookList = bookList;
        initializeReportFields();
    }

    private void initializeReportFields() {
        List<ReportField> fields = new ArrayList<>();

        fields.add(new ReportField("Title", false));
        fields.add(new ReportField("Author", false));
        fields.add(new ReportField("Genre", false));
        fields.add(new ReportField("Publication Date", false));
        //fields.add(new ReportField("Read Status", false));
        fields.add(new ReportField("Notes", false));
        fields.add(new ReportField("Format Details", false));

        for (ReportField field : fields) {
            Log.d("ReportViewModel", "Field: " + field.getName() + ", Selected: " + field.isSelected());
        }

        reportFields.setValue(fields);
    }

    public LiveData<String> getReportTitle() {
        return reportTitle;
    }

    public LiveData<String> getReportDateTime() {
        return reportDateTime;
    }

    public void setReportTitle(String title) {
        reportTitle.setValue(title);
    }

    public void setReportDateTime(String dateTime) {
        reportDateTime.setValue(dateTime);
    }

    public LiveData<List<ReportField>> getReportFields() {
        return reportFields;
    }

    public LiveData<String> getReportPreview() {
        return reportPreview;
    }

    public void updateFieldSelection(int position, boolean isSelected) {
        List<ReportField> fields = reportFields.getValue();
        if (fields != null) {
            fields.get(position).setSelected(isSelected);
            reportFields.setValue(fields);
            generateReport();
        }
    }

    public String generateReport() {
        List<ReportField> fields = reportFields.getValue();
        if (fields == null) {
            Log.d("ReportViewModel", "Report fields are null");
            reportPreview.setValue("No fields selected.");
            return "No Fields Selected";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        setReportDateTime(sdf.format(new Date()));

        StringBuilder preview = new StringBuilder("Report Preview:\n");
        preview.append("Report Title: ").append(reportTitle.getValue()).append("\n");
        preview.append("Report Date: ").append(reportDateTime.getValue()).append("\n");

        boolean hasSelectedFields = false;

        /*
        for (ReportField field : fields) {
            Log.d("ReportViewModel", "Field: " + field.getName() + ", Selected: " + field.isSelected());
            if (field.isSelected()) {
                hasSelectedFields = true;
                preview.append(field.getName()).append(": Sample Data\n");
                hasSelectedFields = true;
            }
        }

         */
        for (Book book : bookList) {
            for (ReportField field : fields) {
                if (field.isSelected()) {
                    hasSelectedFields = true;
                    switch (field.getName()) {
                        case "Title":
                            preview.append("Title: ").append(book.getTitle()).append("\n");
                            break;
                        case "Author":
                            preview.append("Author: ").append(book.getAuthor()).append("\n");
                            break;
                            case "Genre":
                            preview.append("Genre: ").append(book.getGenre()).append("\n");
                            break;
                        case "Publication Date":
                            preview.append("Publication Date: ").append(book.getPublicationDate()).append("\n");
                            break;
                        case "Notes":
                            preview.append("Notes: ").append(book.getNotes()).append("\n");
                            break;
                        case "Format Details":
                            preview.append("Format Details: ").append(book.getFormatDetails()).append("\n");
                            break;
                        default:
                    }
                }
            }

            preview.append("\n");

        }


        if (!hasSelectedFields) {
            Log.d("ReportViewModel", "No fields selected");
            preview.append("No fields selected.");
        }

        Log.d("ReportViewModel", "Report preview: " + preview.toString());
        String reportData = preview.toString();
        reportPreview.setValue(reportData);

        return reportData;
    }

    public void loadPresetTemplate(String templateName) {
        List<ReportField> template = getPresetTemplate(templateName);
        reportFields.setValue(template);
        generateReport();
    }

    private List<ReportField> getPresetTemplate(String templateName) {
        List<ReportField> template = new ArrayList<>();

        if("Basic".equals(templateName)) {
            template.add(new ReportField("Title", false));
            template.add(new ReportField("Author", false));
            //template.add(new ReportField("Genre", false));
            //template.add(new ReportField("Publication Date", false));
        } else if ("Detailed".equals(templateName)) {
            template.add(new ReportField("Title", false));
            template.add(new ReportField("Author", false));
            template.add(new ReportField("Genre", false));
            template.add(new ReportField("Publication Date", false));
            //template.add(new ReportField("Read Status", false));
            template.add(new ReportField("Notes", false));
            template.add(new ReportField("Format Details", false));


        }
        return template;
    }

    public void generatePreview() {
        generateReport();
    }


}
