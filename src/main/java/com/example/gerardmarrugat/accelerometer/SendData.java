package com.example.gerardmarrugat.accelerometer;


import android.app.Activity;
import android.content.Entity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gerard Marrugat on 05/01/2016.
 */

public class SendData extends AsyncTask<String, Void, Void> {

    private String url_server;

    private static final String DataTAG = "DataTAG";


    @Override
    protected Void doInBackground(String... params) {

        //Log.v(DataTAG, params[0].toString());

        // the file to be posted
        String textFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Acceleration Data/" + params[0];
        Log.v(DataTAG, "textFile: " + textFile);

        // the URL where the file will be posted
        String postReceiverUrl = "http://mobilitapp.noip.me/csv/post_date_receiver.php";
        Log.v(DataTAG, "postURL: " + postReceiverUrl);


        HttpClient httpClient = new DefaultHttpClient();

        //Define the url in the post object
        HttpPost httpPost = new HttpPost(postReceiverUrl);

        //File file = new File(textFile);
        //FileBody fileBody = new FileBody(file);

        //Log.v(DataTAG, "file: " + fileBody.toString());

        //MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        //reqEntity.addPart("file", params[0]);
        //httpPost.setEntity(reqEntity);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("filename", textFile));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // execute HTTP post request
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();

        if (resEntity != null) {

            String responseStr = null;
            try {
                responseStr = EntityUtils.toString(resEntity).trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v(DataTAG, "Response: " +  responseStr);

            // you can add an if statement here and do other actions based on the response
        }


        return null;
    }
}

        //Creates an entity that contains the file we want to upload to the server
        //FileEntity reqentity = new FileEntity(params[0],"text/plain");

/*
        try {
            Log.v(DataTAG, "Entity content" + reqentity.getContent().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Define the request entity in the post object
        httpPost.setEntity(reqentity);



        try {

            HttpResponse httpResponse = httpClient.execute(httpPost);

            Log.v(DataTAG,"Transfer Done");

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            //do something with httpResponse

            final StringBuilder out = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                out.append(line + "\n");
                Log.v(DataTAG,"out" + out.toString());
            }

            rd.close();

            Log.d(DataTAG, "serverResponse: " + out.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}*/
