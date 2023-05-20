package com.example.docbank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.docbank.User.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class register extends AppCompatActivity {
    TextView a, b, c, d, e, f;
    Button Register;
    EditText name, email, mobile, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        Register = findViewById(R.id.button3);
        name = findViewById(R.id.editText3);
        email = findViewById(R.id.editText5);
        mobile = findViewById(R.id.editText6);
        password = findViewById(R.id.editText7);
        email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (email.getText().toString().matches(Validations.email) && s.length() > 0) {

                } else {
                    email.setError("Invalid email id");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().isEmpty() || !name.getText().toString().matches(Validations.text)) {
                    name.setError("Please enter your name");
                } else if (mobile.getText().toString().isEmpty() || !mobile.getText().toString().matches(Validations.mobile)) {
                    mobile.setError("Please enter a valid mobile number");
                } else if (email.getText().toString().isEmpty() || !email.getText().toString().matches(Validations.email)) {
                    email.setError("Please enter a valid email address");
                } else if (password.getText().toString().isEmpty()) {
                    password.setError("Please enter your password");
                } else {

                    submitForm();
                }
            }
        });


    }

    public void submitForm() {
        final ProgressDialog progressDoalog = new ProgressDialog(register.this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("User")
                    .whereEqualTo("username", email.getText().toString())
                    .get().
                    addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                addUserToDataBase();
                            } else {
                                progressDoalog.dismiss();
                                Toast.makeText(register.this, "Email id already registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //userRegistration();
                            Toast.makeText(register.this, "Creation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.d("exception", "Exception" + e.toString());
        }
        progressDoalog.dismiss();
    }

    private void addUserToDataBase() {
        final ProgressDialog progressDoalog = new ProgressDialog(register.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        UserModel obj = new UserModel("", name.getText().toString(), mobile.getText().toString(), email.getText().toString(), password.getText().toString(),"User");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        progressDoalog.dismiss();
                        Toast.makeText(register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LOGIN.class));
                        finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDoalog.dismiss();
                        Toast.makeText(register.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LOGIN.class));
        finish();
    }
}
