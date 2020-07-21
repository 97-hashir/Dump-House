package com.example.dumphouse.ui.home_recipient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeRecipientViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeRecipientViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}