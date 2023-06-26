package com.example.smartprescriptionpadfordoctors.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.smartprescriptionpadfordoctors.R;
import com.example.smartprescriptionpadfordoctors.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView dateTimeTextView;
    private Button printButton;
    private Button download;
    private EditText docname;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateTimeTextView = view.findViewById(R.id.dateTime);
        docname = view.findViewById(R.id.patientIdEdit);
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("prescriptions");

        TextView otherFragmentTextView = view.findViewById(R.id.hospitalName);

        printButton = view.findViewById(R.id.saveAndPrintBtn);
        download = view.findViewById(R.id.downloadbtn);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docName = docname.getText().toString().trim();

                if (docName.isEmpty()) {
                    // Handle empty document name
                    docname.setError("Please enter a patient ID");
                    return;
                }

                // Get the current date and time
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
                String currentDateTime = dateFormat.format(new Date());

                // Get the print manager
                PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);

                // Set the print job name
                String jobName = getString(R.string.app_name) + " Document";

                // Start the print job
                printManager.print(jobName, new MyPrintDocumentAdapter(requireContext(), binding.prescription, docName), null);

                // Extract the content from the prescription view
                String prescriptionContent = extractPrescriptionContent(binding.prescription);

                // Save the prescription content to Firestore and Firebase Storage
                saveContentToFirestore(docName, prescriptionContent, currentDateTime);

            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docName = docname.getText().toString().trim();
                // Fetch patient details from Firestore and initiate download
                fetchAndWritePatientPrescriptionsToExcel();

            }
        });


        docname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String docName = docname.getText().toString().trim();
                    if (!docName.isEmpty()) {
                        fetchPatientDetails(docName);
                    }
                }
            }
        });



    }

    private void fetchPatientDetails(String docName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("prescriptions");

        databaseReference.child(docName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String patientName = dataSnapshot.child("Patient name").getValue(String.class);
                    Long age = dataSnapshot.child("Age").getValue(Long.class);
                    String gender = dataSnapshot.child("Gender").getValue(String.class);

                    String ageString = (age != null) ? String.valueOf(age) : "";


                    Toast.makeText(getContext(), "Fetched", Toast.LENGTH_SHORT).show();

                    // Set the retrieved values to the corresponding TextViews
                    binding.patientname.setText(patientName);
                    binding.age.setText(ageString);
                    binding.Gender.setText(gender);
                } else {
                    // Data not found in Realtime Database, try fetching from Firestore
                    fetchPatientDetailsFromFirestore(docName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String toastMessage = "Error fetching patient details from Firebase Realtime Database: " + databaseError.getMessage();
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void fetchPatientDetailsFromFirestore(String docName) {
        DocumentReference documentReference = firestore.collection("prescriptions").document(docName);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String patientName = documentSnapshot.getString("PatientName");
                    String age = documentSnapshot.getString("Age");
                    String gender = documentSnapshot.getString("Gender");

                    binding.patientname.setText(patientName);
                    binding.age.setText(age);
                    binding.Gender.setText(gender);

                } else {
                    Toast.makeText(getContext(), "Patient details not found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String toastMessage = "Error fetching patient details from Firestore: " + e.getMessage();
                Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String extractPrescriptionContent(View prescriptionView) {
        StringBuilder contentBuilder = new StringBuilder();

        // Extract the content from the prescription view
        EditText prescriptionEditText = prescriptionView.findViewById(R.id.writingView);
        String prescriptionText = prescriptionEditText.getText().toString();

        // Split the text into lines
        String[] lines = prescriptionText.split("\n");

        // Append each line to the content builder
        for (String line : lines) {
            contentBuilder.append(line).append("\n");
        }

        return contentBuilder.toString();
    }


    private void saveContentToFirestore(String docName, String prescriptionContent, String currentDateTime) {
        // Replace this with your own implementation to save the content to Firestore
        // Create a new prescription document with the document name
        Map<String, Object> prescriptionData = new HashMap<>();
        prescriptionData.put("PatientName", binding.patientname.getText().toString());
        prescriptionData.put("Age", binding.age.getText().toString());
        prescriptionData.put("Gender", binding.Gender.getText().toString());
        prescriptionData.put("VisitedDateTime", currentDateTime); // Add visited date and time field
        prescriptionData.put("PatientPrescriptionDetails", prescriptionContent);

        // Add the prescription document to the Firestore collection
        firestore.collection("prescriptions").document(docName)
                .set(prescriptionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful save
                        Toast.makeText(getContext(), "Prescription saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        String toastMessage = "Error saving prescription: " + e.getMessage();
                        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fetchAndWritePatientPrescriptionsToExcel() {
        firestore.collection("prescriptions")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Create a new Excel workbook
                            Workbook workbook = new XSSFWorkbook();

                            // Create a new Excel sheet
                            Sheet sheet = workbook.createSheet("Prescriptions");

                            // Create the header row
                            Row headerRow = sheet.createRow(0);
                            headerRow.createCell(0).setCellValue("Serial Number");
                            headerRow.createCell(1).setCellValue("Patient ID");
                            headerRow.createCell(2).setCellValue("Patient Name");
                            headerRow.createCell(3).setCellValue("Age");
                            headerRow.createCell(4).setCellValue("Gender");
                            headerRow.createCell(5).setCellValue("Visited Date and Time");

                            int rowIndex = 1;
                            int serialNumber = 1;

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Fetch prescription details from the document snapshot
                                String docName = documentSnapshot.getId();
                                String patientName = documentSnapshot.getString("PatientName");
                                String age = documentSnapshot.getString("Age");
                                String gender = documentSnapshot.getString("Gender");
                                String visitedDateTime = documentSnapshot.getString("VisitedDateTime");

                                // Create a new row and populate it with prescription details
                                Row row = sheet.createRow(rowIndex++);
                                row.createCell(0).setCellValue(serialNumber++);
                                row.createCell(1).setCellValue(docName);
                                row.createCell(2).setCellValue(patientName);
                                row.createCell(3).setCellValue(age);
                                row.createCell(4).setCellValue(gender);
                                row.createCell(5).setCellValue(visitedDateTime);
                            }

                            // Save the Excel file
                            FileOutputStream fileOutputStream = null;
                            try {
                                Context context = getContext();
                                if (context != null) {
                                    File file = new File(context.getExternalFilesDir(null), "PatientPrescriptions.xlsx");
                                    fileOutputStream = new FileOutputStream(file);
                                    workbook.write(fileOutputStream);
                                    fileOutputStream.close();
                                    workbook.close();

                                    // Create an intent to share the file
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("application/vnd.ms-excel");
                                    Uri fileUri = FileProvider.getUriForFile(context, "com.example.smartprescriptionpadfordoctors.fileprovider", file);
                                    intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(Intent.createChooser(intent, "Share Excel file"));

                                    Toast.makeText(context, "Excel file created", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error creating Excel file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "No prescriptions found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String toastMessage = "Error fetching prescriptions from Firestore: " + e.getMessage();
                        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();

        // Start updating the date and time
        startUpdatingDateTime();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop updating the date and time
        stopUpdatingDateTime();
    }

    private void startUpdatingDateTime() {
        Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                dateTimeTextView.postDelayed(this, 1000); // Update every second (1000 milliseconds)
            }
        };

        dateTimeTextView.post(updateTimeRunnable);
    }

    private void stopUpdatingDateTime() {
        dateTimeTextView.removeCallbacks(null);
    }

    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());

        dateTimeTextView.setText(currentDateTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
