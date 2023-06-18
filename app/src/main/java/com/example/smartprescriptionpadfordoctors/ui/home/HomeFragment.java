package com.example.smartprescriptionpadfordoctors.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartprescriptionpadfordoctors.R;
import com.example.smartprescriptionpadfordoctors.databinding.FragmentHomeBinding;
import com.example.smartprescriptionpadfordoctors.signIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView dateTimeTextView;
    private Handler handler;
    private Runnable updateTimeRunnable;
    private Button printButton;
    private EditText docname;

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

        // Initialize the handler
        handler = new Handler(Looper.getMainLooper());

        printButton = view.findViewById(R.id.saveAndPrintBtn);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the print manager
                PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);

                String docName = docname.getText().toString().trim();

                if (docName.isEmpty()) {
                    // Handle empty document name
                    docname.setError("Please enter a patient ID");
                    return;
                }

                // Set the print job name
                String jobName = getString(R.string.app_name) + " Document";

                // Start the print job
                printManager.print(jobName, new MyPrintDocumentAdapter(requireContext(), binding.prescription,docName), null);

                // Extract the content from the prescription view
                String prescriptionContent = extractPrescriptionContent(binding.prescription);

                // Extract the content from the prescription view
                saveContentToFirestore(binding.prescription, docName,prescriptionContent);
            }
        });

    }
    private String extractPrescriptionContent(View prescriptionView) {
        StringBuilder contentBuilder = new StringBuilder();

        // Extract the content from the prescription view
        TextView hospitalNameTextView = prescriptionView.findViewById(R.id.hospitalName);
        String hospitalName = hospitalNameTextView.getText().toString();
        contentBuilder.append("Hospital Name: ").append(hospitalName).append("\n");

        EditText patientIdEditText = prescriptionView.findViewById(R.id.patientIdEdit);
        String patientId = patientIdEditText.getText().toString();
        contentBuilder.append("Patient ID: ").append(patientId).append("\n");

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


    private void saveContentToFirestore(View prescriptionView, String docName, String prescriptionContent) {
        // Replace this with your own implementation to save the content to Firestore
        // Initialize the Firestore database and collection reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference prescriptionsCollection = db.collection("prescriptions");

        // Create a new prescription document with the document name
        Map<String, Object> prescriptionData = new HashMap<>();
        prescriptionData.put("PatientID", docName);
        prescriptionData.put("Patient Prescription Details ", prescriptionContent);

        // Extract the content line by line from the prescription view
        ViewGroup container = (ViewGroup) prescriptionView;
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
        prescriptionsCollection.add(prescriptionData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Handle successful saving
                        String toastMessage = "Prescription saved to Firestore: " + documentReference.getId();
                        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        String toastMessage = "Error saving prescription to Firestore: " + e.getMessage();
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
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000); // Update every second (1000 milliseconds)
            }
        };

        handler.post(updateTimeRunnable);
    }

    private void stopUpdatingDateTime() {
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
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
