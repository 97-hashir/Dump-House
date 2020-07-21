package com.example.dumphouse.ui.report;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.dumphouse.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ReportFragment extends Fragment {
    Button contact;
    FirebaseDatabase database;
    private ReportViewModel toolsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ReportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        final TextView textView = root.findViewById(R.id.text_tools);
        toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        database = FirebaseDatabase.getInstance();
        final TextView title =  root.findViewById(R.id.editText3);
        contact = root.findViewById(R.id.button3);
        contact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Write a message to the database
                DatabaseReference myRef = database.getReference("reports");
                String key = myRef.push().getKey();


                HashMap<String, Object> result = new HashMap<>();
                result.put("description", title.getText().toString());

                Map<String, Object> postValues = result;


                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put(key, postValues);

                myRef.updateChildren(childUpdates);
                Toast.makeText(getActivity(), "Report Submitted!",
                        Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }
}