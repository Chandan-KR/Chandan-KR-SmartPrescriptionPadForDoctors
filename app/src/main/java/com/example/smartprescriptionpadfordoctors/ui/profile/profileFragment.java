package com.example.smartprescriptionpadfordoctors.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.service.autofill.Field;
import android.util.FloatProperty;
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
import com.example.smartprescriptionpadfordoctors.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.HttpCookie;

public class profileFragment extends Fragment {

    private FragmentProfileBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel galleryViewModel =
                new ViewModelProvider(this).get(profileViewModel.class);
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Find the TextView by ID from the inflated layout
        TextView usernameTextView = rootView.findViewById(R.id.profile_username);
        TextView emailTextView = rootView.findViewById(R.id.profile_Email);
        TextView phoneTextView = rootView.findViewById(R.id.profile_phone);
       // TextView hospitalNameTextView = rootView.findViewById(R.id.hospitalName);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve the user's data from the document
                                String username = document.getString("username");
                                String email = document.getString("email");
                                String phoneNumber = document.getString("phoneNumber");

                                // Do something with the retrieved user data
                                usernameTextView.setText(username);
                                emailTextView.setText(email);
                                phoneTextView.setText(phoneNumber);
                                //hospitalNameTextView.setText();
                            }
                        } else {
                            // Handle the error
                            Log.d(TAG, "Error getting user document: " + task.getException());
                        }
                    }
                });


        return root;
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