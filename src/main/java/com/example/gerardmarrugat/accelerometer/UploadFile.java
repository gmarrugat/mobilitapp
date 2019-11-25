package com.example.gerardmarrugat.accelerometer;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Gerard Marrugat on 04/02/2016.
 */
public class UploadFile implements Runnable {

    private static final String MyTAG = "MyTAG";
    private String filePath;

    public UploadFile(String filePath){

        this.filePath = filePath;

    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String urlServer = "http://mobilitapp.noip.me/csv/post_date_receiver.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try {

            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            Log.v(MyTAG, "fileInput Stream bien");
            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs over the connection
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + filePath + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize); // Math.min: Returns the most negative (closest to negative infinity) of the two arguments
            buffer = new byte[bufferSize];

            // Read file. Read the content inside fileInputStream up to buffersize and put it in buffer
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            // This code is executed only once because in the command   'outputStream.write(buffer, 0, bufferSize)'
            // is specified the amount of bytes that have to be written and this is all the buffer
            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                Log.v(MyTAG,"output Stream bien");
            }



            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();


            fileInputStream.close();
            outputStream.flush(); //Get the outputStream empty
            outputStream.close();

            Log.v(MyTAG, "fin");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
