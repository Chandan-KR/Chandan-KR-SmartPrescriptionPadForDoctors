package com.example.smartprescriptionpadfordoctors.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartprescriptionpadfordoctors.R;
import com.example.smartprescriptionpadfordoctors.SharedViewModel;
import com.example.smartprescriptionpadfordoctors.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.HttpCookie;

public class profileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private EditText hospitalName;


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel galleryViewModel =
                new ViewModelProvider(this).get(profileViewModel.class);
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Find the TextViews and EditText by ID from the inflated layout
        usernameTextView = rootView.findViewById(R.id.profile_username);
        emailTextView = rootView.findViewById(R.id.profile_Email);
        phoneTextView = rootView.findViewById(R.id.profile_phone);
        EditText hospitalNameEditText = rootView.findViewById(R.id.hospitalName);

        // Fetch and display the user's details
        fetchUserDetails();

        return root;
    }

    private void fetchUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    // Retrieve the user's data from the document
                                    String username = document.getString("username");
                                    String email = document.getString("email");
                                    String phoneNumber = document.getString("phone");

                                    // Update the TextViews with the retrieved user data
                                    binding.profileUsername.setText(username);
                                    binding.profileEmail.setText(email);
                                    binding.profilePhone.setText(phoneNumber);
                                    System.out.println(username);
                                    System.out.println(email);
                                    System.out.println(phoneNumber);

                                } else {
                                    Log.d(TAG, "User document doesn't exist");
                                }
                            } else {
                                // Handle the error
                                Log.d(TAG, "Error getting user document: " + task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}