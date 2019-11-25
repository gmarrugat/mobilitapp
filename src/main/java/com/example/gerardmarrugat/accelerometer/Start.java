package com.example.gerardmarrugat.accelerometer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.lang.Boolean;

public class Start extends AppCompatActivity {


    public void onClick(View view){
        // Start Button
        //When we call the Main activity we send a boolean message with the Intent
        Intent i = new Intent(this,DataScreen.class);
        // onStartMessage helps us to know if the action is started or not
        i.putExtra("onStartMessage",true);
        // the activity starts
        startActivity(i);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        //Intent´s extras from the Main activity
        Bundle MainMessage = getIntent().getExtras();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
