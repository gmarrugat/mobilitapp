package com.example.gerardmarrugat.accelerometer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Gerard Marrugat on 20/10/2015.
 */


// DataScreen
// Listen, shows and stores the accelerometer´s data
public class DataScreen extends Activity {

    //Declare attributes of the class which should be visible from each part of the class
    //Intern attributes of each method aren´t visible from other parts of the code inside the class

    private TextView message_info;

    private long start_time, currentTime;

    private static final String MyTAG = "MyTAG";


    //Timer
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            //this method emulates a timer

            currentTime = System.currentTimeMillis() - start_time;

            TextView timerText = (TextView) findViewById(R.id.timer_text);
            timerText.setText(time(currentTime, "mm:ss:SSS"));

            //Very Important! this Handler method calls the runnable again
            timerHandler.post(this);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.datascreen);

        message_info = (TextView) findViewById(R.id.message_info);

        message_info.setText("Welcome");

        //Set timerText to zero
        TextView timerText = (TextView) findViewById(R.id.timer_text);
        timerText.setText("00:00:000");



    }


    //Stops listening the accelerometer
    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //Allow using the accelerometer manually, first we did it onCreate() and onResum()
    public void onStartClick(View view) {

        //Starts the Service Sensor_Listener
        Intent listener = new Intent(this,Sensor_Listener.class);
        startService(listener);

        Log.v(MyTAG, "Servicio Iniciado");


        // Moment in which the accelerometer is going to store data
        start_time = System.currentTimeMillis();

        //call the run method of timerRunnable
        timerHandler.post(timerRunnable);

    }


    //Stops listening the accelerometer
    public void onStopClick(View view) {

        //Stops the Service Sensor_Listener
        Intent listener = new Intent(this,Sensor_Listener.class);
        stopService(listener);

        Log.v(MyTAG, "Servicio Parado");

        message_info = (TextView) findViewById(R.id.message_info);

        message_info.setText("Come back soon");


        //Stops timerRunnable
        timerHandler.removeCallbacks(timerRunnable);

    }


    //Exit Button
    //Stops listening the accelerometer in case the user hasn´t clicked stopButton
    public void onExitClick(View view) {

        //Stops timerRunnable in case the user hasn´t clicked stopButton
        timerHandler.removeCallbacks(timerRunnable);

        //When we stop the Main activity we send a boolean message with the Intent
        Intent i = new Intent(this, Start.class);
        // MainMessage to know if the activity has started or not
        i.putExtra("MainMessage", false);
        // The activity starts
        startActivity(i);
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

}