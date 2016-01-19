package com.logitech.gmckee.edmstikclassicstable;
/**
 * Created by GMckee on 11/01/16.
 */
public class SuperPoweredNativeCall {

    /* Create JNI Link Methods */
    public native void SuperpoweredExample(String apkPath, long[] offsetAndLength);
    public native void onPlayPause(boolean play);
    public native void onTilt(int value);
    public native void onFront(float value);
    public native void onBack(float value);
    public native void onLeft(float value);
    public native void onRight(float value);
    public native void onFxSelect(int[] value);
    public native void onFxOff();
    public native void onDrop();
    public native void onFxValue(int value);
    public native void changeTracks(String apkPath, long[] offsetAndLength);

    public SuperPoweredNativeCall(){
        System.loadLibrary("SuperpoweredExample");
    }

    public void callSuperPoweredJNI(long[] params) {
        //System.loadLibrary("SuperpoweredExample");
        SuperpoweredExample("/data/app/com.logitech.gmckee.edmstikclassicstable-1/base.apk", params);
    }

    public void changeRunningTracks(long[] params) {
        //System.loadLibrary("SuperpoweredExample");
        changeTracks("/data/app/com.logitech.gmckee.edmstikclassicstable-1/base.apk", params);
    }

}
