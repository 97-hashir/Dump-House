package com.example.dumphouse.ui.help;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.dumphouse.ContactFragmentActivity;
import com.example.dumphouse.InstructionFragmentActivity;
import com.example.dumphouse.R;
import com.example.dumphouse.FaqFragmentActivity;

public class HelpFragment extends Fragment {

    private HelpViewModel shareViewModel;
    Button b1;
    Button b2;
    Button b3;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(HelpViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_help, container, false);


        b1=(Button)root.findViewById(R.id.buttonFaq);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), FaqFragmentActivity.class);
                startActivity(intent);

            }
        });

        b2=(Button)root.findViewById(R.id.buttonContact);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ContactFragmentActivity.class);
                startActivity(intent);
            }
        });


        b3=(Button)root.findViewById(R.id.buttonInst);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), InstructionFragmentActivity.class);
                startActivity(intent);

            }
        });

        return root;

    }

}