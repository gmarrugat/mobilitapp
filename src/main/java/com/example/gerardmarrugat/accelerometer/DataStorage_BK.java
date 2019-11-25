package com.example.gerardmarrugat.accelerometer;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Gerard Marrugat on 09/11/2015.
 */
public class DataStorage_BK extends AsyncTask<ArrayList<SensorData>,String,String>{

    public String directory_name;
    public String filename;
    private static final String Header[] = {"timestamp","x_acceleration","y_acceleration","z_acceleration"};
    private String timestamp = null;
    private double x = 0.0;
    private double y = 0.0;
    private double z = 0.0;
    private SensorData data;
    private String date;

    @Override
    protected String doInBackground(ArrayList<SensorData>... params) {
        //get the arraylist of accelerometer data and save it in a file

        date = time(System.currentTimeMillis(),"dd/MM/yyyy");

        filename = "acceleration.csv";


        // Creates a directory for accelertion files if it´s not created yet
       File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/Acceleration Data/");
        if(!dir.exists()) {
            dir.mkdirs();
            publishProgress("Directory created");
        }
        else{
            publishProgress("Directory already exists");
        }

        //Creates the file inside the directory folder
        directory_name = dir.toString();
       File file = new File(directory_name,filename);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Creates the .csv file and store all the acceleration data
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            writer.writeNext(Header);

            data = new SensorData(timestamp,x,y,z);

            int length = params[0].size();

            for(int i=0; i < length; i++){

                data = params[0].get(i);

                timestamp = data.getTimestamp();
                x = data.getX();
                y = data.getY();
                z = data.getZ();

                //Only 3 first decimals are stored
                writer.writeNext(new String[] {timestamp, String.format("%.3f", x), String.format("%.3f", y), String.format("%.3f", z)});

            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "Saved Data";

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }


    public static String time(long milliSeconds,String dateFormat){
        //Gives format to a date

        //Creates a format class with a specific format pattern
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        //Get the current date and time
        Calendar calendar = Calendar.getInstance();
        //Converts the time into milliseconds
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

}
