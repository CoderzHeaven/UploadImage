package com.coderzheaven.uploadimage;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UploadImageApacheHttp {

    public static final String TAG = "Upload Image Apache";

    public void doFileUpload(final String url, final Bitmap bmp, final Handler handler){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "Starting Upload...");
                final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("image", convertBitmapToString(bmp)));

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    String responseStr = EntityUtils.toString(response.getEntity());
                    Log.i(TAG, "doFileUpload Response : " + responseStr);
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    System.out.println("Error in http connection " + e.toString());
                    handler.sendEmptyMessage(0);
                }
            }
        });
        t.start();

    }

    public String convertBitmapToString(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte[] byte_arr = stream.toByteArray();
        String imageStr = Base64.encodeBytes(byte_arr);
        return imageStr;
    }
}
