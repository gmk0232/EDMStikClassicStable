package com.logitech.gmckee.edmstikclassicstable;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.logitech.gmckee.edmstikclassicstable.UIComponents.CircleImageView;

public class PerformanceMenu extends Activity{


    private EDMStikController mEDMStikController;
    boolean wasLongPress = false;

    private CircleImageView Track1;
    private CircleImageView Track2;
    private CircleImageView Track3;
    private CircleImageView Track4;
    private CircleImageView Track5;
    private CircleImageView Track6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Get the device's sample rate and buffer size to enable low-latency Android audio output, if available.
        String samplerateString = null;
        String buffersizeString = null;

        if (Build.VERSION.SDK_INT >= 17) {
            try {
                AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
                buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            }

            catch(Exception e){
                Log.e("AudioManagerErr", e.getMessage());
            }
        }

        if (samplerateString == null) {
            samplerateString = "44100";
        }

        if (buffersizeString == null){
            buffersizeString = "512";
        }

        mEDMStikController = new EDMStikController(this, samplerateString, buffersizeString);

        Track1 = (CircleImageView) findViewById(R.id.button1);
        Track2 = (CircleImageView) findViewById(R.id.button2);
        Track3 = (CircleImageView) findViewById(R.id.button3);
        Track4 = (CircleImageView) findViewById(R.id.button4);
        Track5 = (CircleImageView) findViewById(R.id.button5);
        Track6 = (CircleImageView) findViewById(R.id.button6);

        Track1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mEDMStikController.changeTrack(0);
                }

                catch (Exception e){
                    Log.d("ChangeTrackErr", e.getMessage());
                }
            }
        });

        Track2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mEDMStikController.changeTrack(1);
                }

                catch (Exception e){
                    Log.d("ChangeTrackErr", e.getMessage());
                }
            }
        });

        Track3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mEDMStikController.changeTrack(2);
                }

                catch (Exception e){
                    Log.d("ChangeTrackErr", e.getMessage());
                }
            }
        });

        Track4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mEDMStikController.changeTrack(3);
                }

                catch (Exception e){
                    Log.d("ChangeTrackErr", e.getMessage());
                }
            }
        });

        Track5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mEDMStikController.changeTrack(4);
                }

                catch (Exception e){
                    Log.d("ChangeTrackErr", e.getMessage());
                }
            }
        });

        Track6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mEDMStikController.changeTrack(5);
                }

                catch (Exception e){
                    Log.d("ChangeTrackErr", e.getMessage());
                }
            }
        });
    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && event.isLongPress()) {
                    wasLongPress=true;
                    mEDMStikController.enableFX(true);
                }

                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (!wasLongPress) {
                        mEDMStikController.lockLoop();
                    } else {
                        mEDMStikController.enableFX(false);
                        wasLongPress = false;
                        mEDMStikController.onDrop();

                    }
                }
                return true;
            }

            else{
                    return super.dispatchKeyEvent(event);
            }

    }

}