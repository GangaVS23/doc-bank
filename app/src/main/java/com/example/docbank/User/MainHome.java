package com.example.docbank.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.docbank.LOGIN;
import com.example.docbank.R;

public class MainHome extends AppCompatActivity {
ImageView afamily,doc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        afamily=findViewById(R.id.afamily);
        doc=findViewById(R.id.docs);
        afamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddFamilyMember.class));
                finish();
            }
        });
        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FamilyMembers.class));
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(MainHome.this);
        alertbox.setMessage("Do you want to logout from this app?");
        alertbox.setTitle("Logout!!");

        alertbox.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity(new Intent(MainHome.this, LOGIN.class));
                finish();

            }
        });
        alertbox.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertbox.show();

    }
}