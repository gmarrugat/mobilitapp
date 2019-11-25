package com.example.gerardmarrugat.accelerometer;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.opencsv.CSVWriter;

import android.os.Process;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Gerard Marrugat on 12/02/2016.
 */


public class Sensor_Listener extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    private long start_time, currentTime;

    double x, y, z, x_gyro, y_gyro, z_gyro;

    String timestamp;

    private static final String MyTAG = "MyTAG";

    private boolean first_sample = true;
    private boolean first_sample_gyro = true;

    private int position = 0;
    private int position_gyro = 0;

    //Specify the object type which will be stored in the ArrayList
    private ArrayList<SensorData> AccelerationData = new ArrayList<SensorData>();

    //Specify the object type which will be stored in the ArrayList
    private ArrayList<SensorData> GyroscopeData = new ArrayList<SensorData>();

    private final float THERESLHOLD = (float) 0.4;

    private final float THERESLHOLD_GYRO = (float) 0.05;

    private File csv_file;

    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private PowerManager.WakeLock mWakeLock = null;


    /*
     * Register this as a sensor event listener.
     */
    private void registerListener() {
        // Instance the accelerometer and gyroscope
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // registerListener(EventListener, Sensor, rate)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // Instance the SENSOR_SERVICE
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        registerListener();

        first_sample = true;
        first_sample_gyro = true;

        // Creates an arraylist where the accelerometer data will be stored
        AccelerationData = new ArrayList<SensorData>();

        // Creates an arraylist where the accelerometer data will be stored
        GyroscopeData = new ArrayList<SensorData>();

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MyTAG);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterListener();

        mWakeLock.release();
        stopForeground(true);

        //Stops timerRunnable
        timerHandler.removeCallbacks(timerRunnable);

        new DataStorage().execute(AccelerationData);

        Log.v(MyTAG, "AccelerationData_OK");

        new DataStorage().execute(GyroscopeData);

        Log.v(MyTAG, "GyroscopeData_OK");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Moment in which the accelerometer is going to store data
        start_time = System.currentTimeMillis();

        //call the run method of timerRunnable
        timerHandler.post(timerRunnable);

        mWakeLock.acquire();
        startForeground(Process.myPid(), new Notification());


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            double deltaX = 0, deltaY = 0, deltaZ = 0;

            deltaX = x - event.values[0];
            deltaY = y - event.values[1];
            deltaZ = z - event.values[2];

            //alpha is the LPF constant, alpha = t/(t + dT); t = LPF time constant, dT = time interval between input data
            final float alpha = (float) 0.8;

            if (deltaX > THERESLHOLD || deltaY > THERESLHOLD || deltaZ > THERESLHOLD) {
                //Define a thereshold that identifies the end of the acceleration period

                //We collect data from acceleration peaks but we haven´t marked the end of each peak yet

                if (first_sample) {

                    timestamp = time(currentTime, "mm:ss:SSS");
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];

                    first_sample = false;
                    position++;

                } else {

                    //We use a low pass filter to remove short-terme fluctuations(Jitter) on our samples
                    timestamp = time(currentTime, "mm:ss:SSS");
                    x = alpha * event.values[0] + (1 - alpha) * AccelerationData.get(position - 1).getX();
                    y = alpha * event.values[1] + (1 - alpha) * AccelerationData.get(position - 1).getY();
                    z = alpha * event.values[2] + (1 - alpha) * AccelerationData.get(position - 1).getZ();

                    position++;
                }


                //When the accelerometer receives new values a SensorData object is created with these values, after that this object will be
                // stored in an array

                SensorData data_accelerometer = new SensorData(timestamp, x, y, z);

                AccelerationData.add(data_accelerometer);

                Log.v(MyTAG, "Collecting Accelerometer´s Data");

            }
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            double deltaX_gyro = 0, deltaY_gyro = 0, deltaZ_gyro = 0;

            deltaX_gyro = x_gyro - event.values[0];
            deltaY_gyro = y_gyro - event.values[1];
            deltaZ_gyro = z_gyro - event.values[2];

            //alpha is the LPF constant, alpha = t/(t + dT); t = LPF time constant, dT = time interval between input data
            final float alpha = (float) 0.8;

            if (deltaX_gyro > THERESLHOLD_GYRO || deltaY_gyro > THERESLHOLD_GYRO || deltaZ_gyro > THERESLHOLD_GYRO) {
                //Define a thereshold that identifies the end of the acceleration period

                if (first_sample_gyro) {

                    timestamp = time(currentTime, "mm:ss:SSS");
                    x_gyro = event.values[0];
                    y_gyro = event.values[1];
                    z_gyro = event.values[2];

                    first_sample_gyro = false;
                    position_gyro++;

                } else {

                    //We use a low pass filter to remove short-terme fluctuations(Jitter) on our samples
                    timestamp = time(currentTime, "mm:ss:SSS");
                    x_gyro = alpha * event.values[0] + (1 - alpha) * GyroscopeData.get(position_gyro - 1).getX();
                    y_gyro = alpha * event.values[1] + (1 - alpha) * GyroscopeData.get(position_gyro - 1).getZ();
                    z_gyro = alpha * event.values[2] + (1 - alpha) * GyroscopeData.get(position_gyro - 1).getZ();

                    position_gyro++;
                }

                //When the accelerometer receives new values a SensorData object is created with these values, after that this object will be
                // stored in an array

                SensorData data_gyro = new SensorData(timestamp, x_gyro, y_gyro, z_gyro);

                GyroscopeData.add(data_gyro);

                Log.v(MyTAG, "Collecting Gyroscope´s Data");

            }
        }

    }


    //Gives format to a date
    //Creates a format class with a specific format pattern
    public static String time(long milliSeconds, String dateFormat) {

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        //Get the current date and time
        Calendar calendar = Calendar.getInstance();
        //Converts the time into milliseconds
        calendar.setTimeInMillis(milliSeconds);
        String time = formatter.format(calendar.getTime()).toString();
        return time;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            //this method emulates a timer

            currentTime = System.currentTimeMillis() - start_time;

            //Very Important! this Handler method calls the runnable again
            timerHandler.post(this);

        }
    };


    private class DataStorage extends AsyncTask<ArrayList<SensorData>, String, String> {

        private String directory_name;
        public StringBuilder filename = new StringBuilder();
        private final String Header[] = {"timestamp", "x_acceleration", "y_acceleration", "z_acceleration"};
        private final String Header_gyro[] = {"timestamp", "x_rate", "y_rate", "z_rate"};
        private String timestamp = null;
        private double x = 0.0;
        private double y = 0.0;
        private double z = 0.0;
        private SensorData data, data_gyro;
        private String date;


        @Override
        protected String doInBackground(ArrayList<SensorData>... params) {
            //get the arraylist of accelerometer data and save it in a file

            //Gives a name to the file, date is included in the name´s file
            date = time(System.currentTimeMillis(), "dd-MM-yyyy");
            //Look which kind of data it is;  Acceleration or Gyroscope
            if (params[0].equals(AccelerationData)) {

                filename.append("acceleration_");
                filename.append(date);
                filename.append(".csv");

            }

            if (params[0].equals(GyroscopeData)) {

                filename.append("gyroscope_");
                filename.append(date);
                filename.append(".csv");

            }

            // Creates a directory for acceleration files if it´s not yet created
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/MobilitApp/");
            if (!dir.exists()) {
                dir.mkdirs();
                //publishProgress("Directory created");
            } else {
                //publishProgress("Directory already exists");
            }


            //Creates the file inside the directory folder
            directory_name = dir.toString();
            csv_file = new File(directory_name, filename.toString());

            Log.v("TAG", "directory_name: " + directory_name);
            Log.v("TAG", "filename: " + filename.toString());
            Log.v("TAG", "filename: " + directory_name + "/" + filename.toString());

            try {
                csv_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //Creates the .csv file and store all the acceleration data or gyroscope data
            try {

                if (params[0].equals(AccelerationData)) {
                    CSVWriter writer = new CSVWriter(new FileWriter(csv_file));

                    writer.writeNext(Header);

                    data = new SensorData(timestamp, x, y, z);

                    int length = params[0].size();

                    for (int i = 0; i < length; i++) {

                        data = params[0].get(i);

                        timestamp = data.getTimestamp();
                        x = data.getX();
                        y = data.getY();
                        z = data.getZ();

                        //Only 3 first decimals are stored
                        writer.writeNext(new String[]{timestamp, String.format("%.3f", x), String.format("%.3f", y), String.format("%.3f", z)});

                    }

                    writer.close();
                    Log.v(MyTAG, "AccelerometerData written");


                }

                if (params[0].equals(GyroscopeData)) {
                    CSVWriter writer = new CSVWriter(new FileWriter(csv_file));

                    writer.writeNext(Header_gyro);

                    data_gyro = new SensorData(timestamp, x, y, z);

                    int length = params[0].size();

                    for (int i = 0; i < length; i++) {

                        data_gyro = params[0].get(i);

                        timestamp = data_gyro.getTimestamp();
                        x = data_gyro.getX();
                        y = data_gyro.getY();
                        z = data_gyro.getZ();

                        //Only 3 first decimals are stored
                        writer.writeNext(new String[]{timestamp, String.format("%.3f", x), String.format("%.3f", y), String.format("%.3f", z)});

                    }

                    writer.close();
                    Log.v(MyTAG, "GyroscopeData written");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.v(MyTAG, "DataStorage_OK");
            UploadFile filetoupload = new UploadFile(csv_file.toString());
            Log.v("TAG", csv_file.toString());

            Thread UploadFile = new Thread(filetoupload);
            UploadFile.start();

            while(UploadFile.getState() != Thread.State.TERMINATED){

            }

            Log.v("TAG","Ahora pude borrar los ficheros");

            File delete_file = new File(csv_file.toString());
            Boolean delete = delete_file.delete();

            if (delete) {
                Log.v("TAG", csv_file.toString() + " has been deleted");

            }

            //uploadFile(csv_file.toString());

            return "Data Uploaded to Mobilitapp Server";

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //message_info.setText(values[0]);

        }


        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

        }

    }

}




