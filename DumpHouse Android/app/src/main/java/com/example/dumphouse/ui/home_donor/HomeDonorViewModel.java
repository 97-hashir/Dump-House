package com.example.dumphouse.ui.home_donor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeDonorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeDonorViewModel() {
        mText = new MutableLiveData<>();

    }


}