package com.logitech.gmckee.edmstikclassicstable;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by GMckee on 08/01/16.
 */
public class BOOMAccelerometer implements  SensorEventListener {

    private Context context;
    private SensorManager mSensorManager;
    float[] tilt;

    Tiltable tiltListener;

    public BOOMAccelerometer (Context context) {
        tilt = new float[4];

        for(int i = 0; i < tilt.length; i++){
            tilt[i]=0.0f;
        }
        this.context=context;
        InitSensors();
    }

    public void InitSensors(){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if ( tiltListener != null ) {
            System.arraycopy(event.values, 0, mValuesGravity, 0, 3);
            processTilt();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float[] mValuesGravity = new float[3];
    private float pitch,roll;
    private int front, back,left,right;
    private float flatZone = 0.2f;

    private void processTilt(){
        computePitchRoll();

        if(Math.abs(pitch)>1) pitch = 1.0f;
        if(pitch>=flatZone)
        {
            front = (int) Math.floor((pitch-flatZone)/(1-flatZone)*100);
            back = 0;
        }else if (pitch<=-flatZone)
        {
            front = 0;
            back = (int) Math.floor((Math.abs(pitch)-flatZone)/(1-flatZone)*100);
        }else
        {
            front = 0;
            back = 0;
        }

        if(Math.abs(roll)>1) roll = 1.0f;
        if(roll>=flatZone)
        {
            right = (int) Math.floor((roll-flatZone)/(1-flatZone)*100);
            left = 0;
        }else if(roll<=-flatZone)
        {
            right= 0;
            left = (int) Math.floor((Math.abs(roll)-flatZone)/(1-flatZone)*100);
        }
        else
        {
            right = 0;
            left = 0;
        }
        tilt[0] = front;
        tilt[1] = back;
        tilt[2] = left;
        tilt[3] = right;

        tiltListener.onTiltChange(tilt);
        computeStates(tilt);
//            if(!bWasLongPress)onCrossfaderFront(tilt);
//            onFxValue(tilt);

    }

    private void computeStates(float... val) {
        for( int i = 0; i <4 ; i++ ){
            if ( val[i] > 10.0f )
            {
                switch(i){

                    case 0:
                        tiltListener.onFront(val[i]);
                        break;
                    case 1:
                        tiltListener.onBack(val[i]);
                        break;
                    case 2:
                        tiltListener.onLeft(val[i]);
                        break;
                    case 3:
                        tiltListener.onRight(val[i]);
                        break;

                }

                if( (val[0]+val[1]+val[2]+val[3])<20.0f ){
                    tiltListener.onCentre();
                }

            }

        }
    }

    private void computePitchRoll()
    {

        pitch= mValuesGravity[0]/9.8f;
        roll= mValuesGravity[2]/9.8f;

    }

    public void registerListener(Tiltable listener) {

        tiltListener = listener;

    }
}
