package com.coderzheaven.uploadimage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "Upload Image";

    // I am using my local server for uploading image, you should replace it with your server address
    public static final String UPLOAD_URL = "http://coderzheaven.com/sample_file_upload/upload_image.php";
    public static final String UPLOAD_URL2 = "http://coderzheaven.com/sample_file_upload/upload_image2.php";

    public static final String UPLOAD_KEY = "upload_image";

    private int PICK_IMAGE_REQUEST = 100;

    private Button btnSelect, btnUpload;
    private TextView txtStatus;

    private ImageView imgView;

    private Bitmap bitmap;

    private Uri filePath;

    private String selectedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgView = (ImageView) findViewById(R.id.imgView);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        btnSelect.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

    }

    Handler handler = handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Handler " + msg.what);
            if(msg.what == 1){
                txtStatus.setText("Upload Success");
            }else{
                txtStatus.setText("Upload Error");
            }
        }

    };

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            selectedFilePath = getPath(filePath);
            Log.i(TAG, " File path : " + selectedFilePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgView.setImageBitmap(bitmap);
                txtStatus.setText("Upload Started...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void uploadImage2(){

        new Thread(new Runnable() {
            public void run() {
                UploadFile uploadFile = new UploadFile(UPLOAD_URL2, selectedFilePath, handler);
                uploadFile.doUpload();
            }
        }).start();

    }

    private void uploadImage() {

        UploadImageApacheHttp uploadTask = new UploadImageApacheHttp();
        uploadTask.doFileUpload(UPLOAD_URL, bitmap, handler);

    }

    @Override
    public void onClick(View v) {
        if (v == btnSelect)
            showFileChooser();
        else
            uploadImage();
    }
}
