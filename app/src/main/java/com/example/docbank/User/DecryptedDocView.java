package com.example.docbank.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.docbank.R;

public class DecryptedDocView extends AppCompatActivity {
ImageView dImg;
Button dwn,print;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypted_doc_view);
        dImg=findViewById(R.id.dImg);
        dwn=findViewById(R.id.dbtn);
        print=findViewById(R.id.print);
        Bundle b=getIntent().getExtras();
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintHelper photoPrinter = new PrintHelper(DecryptedDocView.this);
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                //Bitmap bitmap = imageView.getDrawingCache(  );
                Bitmap bitmap = ((BitmapDrawable) dImg.getDrawable()).getBitmap();
                photoPrinter.printBitmap("test print",bitmap);
            }
        });


        byte[] decodedString = Base64.decode(b.getString("encdoc"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        dImg.setImageBitmap(decodedByte);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), DecryptDocuments.class));
        finish();
    }
}