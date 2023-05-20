package com.example.docbank.User.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docbank.R;
import com.example.docbank.User.DecryptDocuments;
import com.example.docbank.User.FamilyMembers;
import com.example.docbank.User.MemberModel;
import com.example.docbank.User.SelectFolder;
import com.example.docbank.User.UploadDocument;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
    Context context, a;
    List<MemberModel> ulist;
    String id, action = "";

    public MemberAdapter(Context context, List<MemberModel> userlist) {
        this.context = context;
        this.ulist = userlist;
    }

    @NonNull
    @Override
    public MemberAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_member, viewGroup, false);

        return new MemberAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.MyViewHolder myViewHolder, int i) {
        final MemberModel u = ulist.get(i);
        SharedPreferences sp = context.getSharedPreferences("logDetails", Context.MODE_PRIVATE);

        myViewHolder.name.setText( u.getName());
        myViewHolder.email.setText( u.getRelation());
        myViewHolder.phone.setText(u.getMid());
        myViewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert(u.getMid(),v);
            }
        });

    }



    @Override
    public int getItemCount() {
        return ulist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, name, email, phone;
        CardView card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.mname);
            email = itemView.findViewById(R.id.mrelation);
            phone = itemView.findViewById(R.id.mId);
            a = itemView.getContext();
            card = itemView.findViewById(R.id.card);
        }
    }


    private void Alert(String mid, View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setMessage("what to do?");
        alertDialogBuilder.setPositiveButton("Encrypt Document",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences ss=a.getSharedPreferences("uploadInfo",Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed=ss.edit();
                        ed.putString("from","member");
                        ed.putString("mId",mid);
                        ed.commit();
                        Intent i = new Intent(context, UploadDocument.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);
                    }
                });

        alertDialogBuilder.setNegativeButton("Decode document", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences ss=a.getSharedPreferences("uploadInfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor ed=ss.edit();
                ed.putString("from","member");
                ed.putString("mId",mid);
                ed.commit();
                Intent i = new Intent(context, SelectFolder.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        });
        alertDialogBuilder.setNeutralButton("delete this member", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember(mid,v);
            }
        });

        AlertDialog albox = alertDialogBuilder.create();
        albox.show();

    }
    private void deleteMember(String uID, View v) {
        //Log.d("@", "showData: Called")

        final ProgressDialog progressDoalog = new ProgressDialog(v.getRootView().getContext());
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FamilyMember").document(uID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getRootView().getContext(), "Document removed successfully", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(context, FamilyMembers.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(i);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getRootView().getContext(), "Technical error occured", Toast.LENGTH_SHORT).show();

                    }
                });

        progressDoalog.dismiss();

    }

}


