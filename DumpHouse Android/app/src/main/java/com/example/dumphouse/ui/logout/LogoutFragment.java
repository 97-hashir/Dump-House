package com.example.dumphouse.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.dumphouse.LoginActivity;
import com.example.dumphouse.R;

public class LogoutFragment extends Fragment {

    private LogoutViewModel sendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(LogoutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        Intent i=new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        return root;
    }
}