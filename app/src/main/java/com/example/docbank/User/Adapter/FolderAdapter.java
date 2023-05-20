package com.example.docbank.User.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docbank.R;
import com.example.docbank.User.DecryptDocuments;
import com.example.docbank.User.DocModel;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
    Context context, a;
    List<DocModel> ulist;
    String id, action = "";
String uid;
    public FolderAdapter(Context context, List<DocModel> userlist, String UID) {
        this.context = context;
        this.ulist = userlist;
        this.uid = UID;
    }

    @NonNull
    @Override
    public FolderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_folder, viewGroup, false);

        return new FolderAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter.MyViewHolder myViewHolder, int i) {
        final DocModel u = ulist.get(i);
        SharedPreferences sp = context.getSharedPreferences("logDetails", Context.MODE_PRIVATE);

        myViewHolder.name.setText( u.getFname());

        myViewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences ss=a.getSharedPreferences("uploadInfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor ed=ss.edit();
                ed.putString("ftype",u.getFname());
                ed.putString("from",uid);
                ed.commit();
                Intent i = new Intent(context, DecryptDocuments.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        });

    }



    @Override
    public int getItemCount() {
        return ulist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, name, email, phone;
        ConstraintLayout card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.fldname);

            a = itemView.getContext();
            card = itemView.findViewById(R.id.card);
        }
    }




}


