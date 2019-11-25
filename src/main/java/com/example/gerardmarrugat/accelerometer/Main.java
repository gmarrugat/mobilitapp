package com.example.gerardmarrugat.accelerometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

/**
 * Created by Gerard Marrugat on 20/10/2015.
 */
public class Main extends Activity implements SensorEventListener {

    //Declaramos los atributos de la classe que deben ser accesibles desde cualquier método de la classe
    //Los atributos internos de cada método son sólo accesibles desde esos métodos, no se podría acceder desde ningún otro
    //punto de la classe

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private float Xdata, Ydata, Zdata;
    private boolean mInitialized;
    private final float NOISE = (float) 2.0;



    public Main() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.datascreen);

        // Indicates that the app hasn´t started
        mInitialized = false;

        // Instance the accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // registerListener(EventListener, Sensor, rate)
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    protected void onPause() {
        super.onPause();
        // stops listening the accelerometer
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // listen the accelerometer
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Enlazamos los recursos de la interfaz con las variables de código que las representan
        TextView x_axis = (TextView)findViewById(R.id.x_axis);

        TextView y_axis = (TextView)findViewById(R.id.y_axis);

        TextView z_axis = (TextView)findViewById(R.id.z_axis);

        ImageView image = (ImageView)findViewById(R.id.image);


            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            if (!mInitialized) {

                x_axis.setText("0.0");
                y_axis.setText("0.0");
                z_axis.setText("0.0");

                Xdata = x;
                Ydata = y;
                Zdata = z;

                mInitialized = true;

            } else {

                float deltaX = Math.abs(Xdata - x);
                float deltaY = Math.abs(Ydata - y);
                float deltaZ = Math.abs(Zdata - z);

                if (deltaX < NOISE) deltaX = (float) 0.0;
                if (deltaY < NOISE) deltaY = (float) 0.0;
                if (deltaZ < NOISE) deltaZ = (float) 0.0;

                Xdata = x;
                Ydata = y;
                Zdata = z;

                x_axis.setText(Float.toString(deltaX));
                y_axis.setText(Float.toString(deltaY));
                z_axis.setText(Float.toString(deltaZ));


                image.setVisibility(View.VISIBLE);
                if ((deltaX > deltaY) && (deltaX > deltaZ)) {
                    image.setImageResource(R.drawable.shaker_fig_1);
                } else if ((deltaY > deltaX) && (deltaY > deltaZ)) {
                    image.setImageResource(R.drawable.shaker_fig_2);
                } else if ((deltaZ > deltaX) && (deltaZ > deltaY)) {
                    image.setImageResource(R.drawable.shaker_fig_3);
                } else {
                    image.setVisibility(View.INVISIBLE);

                }

            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
