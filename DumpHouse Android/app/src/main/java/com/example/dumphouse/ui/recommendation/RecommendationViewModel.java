package com.example.dumphouse.ui.recommendation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecommendationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecommendationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("The API is working on the recommendations!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}