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
import com.example.docbank.User.Adapter.FolderAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectFolder extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText sdoc;
    FolderAdapter adapter;
    String key = "", value = "";
    List<DocModel> usersList = new ArrayList();
String type=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);
        //String UID;
        SharedPreferences ss = getSharedPreferences("uploadInfo", Context.MODE_PRIVATE);
        if (ss.getString("from", "").equals("member")) {
            //UID = ss.getString("mId", "");
            type="member";
        } else {
            SharedPreferences sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);
          //  UID = sp.getString("userId", "");
            type="owner";
        }
        recyclerView = findViewById(R.id.rvdoc);

        LinearLayoutManager ll = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(ll);
        usersList.add(new DocModel("",
               "","","","","","",
                "ID Cards"));
        usersList.add(new DocModel("",
                "","","","","","",
                "Educational Documents"));
        usersList.add(new DocModel("",
                "","","","","","",
                "Others"));
        adapter = new FolderAdapter(getApplicationContext(), usersList,type);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), FamilyMembers.class));
        finish();
    }
}