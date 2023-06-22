package com.example.smartprescriptionpadfordoctors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> editTextValue = new MutableLiveData<>();

    public void setEditTextValue(String value) {
        editTextValue.setValue(value);
    }

    public LiveData<String> getEditTextValue() {
        return editTextValue;
    }
}
