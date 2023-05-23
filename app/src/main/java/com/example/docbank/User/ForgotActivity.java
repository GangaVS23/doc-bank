package com.example.docbank.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.docbank.LOGIN;
import com.example.docbank.R;
import com.example.docbank.Validations;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class ForgotActivity extends AppCompatActivity {
EditText ph,otp,npass;
    String Otp;
    Button btnSend,btnVerify,btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        ph=findViewById(R.id.etphone);
        otp=findViewById(R.id.etotp);
        npass=findViewById(R.id.etpass);
        btnSend=findViewById(R.id.btnsendOtp);
        btnVerify=findViewById(R.id.btnVerify);
        btnUpdate=findViewById(R.id.btnUpdate);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp.getText().toString().isEmpty()){
                    otp.setError("enter otp");
                }
                else{
                    if(otp.getText().toString().equals( Otp )){
                        btnVerify.setEnabled(false);
                        btnVerify.setText("Verified");
                    }else{
                        Toast.makeText(ForgotActivity.this, "Otp does not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(npass.getText().toString().isEmpty()){
                    npass.setError("Enter new password");
                }else{final ProgressDialog progressDoalog = new ProgressDialog(ForgotActivity.this);
                    progressDoalog.setMessage("Checking....");
                    progressDoalog.setTitle("Please wait");
                    progressDoalog.setCancelable(true);
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDoalog.show();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    try {

                        db.collection("User").whereEqualTo("phone", ph.getText().toString())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        try {
                                            if (queryDocumentSnapshots.getDocuments().size() == 0) {

                                                Toast.makeText(ForgotActivity.this, "Invalid  Phone Number", Toast.LENGTH_SHORT).show();
                                            } else {

                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("User").document(queryDocumentSnapshots.getDocuments().get(0).getId()).
                                                        update("password",npass.getText().toString())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getApplicationContext(), "Password Updated successfully", Toast.LENGTH_SHORT).show();
                                                                Intent i = new Intent(getApplicationContext(), LOGIN.class);
                                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(i);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("bgloc", "onFailure: ");
                                                                Toast.makeText(getApplicationContext(), e+"updation failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

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

                                        Toast.makeText(ForgotActivity.this, "DB error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e) {
                        Log.d("exception: ", e.toString());
                    }

                }


            }


        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ph.getText().toString().isEmpty()|| !ph.getText().toString().matches(Validations.mobile)){
                    ph.setError("Enter Phone 10 digit phone Number");
                }else{
                    try {
                        Random rand = new Random();
                        Otp = String.format("%04d", rand.nextInt(100000));
                        Log.d("@@",Otp);
                        sendSMS(ph.getText().toString(), "Your login pin Reset Otp is:" + Otp);
                        btnSend.setEnabled(false);
                        btnSend.setText("OTP Sent");

                    } catch (Exception e) {
                        //progressDoalog.dismiss();
                        Log.d("exception: ", e.toString());
                    }
                }
            }
        });
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}