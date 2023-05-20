package com.example.docbank.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.docbank.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class UploadDocument extends AppCompatActivity {
    EditText spinner, pass;
    String docType = "";
    Button Upload, add;
    TextView decodeimg;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, SELECTPDF = 2;
    private Bitmap bitmapProfile = null;
    private int STORAGE_PERMISSION_CODE = 23;
    boolean somePermissionsForeverDenied = false;
    private String userChosenTask;
    String encodedImage = "", qrstring = "";
    int t1 = 0;
    private static int RESULT_LOAD_IMAGE = 1;
    int newWidth = 500;
    String encodeFileToBase64Binary = null;
    int newHeight = 500;

    Matrix matrix;

    Bitmap resizedBitmap, toDecodeString;

    float scaleWidth;
    Uri filePath = null;
    float scaleHeight;
    String Ftype="";

    ByteArrayOutputStream outputStream;
    public final static int QRcodeWidth = 500;
    private static final String IMAGE_DIRECTORY = "/QRcodeDemonuts";
    Bitmap bitmap;
String ftype[]={"ID Cards","Educational Documents","Others"};
Spinner sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);
        spinner = findViewById(R.id.spinner2);
        pass = findViewById(R.id.passw);
        Upload = findViewById(R.id.button6);
        sp = findViewById(R.id.ftype);
        decodeimg = findViewById(R.id.decodeimg);
        add = findViewById(R.id.button7);
        ArrayAdapter aa=new ArrayAdapter(this, android.R.layout.simple_spinner_item,ftype);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(aa);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ftype=sp.getSelectedItem().toString() ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  decodeimg.setText(encodeFileToBase64Binary);
                if (spinner.getText().toString().isEmpty()) {
                    spinner.setError("Enter Document Name");
                } else if (pass.getText().toString().isEmpty() || pass.getText().toString().length() < 4) {
                    pass.setError("Enter Document 4 digit password");
                } else if (!Upload.getText().equals("uploaded")) {
                    Toast.makeText(UploadDocument.this, "please take a picture", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("@@", encodeFileToBase64Binary);
                    uploadDocuments();

                }

            }
        });
    }

    private void uploadDocuments() {
        String UID;
        SharedPreferences ss = getSharedPreferences("uploadInfo", Context.MODE_PRIVATE);
        if (ss.getString("from", "").equals("member")) {
            UID = ss.getString("mId", "");
        } else {
            SharedPreferences sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);
            UID = sp.getString("userId", "");
        }

        submitForm(UID);
    }

    public void submitForm(String UID) {
        final ProgressDialog progressDoalog = new ProgressDialog(UploadDocument.this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("Documents")
                    .whereEqualTo("docName", spinner.getText().toString().trim())
                    .whereEqualTo("uID",UID)
                    .get().
                    addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                addBoyToDataBase(UID);
                            } else {
                                progressDoalog.dismiss();
                                Toast.makeText(UploadDocument.this, "This Document already added by you", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //userRegistration();
                            Toast.makeText(UploadDocument.this, "Creation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.d("exception", "Exception" + e.toString());
        }
        progressDoalog.dismiss();
    }

    private void addBoyToDataBase(String uid) {
        SharedPreferences sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);
        final ProgressDialog progressDoalog = new ProgressDialog(UploadDocument.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        DocModel obj = new DocModel("", spinner.getText().toString(), pass.getText().toString(), docType, encodeFileToBase64Binary, uid, encodedImage,Ftype);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Documents").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        progressDoalog.dismiss();
                        Toast.makeText(UploadDocument.this, "Document Encrypted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), UploadDocument.class));
                        finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDoalog.dismiss();
                        Toast.makeText(UploadDocument.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectImage() {
        //final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Choose PDF", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload your documents");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    docType = "image";
                    userChosenTask = "Take Photo";
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask = "Choose from Library";
                    docType = "image";
                    galleryIntent();
                } else if (items[item].equals("Choose PDF")) {
                    userChosenTask = "choose PDF";
                    docType = "pdf";
                    pdfIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                    Log.d("dialog dismiss ", "true");
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void pdfIntent() {
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), SELECTPDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data, SELECT_FILE);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == SELECTPDF)
                onSelectFromGalleryResult(data, SELECTPDF);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data, int type) {
        Bitmap bm = null;
        if (data != null) {
            try {
                if (type == 2) {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    String path = saveImage(bm); //give read write permission
                    File dir = Environment.getExternalStorageDirectory();
                    File yourFile = new File(dir, path);
                    encodeFileToBase64Binary = encodeFileToBase64Binary(yourFile);
                    Upload.setText("uploaded");
                    Log.d("@@gallary pdf", encodeFileToBase64Binary + "");
                    Uri newFile = data.getData();
                    uploadImage(newFile);
                } else if (type == 1) {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    int nh = (int) (bm.getHeight() * (512.0 / bm.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bm, 102, nh, true);
                    reZize(bm);
                    Upload.setText("uploaded");
//                    filePath = getImageUri(getApplicationContext(), bm);
//                    uploadImage(filePath);

                   // Log.d("@@gallary image", encodeFileToBase64Binary + "");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void reZize(Bitmap bp){
        int width = bp.getWidth();
        int height = bp.getHeight();
        Matrix matrix = new Matrix();
        scaleWidth = ((float) newWidth) / width;
        scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        resizedBitmap = Bitmap.createBitmap(bp, 0, 0, width, height, matrix, true);
        outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        if (resizedBitmap != null) {
            encodeFileToBase64Binary = getStringImage(resizedBitmap);
        }
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        //Toast.makeText(getContext(), "" + destination, Toast.LENGTH_SHORT).show();
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmapProfile = thumbnail;
        if (bitmapProfile != null) {

           // filePath = getImageUri(getApplicationContext(), bitmapProfile);
            encodeFileToBase64Binary = getStringImage(bitmapProfile);
            Log.d("@@camera image", encodeFileToBase64Binary + "");
            //uploadImage(filePath);
        }


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Upload.setText("uploaded");
        return encodedImage;
    }

    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        int size = (int) fileName.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileName));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return encoded;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //   myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    public static GetFilePathAndStatus getFileFromBase64AndSaveInSDCard(String base64, String filename, String extension) {
        GetFilePathAndStatus getFilePathAndStatus = new GetFilePathAndStatus();
        try {
            byte[] pdfAsBytes = Base64.decode(base64, 0);
            FileOutputStream os;
            os = new FileOutputStream(getReportPath(IMAGE_DIRECTORY + "/" + filename, extension), false);
            os.write(pdfAsBytes);
            os.flush();
            os.close();
            getFilePathAndStatus.filStatus = true;
            getFilePathAndStatus.filePath = getReportPath(filename, extension);
            return getFilePathAndStatus;
        } catch (IOException e) {
            e.printStackTrace();
            getFilePathAndStatus.filStatus = false;
            getFilePathAndStatus.filePath = getReportPath(filename, extension);
            return getFilePathAndStatus;
        }
    }

    public static String getReportPath(String filename, String extension) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "ParentFolder/Report");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + filename + "." + extension);
        return uriSting;

    }

    public static class GetFilePathAndStatus {
        public boolean filStatus;
        public String filePath;

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImage(Uri path) {
        Log.d("## filepath: inside", path + "");
        if (path != null) {
            final ProgressDialog progressDoalog = new ProgressDialog(UploadDocument.this);
            progressDoalog.setMessage("Encrypting....");
            progressDoalog.setTitle("Please wait");
            progressDoalog.setCancelable(true);
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDoalog.show();
            Log.d("## filepath:", path + "");

//            final String timestamp = "" + System.currentTimeMillis();
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//            final String messagePushID = timestamp;
//            Toast.makeText(UploadDocument.this, path.toString(), Toast.LENGTH_SHORT).show();
//
//            // Here we are uploading the pdf in firebase storage with the name of current time
//            final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
//            Toast.makeText(UploadDocument.this, filepath.getName(), Toast.LENGTH_SHORT).show();
//            filepath.putFile(path).continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return filepath.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        progressDoalog.dismiss();
//                        // After uploading is done it progress
//                        // dialog box will be dismissed
//                      //  dialog.dismiss();
//                        Uri uri = task.getResult();
//                        String myurl;
//                        myurl = uri.toString();
//                        Toast.makeText(UploadDocument.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        progressDoalog.dismiss();
//                        //dialog.dismiss();
//                        Toast.makeText(UploadDocument.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });


            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            storageReference = storageReference.child("encrypted_Images/" + UUID.randomUUID().toString());
            UploadTask uploadTask = storageReference.putFile(path);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String uploadedImageUrl = task.getResult().toString();
                            Log.d("##", uploadedImageUrl);
                            encodedImage = uploadedImageUrl;
                            progressDoalog.dismiss();
                        }
                    });
                }
            });

        } else {
            Toast.makeText(UploadDocument.this, "Please Upload an Image", Toast.LENGTH_SHORT).show();

        }
    }
}