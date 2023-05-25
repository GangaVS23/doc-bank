package com.example.docbank.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.docbank.LOGIN;
import com.example.docbank.R;
import com.example.docbank.register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AddFamilyMember extends AppCompatActivity {
    EditText mname, mrelation;
    Button btnadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);
        mname = findViewById(R.id.mname);
        mrelation = findViewById(R.id.mrelation);
        btnadd = findViewById(R.id.btnadd);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mname.getText().toString().isEmpty()) {
                    mname.setError("Enter name");
                } else if (mrelation.getText().toString().isEmpty()) {
                    mrelation.setError("Enter relation");
                } else {

                    submitForm();
                }
            }
        });

    }

    public void submitForm() {
        final ProgressDialog progressDoalog = new ProgressDialog(AddFamilyMember.this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("FamilyMember")
                    .whereEqualTo("name", mname.getText().toString())
                    .get().
                    addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                addFMemberToDataBase();
                            } else {
                                progressDoalog.dismiss();
                                Toast.makeText(AddFamilyMember.this, "This Member already registered by you", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //userRegistration();
                            Toast.makeText(AddFamilyMember.this, "Creation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.d("exception", "Exception" + e.toString());
        }
        progressDoalog.dismiss();
    }

    private void addFMemberToDataBase() {
        SharedPreferences sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);

        final ProgressDialog progressDoalog = new ProgressDialog(AddFamilyMember.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        MemberModel obj = new MemberModel("", mname.getText().toString(), mrelation.getText().toString(),sp.getString("userId",""));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FamilyMember").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        progressDoalog.dismiss();
                        Toast.makeText(AddFamilyMember.this, "Member Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainHome.class));
                        finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDoalog.dismiss();
                        Toast.makeText(AddFamilyMember.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainHome.class));
        finish();
    }
}