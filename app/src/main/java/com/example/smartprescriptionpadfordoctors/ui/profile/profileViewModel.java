package com.example.smartprescriptionpadfordoctors.ui.profile;

import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartprescriptionpadfordoctors.R;

public class profileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public profileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}