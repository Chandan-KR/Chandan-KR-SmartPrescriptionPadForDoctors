package com.example.smartprescriptionpadfordoctors.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartprescriptionpadfordoctors.R;
import com.example.smartprescriptionpadfordoctors.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView dateTimeTextView;
    private Button printButton;
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

        printButton = view.findViewById(R.id.saveAndPrintBtn);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docName = docname.getText().toString().trim();

                if (docName.isEmpty()) {
                    // Handle empty document name
                    docname.setError("Please enter a patient ID");
                    return;
                }

                // Get the print manager
                PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);

                // Set the print job name
                String jobName = getString(R.string.app_name) + " Document";

                // Start the print job
                printManager.print(jobName, new MyPrintDocumentAdapter(requireContext(), binding.prescription, docName), null);

                // Extract the content from the prescription view
                String prescriptionContent = extractPrescriptionContent(binding.prescription);

                // Save the prescription content to Firestore
                saveContentToFirestore(docName, prescriptionContent);
            }
        });

        Button fetchButton = view.findViewById(R.id.fetchbtn);
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docName = docname.getText().toString().trim();
                if (!docName.isEmpty()) {
                    fetchPatientDetails(docName);
                }
            }
        });
    }

    private void fetchPatientDetails(String docName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smartppd-2a51f-default-rtdb.firebaseio.com/prescriptions");

        Query query = databaseReference.orderByChild("docName").equalTo(docName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String patientName = dataSnapshot.child("PatientName").getValue(String.class);
                        String age = dataSnapshot.child("Age").getValue(String.class);
                        String gender = dataSnapshot.child("Gender").getValue(String.class);


                        System.out.println("Patient Name: " + patientName);
                        System.out.println("Age: " + age);
                        System.out.println("Gender: " + gender);
                        Toast.makeText(getContext(), "Fetched ", Toast.LENGTH_SHORT).show();

                        // Set the retrieved values to the corresponding TextViews
                        binding.patientname.setText(patientName);
                        binding.age.setText(age);
                        binding.Gender.setText(gender);
                    }
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

                    System.out.println("Patient Name: " + patientName);
                    System.out.println("Age: " + age);
                    System.out.println("Gender: " + gender);

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
        EditText patientNameEditText = prescriptionView.findViewById(R.id.patientname);
        String patientName = patientNameEditText.getText().toString();
        contentBuilder.append("Patient Name: ").append(patientName).append("\n");

        EditText ageEditText = prescriptionView.findViewById(R.id.age);
        String age = ageEditText.getText().toString();
        contentBuilder.append("Age: ").append(age).append("\n");

        EditText genderEditText = prescriptionView.findViewById(R.id.Gender);
        String gender = genderEditText.getText().toString();
        contentBuilder.append("Gender: ").append(gender).append("\n");

        // Add any additional views you want to extract content from

        return contentBuilder.toString();
    }

    private void saveContentToFirestore(String docName, String prescriptionContent) {
        // Replace this with your own implementation to save the content to Firestore
        // Create a new prescription document with the document name
        Map<String, Object> prescriptionData = new HashMap<>();
        prescriptionData.put("PatientName", binding.patientname.getText().toString());
        prescriptionData.put("Age", binding.age.getText().toString());
        prescriptionData.put("Gender", binding.Gender.getText().toString());
        prescriptionData.put("PatientPrescriptionDetails", prescriptionContent);

        // Extract the content line by line from the prescription view
        ViewGroup container = (ViewGroup) binding.prescription;
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = container.getChildAt(i);
            if (childView instanceof EditText) {
                EditText editText = (EditText) childView;
                String lineContent = editText.getText().toString();
                prescriptionData.put("line" + (i + 1), lineContent);
            }
        }

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
