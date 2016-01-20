package com.logitech.gmckee.edmstikclassicstable;

/**
 * Created by GMckee on 08/01/16.
 */
public interface Tiltable  {

    void onFront(float fwTilt);

    void onBack(float bwTilt);

    void onLeft(float leTilt);

    void onRight(float riTilt);

    void onCentre();

    void onDrop();

    void onFX(float[] fxTilts);

    /*This method is called once sanitized data has been sent from an accelerometer module */
    void onTiltChange(float... value);

}
