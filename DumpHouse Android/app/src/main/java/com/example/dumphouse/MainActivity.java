package com.example.dumphouse;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.*;

import java.util.HashMap;
import java.util.Map;


// Signup Activity
public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText email;
    EditText pass;
    Switch switch1;
    Intent i;
    Intent j;

    FirebaseDatabase database;
    boolean checked=false; // false states donor user type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        setContentView(R.layout.activity_main);


        Button signUp =  findViewById(R.id.button);
        TextView singIn = findViewById(R.id.textView);
        email = findViewById(R.id.editText);
        pass = findViewById(R.id.editText2);
        switch1 = findViewById(R.id.switch1);

        i=new Intent(MainActivity.this, DonorActivity.class);
        j=new Intent(MainActivity.this, RecipientActivity.class);



        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                Log.v("Switch State=", ""+isChecked);
            }

        });

        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent   i=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createAccount(email.getText().toString(), pass.getText().toString(), checked);
                }
                catch(Exception e){

                    Log.e("Error","Error signing up");
                }
            }
        });
    }


    private void createAccount(String email, String password, final boolean check) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("sign up", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(!checked) { //false
                                addDonor(user.getUid());
                                startActivity(i);
                            }
                            else {
                                addRecipient(user.getUid());
                                startActivity(j);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fail", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
      //  updateUI(currentUser);
    }

    private void addDonor(String uid){

        DatabaseReference myRef = database.getReference("users");

        String key = myRef.push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(uid, "donor");

        myRef.updateChildren(childUpdates);
    }

    private void addRecipient(String uid){
        DatabaseReference myRef = database.getReference("users");
        String key = myRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(uid, "recipient");
        myRef.updateChildren(childUpdates);


    }


}
