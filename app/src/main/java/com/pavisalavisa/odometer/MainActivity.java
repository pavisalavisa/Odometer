package com.pavisalavisa.odometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private OdometerService odometer;
    private boolean bound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        watchMileage();
    }

    @Override
    public void onStart(){
        super.onStart();
        Intent intent=new Intent(this,OdometerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
        
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bound){
            unbindService(connection);
        }
    }

    private ServiceConnection connection=new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OdometerService.OdometerBinder odometerBinder=(OdometerService.OdometerBinder) service;
            odometer=odometerBinder.getOdometer();
            bound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound=false;
        }
    };

    private void watchMileage(){
        final TextView distanceView=(TextView)findViewById(R.id.distance);
        final Handler handler=new Handler();
        handler.post(new Runnable(){

            @Override
            public void run() {
                double distance=0.0;
                if(odometer!=null){
                    distance=odometer.getDistanceInMiles();
                }
                String distanceStr=String.format("%1$,.2f miles",distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this,1000);
            }
        });
    }
}
