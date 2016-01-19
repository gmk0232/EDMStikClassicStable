package com.logitech.gmckee.edmstikclassicstable;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import java.io.IOException;

/**
 * Created by GMckee on 08/01/16.
 */
public class EDMStikController implements Tiltable{

    private final BOOMAccelerometer mBOOMAccelerometer;
    private final Context ctx;
    private final SuperPoweredNativeCall spnc;

    private static final int FRONT_TILT = 5000;
    private static final int BACK_TILT = 5001;
    private static final int LEFT_TILT = 5002;
    private static final int RIGHT_TILT = 5003;
    private static final int CENTRE = 0;

    private String samplerateString;
    private String bufferSizeString;
    private boolean frontLocked, leftLocked, rightLocked;
    private AssetFileDescriptor fd0 , fd1, fd2, fd3, fd4, fd5;
    private int direction = 0;
    private boolean fXEnabled;
    private int[] fxValues;

    public EDMStikController(Context ctx, String samplerate, String bufferSize){

        this.ctx=ctx;
        spnc = new SuperPoweredNativeCall();
        samplerateString=samplerate;
        bufferSizeString=bufferSize;
        frontLocked=false;
        leftLocked=false;
        rightLocked=false;

        fxValues = new int[4];

        mBOOMAccelerometer = new BOOMAccelerometer(ctx);
        mBOOMAccelerometer.registerListener(this);

        //Startup first track
        fd0 = ctx.getResources().openRawResourceFd(R.raw.ssvoxrr);
        fd1 = ctx.getResources().openRawResourceFd(R.raw.ssmixrr);
        fd2 = ctx.getResources().openRawResourceFd(R.raw.sschordrr);
        fd3 = ctx.getResources().openRawResourceFd(R.raw.ssgtrrr);
        fd4 = ctx.getResources().openRawResourceFd(R.raw.sslooprr);
        fd5 = ctx.getResources().openRawResourceFd(R.raw.russiandrop);

        long[] params = {
                fd0.getStartOffset(),
                fd0.getLength(),
                fd1.getStartOffset(),
                fd1.getLength(),
                fd2.getStartOffset(),
                fd2.getLength(),
                fd3.getStartOffset(),
                fd3.getLength(),
                fd4.getStartOffset(),
                fd4.getLength(),
                fd5.getStartOffset(),
                fd5.getLength(),
                Integer.parseInt(samplerateString),
                Integer.parseInt(bufferSizeString)
        };

        try {
            fd0.getParcelFileDescriptor().close();
            fd1.getParcelFileDescriptor().close();
            fd2.getParcelFileDescriptor().close();
            fd3.getParcelFileDescriptor().close();
            fd4.getParcelFileDescriptor().close();
            fd5.getParcelFileDescriptor().close();
        } catch (IOException e) {
            //Log.e("IOException", e.getMessage());
        }

        spnc.callSuperPoweredJNI(params);
        spnc.onPlayPause(true);

    }

    public void changeTrack(int trackNo) {
        unLockAll();

        //Russian Roulette
        if(trackNo==0) {
            fd0 = ctx.getResources().openRawResourceFd(R.raw.ssvoxrr);
            fd1 = ctx.getResources().openRawResourceFd(R.raw.ssmixrr);
            fd2 = ctx.getResources().openRawResourceFd(R.raw.sschordrr);
            fd3 = ctx.getResources().openRawResourceFd(R.raw.ssgtrrr);
            fd4 = ctx.getResources().openRawResourceFd(R.raw.sslooprr);
            fd5 = ctx.getResources().openRawResourceFd(R.raw.russiandrop);

            long[] params = {
                    fd0.getStartOffset(),
                    fd0.getLength(),
                    fd1.getStartOffset(),
                    fd1.getLength(),
                    fd2.getStartOffset(),
                    fd2.getLength(),
                    fd3.getStartOffset(),
                    fd3.getLength(),
                    fd4.getStartOffset(),
                    fd4.getLength(),
                    fd5.getStartOffset(),
                    fd5.getLength(),
                    Integer.parseInt(samplerateString),
                    Integer.parseInt(bufferSizeString)
            };

            try {
                fd0.getParcelFileDescriptor().close();
                fd1.getParcelFileDescriptor().close();
                fd2.getParcelFileDescriptor().close();
                fd3.getParcelFileDescriptor().close();
                fd4.getParcelFileDescriptor().close();
                fd5.getParcelFileDescriptor().close();
            } catch (IOException e) {
                //Log.e("IOException", e.getMessage());
            }


            spnc.changeRunningTracks(params);
            spnc.onPlayPause(true);
        }

        if(trackNo==1) {
            //Skrillesque
            fd0 = ctx.getResources().openRawResourceFd(R.raw.ssvoxskrillb);
            fd1 = ctx.getResources().openRawResourceFd(R.raw.ssmixskrillb);
            fd2 = ctx.getResources().openRawResourceFd(R.raw.sstripletsynthskrillb);
            fd3 = ctx.getResources().openRawResourceFd(R.raw.sspocksynthskrillb);
            fd4 = ctx.getResources().openRawResourceFd(R.raw.sssawsynthskrillb);
            fd5 = ctx.getResources().openRawResourceFd(R.raw.puturhandsup);

            long[] params = {
                    fd0.getStartOffset(),
                    fd0.getLength(),
                    fd1.getStartOffset(),
                    fd1.getLength(),
                    fd2.getStartOffset(),
                    fd2.getLength(),
                    fd3.getStartOffset(),
                    fd3.getLength(),
                    fd4.getStartOffset(),
                    fd4.getLength(),
                    fd5.getStartOffset(),
                    fd5.getLength(),
                    Integer.parseInt(samplerateString),
                    Integer.parseInt(bufferSizeString)
            };

            try {
                fd0.getParcelFileDescriptor().close();
                fd1.getParcelFileDescriptor().close();
                fd2.getParcelFileDescriptor().close();
                fd3.getParcelFileDescriptor().close();
                fd4.getParcelFileDescriptor().close();
                fd5.getParcelFileDescriptor().close();
            } catch (IOException e) {
                //Log.e("IOException", e.getMessage());
            }


            spnc.changeRunningTracks(params);
            spnc.onPlayPause(true);
        }

        if(trackNo==2) {
            //The Dollar is our God
            fd0 = ctx.getResources().openRawResourceFd(R.raw.ssvoxdollar);
            fd1 = ctx.getResources().openRawResourceFd(R.raw.ssmixdollar);
            fd2 = ctx.getResources().openRawResourceFd(R.raw.ssmelodydollar);
            fd3 = ctx.getResources().openRawResourceFd(R.raw.ssbuzzdollar);
            fd4 = ctx.getResources().openRawResourceFd(R.raw.sspercdollar);
            fd5 = ctx.getResources().openRawResourceFd(R.raw.dollarisourgod);

            long[] params = {
                    fd0.getStartOffset(),
                    fd0.getLength(),
                    fd1.getStartOffset(),
                    fd1.getLength(),
                    fd2.getStartOffset(),
                    fd2.getLength(),
                    fd3.getStartOffset(),
                    fd3.getLength(),
                    fd4.getStartOffset(),
                    fd4.getLength(),
                    fd5.getStartOffset(),
                    fd5.getLength(),
                    Integer.parseInt(samplerateString),
                    Integer.parseInt(bufferSizeString)
            };

            try {
                fd0.getParcelFileDescriptor().close();
                fd1.getParcelFileDescriptor().close();
                fd2.getParcelFileDescriptor().close();
                fd3.getParcelFileDescriptor().close();
                fd4.getParcelFileDescriptor().close();
                fd5.getParcelFileDescriptor().close();
            } catch (IOException e) {
                //Log.e("IOException", e.getMessage());
            }


            spnc.changeRunningTracks(params);
            spnc.onPlayPause(true);
        }

        if(trackNo==3) {
            //Welcome Robots
            fd0 = ctx.getResources().openRawResourceFd(R.raw.ssmelodywtrb);
            fd1 = ctx.getResources().openRawResourceFd(R.raw.ssmixwtr);
            fd2 = ctx.getResources().openRawResourceFd(R.raw.sssyncwtrb);
            fd3 = ctx.getResources().openRawResourceFd(R.raw.sssirenwtrb);
            fd4 = ctx.getResources().openRawResourceFd(R.raw.ssformantwtrb);
            fd5 = ctx.getResources().openRawResourceFd(R.raw.vocode);

            long[] params = {
                    fd0.getStartOffset(),
                    fd0.getLength(),
                    fd1.getStartOffset(),
                    fd1.getLength(),
                    fd2.getStartOffset(),
                    fd2.getLength(),
                    fd3.getStartOffset(),
                    fd3.getLength(),
                    fd4.getStartOffset(),
                    fd4.getLength(),
                    fd5.getStartOffset(),
                    fd5.getLength(),
                    Integer.parseInt(samplerateString),
                    Integer.parseInt(bufferSizeString)
            };

            try {
                fd0.getParcelFileDescriptor().close();
                fd1.getParcelFileDescriptor().close();
                fd2.getParcelFileDescriptor().close();
                fd3.getParcelFileDescriptor().close();
                fd4.getParcelFileDescriptor().close();
                fd5.getParcelFileDescriptor().close();
            } catch (IOException e) {

            }
            spnc.changeRunningTracks(params);
            spnc.onPlayPause(true);
        }

        if(trackNo==4) {
            //Disco Dynamite
            fd0 = ctx.getResources().openRawResourceFd(R.raw.ssbrassdisco);
            fd1 = ctx.getResources().openRawResourceFd(R.raw.ssmixdisco);
            fd2 = ctx.getResources().openRawResourceFd(R.raw.ssclavdisco);
            fd3 = ctx.getResources().openRawResourceFd(R.raw.ssstringsdisco);
            fd4 = ctx.getResources().openRawResourceFd(R.raw.sssynthdisco);
            fd5 = ctx.getResources().openRawResourceFd(R.raw.changinb);

            long[] params = {
                    fd0.getStartOffset(),
                    fd0.getLength(),
                    fd1.getStartOffset(),
                    fd1.getLength(),
                    fd2.getStartOffset(),
                    fd2.getLength(),
                    fd3.getStartOffset(),
                    fd3.getLength(),
                    fd4.getStartOffset(),
                    fd4.getLength(),
                    fd5.getStartOffset(),
                    fd5.getLength(),
                    Integer.parseInt(samplerateString),
                    Integer.parseInt(bufferSizeString)
            };

            try {
                fd0.getParcelFileDescriptor().close();
                fd1.getParcelFileDescriptor().close();
                fd2.getParcelFileDescriptor().close();
                fd3.getParcelFileDescriptor().close();
                fd4.getParcelFileDescriptor().close();
                fd5.getParcelFileDescriptor().close();
            } catch (IOException e) {
                //Log.e("IOException", e.getMessage());
            }


            spnc.changeRunningTracks(params);
            spnc.onPlayPause(true);
        }

        if(trackNo==5) {
            //Mambo No. 6
            fd0 = ctx.getResources().openRawResourceFd(R.raw.click);
            fd1 = ctx.getResources().openRawResourceFd(R.raw.click);
            fd2 = ctx.getResources().openRawResourceFd(R.raw.click);
            fd3 = ctx.getResources().openRawResourceFd(R.raw.click);
            fd4 = ctx.getResources().openRawResourceFd(R.raw.click);
            fd5 = ctx.getResources().openRawResourceFd(R.raw.click);

            long[] params = {
                    fd0.getStartOffset(),
                    fd0.getLength(),
                    fd1.getStartOffset(),
                    fd1.getLength(),
                    fd2.getStartOffset(),
                    fd2.getLength(),
                    fd3.getStartOffset(),
                    fd3.getLength(),
                    fd4.getStartOffset(),
                    fd4.getLength(),
                    fd5.getStartOffset(),
                    fd5.getLength(),
                    Integer.parseInt(samplerateString),
                    Integer.parseInt(bufferSizeString)
            };

            try {
                fd0.getParcelFileDescriptor().close();
                fd1.getParcelFileDescriptor().close();
                fd2.getParcelFileDescriptor().close();
                fd3.getParcelFileDescriptor().close();
                fd4.getParcelFileDescriptor().close();
                fd5.getParcelFileDescriptor().close();
            } catch (Exception e) {

                Log.d("ChangeTrackErr", e.getMessage());
            }

            spnc.changeRunningTracks(params);
            PerformanceMenu mpm = (PerformanceMenu)ctx;
            mpm.testButton.setBackgroundColor(0xFFFF0000);
            spnc.onPlayPause(true);
        }
    }

    public void unLockAll(){
        frontLocked=false;
        leftLocked=false;
        rightLocked = false;
    }

    public void lockLoop (){

        switch (direction){
            case FRONT_TILT:

                if(!frontLocked) {
                    frontLocked = true;
                }
                else{
                    frontLocked=false;
                }

                break;
            case BACK_TILT:

                break;
            case LEFT_TILT:
                if(!leftLocked) {
                    leftLocked = true;
                }
                else{
                    leftLocked=false;
                }

                break;
            case RIGHT_TILT:
                if(!rightLocked) {
                    rightLocked = true;
                }
                else{
                    rightLocked=false;
                }

                break;

            default:

        }
    }


    @Override
    public void onFront(float fwTilt) {
        direction=EDMStikController.FRONT_TILT;


    }

    @Override
    public void onBack(float bwTilt) {
        direction=EDMStikController.BACK_TILT;

    }

    @Override
    public void onLeft(float leTilt) {
        direction=EDMStikController.LEFT_TILT;

    }

    @Override
    public void onRight(float riTilt) {

        direction=EDMStikController.RIGHT_TILT;

    }

    @Override
    public void onCentre(){
        direction=EDMStikController.CENTRE;

        if(!frontLocked) {
            spnc.onFront(0);
        }
        if(!leftLocked) {
            spnc.onLeft(0);
        }
        if(!rightLocked) {
            spnc.onRight(0);
        }

        spnc.onBack(0);


    }

    @Override
    public void onDrop() {
       if(fxValues[0]>90){
           spnc.onDrop();
           unLockAll();
       }
    }

    @Override
    public void onFX(float[] fxTilts) {

    }

    public void enableFX(boolean isFX) {

        if(!isFX){
            spnc.onFxOff();
        }

        fXEnabled=isFX;
    }

    @Override
    public void onTiltChange(float[] tiltValues) {
        if(!fXEnabled){
            if(!frontLocked) {
                spnc.onFront(tiltValues[0]);
            }
            if(!leftLocked) {
                spnc.onLeft(tiltValues[2]);
            }
            if(!rightLocked) {
                spnc.onRight(tiltValues[3]);
            }

            spnc.onBack(tiltValues[1]);
        }

        else{
            fxValues[0]=(int)tiltValues[0];
            fxValues[1]=(int)tiltValues[1];
            fxValues[2]=(int)tiltValues[2];
            fxValues[3]=(int)tiltValues[3];

            spnc.onFxSelect(fxValues);
        }
    }

}
