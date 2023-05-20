package com.example.docbank.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docbank.R;
import com.example.docbank.User.Adapter.MemberAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FamilyMembers extends AppCompatActivity {
    RecyclerView recyclerView;
    MemberAdapter adapter;
    String key = "", value = "";
    List<MemberModel> usersList = new ArrayList();
    CardView oCard;
    TextView oname, Oid;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_members);
        recyclerView = findViewById(R.id.rvpay);
        oCard = findViewById(R.id.Ocard);
        oname = findViewById(R.id.oname);
        Oid = findViewById(R.id.oId);
        sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);
        oname.setText(sp.getString("username", "") + "(Owner)");
        Oid.setText(sp.getString("userId", ""));
        oCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert();
            }
        });
        LinearLayoutManager ll = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ll);
        showData();
    }

    private void Alert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FamilyMembers.this);
        alertDialogBuilder.setMessage("Select an option?");
        alertDialogBuilder.setPositiveButton("Upload Document",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences ss=getSharedPreferences("uploadInfo",Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed=ss.edit();
                        ed.putString("from","owner");
                        ed.commit();
                        Intent i = new Intent(getApplicationContext(), UploadDocument.class);
                        startActivity(i);
                    }
                });

        alertDialogBuilder.setNegativeButton("Get Saved Documents", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences ss=getSharedPreferences("uploadInfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor ed=ss.edit();
                ed.putString("from","owner");
                ed.commit();
                Intent i = new Intent(getApplicationContext(), SelectFolder.class);
                startActivity(i);
            }
        });

        AlertDialog albox = alertDialogBuilder.create();
        albox.show();

    }

    private void showData() {
        final ProgressDialog progressDoalog = new ProgressDialog(FamilyMembers.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        usersList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("FamilyMember")
                .whereEqualTo("uId", sp.getString("userId", ""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i;
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                usersList.add(new MemberModel(queryDocumentSnapshots.getDocuments().get(i).getId(),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("name"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("relation"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uId")
                                ));
                            }
                            adapter = new MemberAdapter(getApplicationContext(), usersList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(FamilyMembers.this, "No Members Added yet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FamilyMembers.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainHome.class));
        finish();
    }
}