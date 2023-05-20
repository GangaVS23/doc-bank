package com.example.docbank.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.docbank.R;
import com.example.docbank.User.Adapter.DocAdapter;
import com.example.docbank.User.Adapter.MemberAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DecryptDocuments extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText sdoc;
    DocAdapter adapter;
    String key = "", value = "";
    List<DocModel> usersList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_documents);
        String UID;
        SharedPreferences ss = getSharedPreferences("uploadInfo", Context.MODE_PRIVATE);
        if (ss.getString("from", "").equals("member")) {
            UID = ss.getString("mId", "");
            key = ss.getString("ftype", "");
        } else {
            SharedPreferences  sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);
            UID = sp.getString("userId", "");
            key = ss.getString("ftype", "");
        }
        recyclerView = findViewById(R.id.rvdoc);
        sdoc = findViewById(R.id.sdoc);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ll);
        showData(UID);
    }

    private void showData(String UID) {
        final ProgressDialog progressDoalog = new ProgressDialog(DecryptDocuments.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        usersList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Documents")
                .whereEqualTo("uID", UID)
                .whereEqualTo("fname", key)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i;
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                usersList.add(new DocModel(queryDocumentSnapshots.getDocuments().get(i).getId(),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("docName"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("docPassword"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("docType"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("encDoc"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("uId"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("encodedUrl"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("Fname")));
                                Log.d("@@", queryDocumentSnapshots.getDocuments().get(i).getString("docType"));
                            }
                            adapter = new DocAdapter(getApplicationContext(), usersList);
                            sdoc.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    //after the change calling the method and passing the search input
                                    adapter.getFilter().filter(editable.toString());

                                }
                            });
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(DecryptDocuments.this, "No Documents Added for this member", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DecryptDocuments.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SelectFolder.class));
        finish();
    }
}