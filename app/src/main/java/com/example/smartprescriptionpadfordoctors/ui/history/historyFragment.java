package com.example.smartprescriptionpadfordoctors.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartprescriptionpadfordoctors.databinding.FragmentHistoryBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
