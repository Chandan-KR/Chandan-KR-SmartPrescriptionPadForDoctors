package com.example.smartprescriptionpadfordoctors.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartprescriptionpadfordoctors.R;
import com.example.smartprescriptionpadfordoctors.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView dateTimeTextView;
    private Handler handler;
    private Runnable updateTimeRunnable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dateTimeTextView = root.findViewById(R.id.dateTime);

        // Initialize the handler
        handler = new Handler(Looper.getMainLooper());

        return root;
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
