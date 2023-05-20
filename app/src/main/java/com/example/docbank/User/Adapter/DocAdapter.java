package com.example.docbank.User.Adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docbank.R;
import com.example.docbank.User.DecryptDocuments;
import com.example.docbank.User.DecryptedDocView;
import com.example.docbank.User.DocModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.MyViewHolder> implements Filterable {
    Context context, a;
    List<DocModel> ulist;
    String id, action = "";
    List<DocModel> exampleListFull;
    public DocAdapter(Context context, List<DocModel> userlist) {
        this.context = context;
        this.ulist = userlist;
        exampleListFull = new ArrayList<>(userlist);
    }

    @NonNull
    @Override
    public DocAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_doc, viewGroup, false);

        return new DocAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull DocAdapter.MyViewHolder myViewHolder, int i) {
        final DocModel u = ulist.get(i);
        SharedPreferences sp = context.getSharedPreferences("logDetails", Context.MODE_PRIVATE);

        myViewHolder.dname.setText(u.getDocName());
        myViewHolder.dpass.setText("****");
        myViewHolder.did.setText(u.getDocId());
        myViewHolder.dtype.setText(u.getDocType());
        myViewHolder.icdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert2(u.getDocId(), v);
            }
        });
        myViewHolder.fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert3(u.getDocId(),v);
            }
        });
        myViewHolder.btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myViewHolder.cvv.getText().toString().isEmpty() || myViewHolder.cvv.getText().toString().length() < 4) {
                    myViewHolder.cvv.setError("Enter your password");
                } else if (myViewHolder.cvv.getText().toString().equals(u.getDocPassword())) {

                    Alert(u.getEncDoc(), u.getDocType(), u.getEncodedUrl());


                } else {
                    Toast.makeText(a, "Password not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return ulist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dname, dpass, dtype, did;
        EditText cvv;
        Button btnpay;
        ImageView icdelete, fpass;

        CardView card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dname = itemView.findViewById(R.id.dname);
            dpass = itemView.findViewById(R.id.dpass);
            dtype = itemView.findViewById(R.id.dtype);
            did = itemView.findViewById(R.id.dID);
            cvv = itemView.findViewById(R.id.cvv);
            btnpay = itemView.findViewById(R.id.btnpay);
            a = itemView.getContext();
            card = itemView.findViewById(R.id.card);
            icdelete = itemView.findViewById(R.id.imageView3);
            fpass = itemView.findViewById(R.id.fpass);
        }
    }


    private void Alert(String encDoc, String docType, String downloadUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setMessage("what to do?");
        alertDialogBuilder.setPositiveButton("Decrypt Document",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (docType.equals("image")) {
                            Intent i = new Intent(context, DecryptedDocView.class);
                            Bundle b = new Bundle();
                            b.putString("encdoc", encDoc);
                            b.putString("dUrl", downloadUrl);
                            i.putExtras(b);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(i);
                        } else {
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(downloadUrl));
                            a.startActivity(Intent.createChooser(i, "Title"));
                        }

                    }
                });

        AlertDialog albox = alertDialogBuilder.create();
        albox.show();

    }

    private void Alert2(String docId, View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setMessage("Do you want to delete this document?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteAccount(docId, v);
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog albox = alertDialogBuilder.create();
        albox.show();

    }
    private void Alert3(String docId, View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setMessage("Are you forgot your password?do you wish to upadte your password for this document?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                            final Dialog dialog = new Dialog(a);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.custom_dialogue);

                            EditText text =  dialog.findViewById(R.id.npass);

                            Button dialogButton = (Button) dialog.findViewById(R.id.btnchange);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(text.getText().toString().isEmpty()||text.getText().toString().length()<4 ){
                                        text.setError("Enter 4 digit  pin number");
                                    }else{
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("Documents").document(docId).
                                                update("docPassword",text.getText().toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(a, "Password Updated successfully", Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(context, DecryptDocuments.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        context.startActivity(i);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("bgloc", "onFailure: ");
                                                        Toast.makeText(a, e+"updation failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        dialog.dismiss();
                                    }

                                }
                            });

                            dialog.show();

                        }

                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog albox = alertDialogBuilder.create();
        albox.show();

    }

    private void deleteAccount(String uID, View v) {
        //Log.d("@", "showData: Called")

        final ProgressDialog progressDoalog = new ProgressDialog(v.getRootView().getContext());
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Documents").document(uID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getRootView().getContext(), "Document removed successfully", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(context, DecryptDocuments.class);
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
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DocModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DocModel item : exampleListFull) {

                    if (item.getDocName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }

                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ulist.clear();
            ulist.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}



