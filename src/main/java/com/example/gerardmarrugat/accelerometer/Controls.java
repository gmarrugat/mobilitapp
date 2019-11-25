package com.example.gerardmarrugat.accelerometer;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import java.lang.Boolean;

import java.util.ArrayList;

/**
 * Created by Gerard Marrugat on 28/10/2015.
 */

//Controls Fragment
public class Controls extends Fragment {

    ControlsListener activityCommander;

    public interface ControlsListener{

        // Here it will be a method that must be implemented by the MainActivity
        public void collect_data();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            activityCommander = (ControlsListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.controlscreen,container, false);

        final Button startbutton = (Button) view.findViewById(R.id.start_button);
        final Button stopbutton = (Button) view.findViewById(R.id.stop_button);



        startbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startbuttonClicked(v);
                    }
                }
        );

        stopbutton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        stopbuttonClicked(v);
                    }
                }
        );


        return view;
    }

    public void startbuttonClicked(View view){
        //Start storing accelerometer´s data

    }

    public void stopbuttonClicked(View view){
        //Stop storing accelerometer´s data

    }
}
