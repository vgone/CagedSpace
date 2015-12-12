package caged.coaa.com.cagedspace.Utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import caged.coaa.com.cagedspace.Interface.MovementListener;
import caged.coaa.com.cagedspace.MainActivity;

/**
 * Created by SaideepReddy on 12/10/2015.
 */
public class UserPositionDetector implements SensorEventListener{

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    MovementListener mListener;

    public UserPositionDetector(SensorManager sensorManager,MovementListener listener){
        this.mListener = listener;
        this.mSensorManager = sensorManager;
        //this.mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    public void startDetector(){
        mSensorManager.registerListener(this, mAccelerometer, 1700000);
    }

    public void stopDetector(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       /* Log.d("sensor","x="+event.values[0]+"y="+event.values[1]+"z="+event.values[2]);
        float x=event.values[0], y =event.values[1];
        if(x>=2.0||y>=2.0){
            mListener.onMovement(true);
        } else {
            mListener.onMovement(false);
        }*/
        mListener.onMovement();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
