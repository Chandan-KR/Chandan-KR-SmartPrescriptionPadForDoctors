package com.example.smartprescriptionpadfordoctors.ui.history;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartprescriptionpadfordoctors.databinding.FragmentHistoryBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class historyFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private FirebaseFirestore firestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel slideshowViewModel =
                new ViewModelProvider(this).get(historyViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHistory;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        firestore = FirebaseFirestore.getInstance();

        fetchPatientDetailsFromFirestore();

        Button exportButton = binding.buttonExport;
        exportButton.setOnClickListener(v -> {
            exportDataToExcel();
        });
        return root;

    }

    private void fetchPatientDetailsFromFirestore() {
        firestore.collection("prescriptions")
                .orderBy("VisitedDateTime")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    StringBuilder prescriptionDetails = new StringBuilder();

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String patientName = documentSnapshot.getString("PatientName");
                        String age = documentSnapshot.getString("Age");
                        String gender = documentSnapshot.getString("Gender");
                        String patientPrescriptionDetails = documentSnapshot.getString("PatientPrescriptionDetails");
                        String visitedDateTime = documentSnapshot.getString("VisitedDateTime");

                        prescriptionDetails.append("Patient Name: ").append(patientName).append("\n");
                        prescriptionDetails.append("Age: ").append(age).append("\n");
                        prescriptionDetails.append("Gender: ").append(gender).append("\n");
                        prescriptionDetails.append("Patient Prescription Details: ").append(patientPrescriptionDetails).append("\n");
                        prescriptionDetails.append("Visited Date Time: ").append(visitedDateTime).append("\n");
                        prescriptionDetails.append("-----------------------------------------------").append("\n");
                    }

                    binding.textHistory.setText(prescriptionDetails.toString());
                })
                .addOnFailureListener(e -> {
                    String toastMessage = "Error fetching patient details from Firestore: " + e.getMessage();
                    Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                });
    }

    private void exportDataToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Prescription History");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Patient Name");
        headerRow.createCell(1).setCellValue("Age");
        headerRow.createCell(2).setCellValue("Gender");
        headerRow.createCell(3).setCellValue("Prescription Details");
        headerRow.createCell(4).setCellValue("Visited Date Time");

        // Rest of the code for exporting data to Excel

        // Save the workbook to a file in internal storage
        try {
            File file = createExcelFile();

            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
// Notify the Media Scanner about the new file
            MediaScannerConnection.scanFile(
                    requireContext(),
                    new String[]{file.getAbsolutePath()},
                    null,
                    (path, uri) -> {
                        // File scanned successfully
                        Toast.makeText(getContext(), "Data exported to Excel successfully.", Toast.LENGTH_SHORT).show();
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error exporting data to Excel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createExcelFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Prescription_History_" + timeStamp + ".xlsx";

        // Get the internal storage directory specific to the app
        File dir = requireContext().getFilesDir();

        // Create the file
        File file = new File(dir, fileName);
        return file;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
