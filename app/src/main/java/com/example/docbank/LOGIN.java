package com.example.docbank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.docbank.User.ForgotActivity;
import com.example.docbank.User.MainHome;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class LOGIN extends AppCompatActivity {
    TextView a, b, c, d, signup, forgot;
    EditText pass, user;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        pass = findViewById(R.id.editText2);
        user = findViewById(R.id.editText);
        login = findViewById(R.id.button);
        signup = findViewById(R.id.button2);
        forgot = findViewById(R.id.txt_forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotActivity.class));
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.getText().toString().isEmpty()) {
                    user.setError("Please enter your Username");

                }
                if (pass.getText().toString().isEmpty()) {
                    pass.setError("Please enter your Password");

                } else {
                    final ProgressDialog progressDoalog = new ProgressDialog(LOGIN.this);
                    progressDoalog.setMessage("Checking....");
                    progressDoalog.setTitle("Please wait");
                    progressDoalog.setCancelable(true);
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDoalog.show();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    try {

                        db.collection("User").whereEqualTo("username", user.getText().toString()).
                                whereEqualTo("password", pass.getText().toString())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        try {
                                            if (queryDocumentSnapshots.getDocuments().size() == 0) {

                                                Toast.makeText(LOGIN.this, "invalid  credentials", Toast.LENGTH_SHORT).show();
                                            } else {
                                                SharedPreferences sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor;
                                                editor = sp.edit();
                                                editor.putString("utype", queryDocumentSnapshots.getDocuments().get(0).getString("utype"));
                                                editor.putString("username", queryDocumentSnapshots.getDocuments().get(0).getString("name"));
                                                editor.putString("userMobile", queryDocumentSnapshots.getDocuments().get(0).getString("phone"));
                                                editor.putString("userEmail", queryDocumentSnapshots.getDocuments().get(0).getString("username"));
                                                editor.putString("userId", queryDocumentSnapshots.getDocuments().get(0).getId());
                                                editor.commit();
                                                if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("User")) {
                                                    Intent i = new Intent(getApplicationContext(), MainHome.class);
                                                    startActivity(i);
                                                    finish();
                                                    progressDoalog.dismiss();
                                                } else {
                                                    user.setText("");
                                                    pass.setText("");
                                                    Toast.makeText(LOGIN.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                                }


                                            }

                                            progressDoalog.dismiss();
                                        } catch (Exception e) {
                                            progressDoalog.dismiss();
                                            Log.d("exception: ", e.toString());
                                        }


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDoalog.dismiss();

                                        Toast.makeText(LOGIN.this, "Database error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e) {
                        Log.d("exception: ", e.toString());
                    }

                }


            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), register.class);
                startActivity(i);

            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
    }
}
