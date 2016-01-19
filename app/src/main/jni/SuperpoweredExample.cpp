#include "SuperpoweredExample.h"
#include "SuperpoweredSimple.h"
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include <csignal>

double currentBPM = 125.0;

static SuperpoweredExample* example = NULL;
static void sigsegvHandler(int signum){
    __android_log_write(ANDROID_LOG_INFO, "signalHandler", "Caught SIGSEGV!");
    delete example;
}

static void sigabrtHandler(int signum){
    __android_log_write(ANDROID_LOG_INFO, "signalHandler", "Caught SIGABRT!");
    delete example;
}

static void playerEventCallbackFront(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value) {
    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
    	SuperpoweredAdvancedAudioPlayer *playerFront = *((SuperpoweredAdvancedAudioPlayer **)clientData);
        //playerFront->setBpm(currentBPM);
        playerFront->setFirstBeatMs(0);
        playerFront->setPosition(playerFront->firstBeatMs, false, false);
    };
}

static void playerEventCallbackBack(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value) {
    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
    	SuperpoweredAdvancedAudioPlayer *playerBack = *((SuperpoweredAdvancedAudioPlayer **)clientData);
        //playerBack->setBpm(currentBPM);
        playerBack->setFirstBeatMs(0);
        playerBack->setPosition(playerBack->firstBeatMs, false, false);
    };
}

static void playerEventCallbackLeft(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value) {
    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
        SuperpoweredAdvancedAudioPlayer *playerLeft = *((SuperpoweredAdvancedAudioPlayer **)clientData);
        //playerLeft->setBpm(currentBPM);
        playerLeft->setFirstBeatMs(0);
        playerLeft->setPosition(playerLeft->firstBeatMs, false, false);
    };
}

static void playerEventCallbackRight(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value) {
    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
        SuperpoweredAdvancedAudioPlayer *playerRight = *((SuperpoweredAdvancedAudioPlayer **)clientData);
        //playerRight->setBpm(currentBPM);
        playerRight->setFirstBeatMs(0);
        playerRight->setPosition(playerRight->firstBeatMs, false, false);
    };
}

static void playerEventCallbackCentre(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value) {
    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
        SuperpoweredAdvancedAudioPlayer *playerCentre = *((SuperpoweredAdvancedAudioPlayer **)clientData);
        //playerCentre->setBpm(currentBPM);
        playerCentre->setFirstBeatMs(0);
        playerCentre->setPosition(playerCentre->firstBeatMs, false, false);
    };
}

static void playerEventCallbackDrop(void *clientData, SuperpoweredAdvancedAudioPlayerEvent event, void *value) {
    if (event == SuperpoweredAdvancedAudioPlayerEvent_LoadSuccess) {
        SuperpoweredAdvancedAudioPlayer *playerDrop = *((SuperpoweredAdvancedAudioPlayer **)clientData);
        playerDrop->setFirstBeatMs(0);
        playerDrop->setPosition(playerDrop->firstBeatMs, false, false);
    };

    if (event == SuperpoweredAdvancedAudioPlayerEvent_EOF){
        //Once drop has finished
        example->onPlayPause(true);
    }
}

static bool audioProcessing(void *clientdata, short int *audioIO, int numberOfSamples, int samplerate) {
	return ((SuperpoweredExample *)clientdata)->process(audioIO, numberOfSamples);
}

SuperpoweredExample::SuperpoweredExample(const char *path, int *params) :
        activeFx(0), crossValue(0.0f), volFront(1.0f), volBack(0.5f), volLeft(0.0f), volRight(0.0f), volCentre(0.5f), currentBPM(125.0) {

    signal(SIGSEGV, sigsegvHandler);
    signal(SIGABRT, sigabrtHandler);

    pthread_mutex_init(&mutex, NULL); // This will keep our player volumes and playback states in sync.
    unsigned int samplerate = params[12];
    unsigned int buffersize = params[13];

    if(!stereoBuffer){
        free(stereoBuffer);
    }

    stereoBuffer = (float *)memalign(32, (buffersize + 16) * sizeof(float) * 2);

    currentBPM = 125.0f;

    playerFront = new SuperpoweredAdvancedAudioPlayer(&playerFront , playerEventCallbackFront, samplerate, 0);
    playerBack = new SuperpoweredAdvancedAudioPlayer(&playerBack , playerEventCallbackBack, samplerate, 0);
    playerLeft = new SuperpoweredAdvancedAudioPlayer(&playerLeft , playerEventCallbackLeft, samplerate, 0);
    playerRight = new SuperpoweredAdvancedAudioPlayer(&playerRight , playerEventCallbackRight, samplerate, 0);
    playerCentre = new SuperpoweredAdvancedAudioPlayer(&playerCentre , playerEventCallbackCentre, samplerate, 0);
    playerDrop = new SuperpoweredAdvancedAudioPlayer(&playerDrop , playerEventCallbackDrop, samplerate, 0);

    playerFront->open(path, params[0], params[1]);
    playerBack->open(path, params[2], params[3]);
    playerLeft->open(path, params[4], params[5]);
    playerRight->open(path, params[6], params[7]);
    playerCentre->open(path, params[8], params[9]);
    playerDrop->open(path, params[10], params[11]);

    playerFront->setBpm(currentBPM);
    playerBack->setBpm(currentBPM);
    playerLeft->setBpm(currentBPM);
    playerRight->setBpm(currentBPM);
    playerCentre->setBpm(currentBPM);
    playerDrop->setBpm(currentBPM);

    playerFront->syncMode = SuperpoweredAdvancedAudioPlayerSyncMode_TempoAndBeat;
    playerBack->syncMode = SuperpoweredAdvancedAudioPlayerSyncMode_TempoAndBeat;
    playerLeft->syncMode = SuperpoweredAdvancedAudioPlayerSyncMode_TempoAndBeat;
    playerRight->syncMode = SuperpoweredAdvancedAudioPlayerSyncMode_TempoAndBeat;
    playerCentre->syncMode = SuperpoweredAdvancedAudioPlayerSyncMode_TempoAndBeat;

    filter = new SuperpoweredFilter(SuperpoweredFilter_Resonant_Lowpass, samplerate);
    flanger = new SuperpoweredFlanger(samplerate);
    reverb = new SuperpoweredReverb(samplerate);
    roll = new SuperpoweredRoll(samplerate);
    roll->bpm=currentBPM;
    whoosh = new SuperpoweredWhoosh(samplerate);
    echo = new SuperpoweredEcho(samplerate);

    audioSystem = new SuperpoweredAndroidAudioIO(samplerate, buffersize, false, true, audioProcessing, this, 0);

}

SuperpoweredExample::~SuperpoweredExample() {

    delete playerFront;
    delete playerBack;
    delete playerLeft;
    delete playerRight;
    delete playerCentre;
    delete playerDrop;
    delete filter;
    delete flanger;
    delete reverb;
    delete roll;
    delete whoosh;
    delete echo;
    delete audioSystem;

    pthread_mutex_destroy(&mutex);
}

void SuperpoweredExample::changeTracks(const char *path, int *params) {

    playerFront->open(path, params[0], params[1]);
    playerBack->open(path, params[2], params[3]);
    playerLeft->open(path, params[4], params[5]);
    playerRight->open(path, params[6], params[7]);
    playerCentre->open(path, params[8], params[9]);
    playerDrop->open(path, params[10], params[11]);

    currentBPM = 125.0f;

    playerFront->setBpm(currentBPM);
    playerBack->setBpm(currentBPM);
    playerLeft->setBpm(currentBPM);
    playerRight->setBpm(currentBPM);
    playerCentre->setBpm(currentBPM);
    playerDrop->setBpm(currentBPM);

}

void SuperpoweredExample::onPlayPause(bool play) {

    playerFront->setPosition(0, false, false);
    playerBack->setPosition(0, false, false);
    playerLeft->setPosition(0, false, false);
    playerRight->setPosition(0, false, false);
    playerCentre->setPosition(0, false, false);

    if (!play) {
        playerFront->pause();
        playerBack->pause();
        playerLeft->pause();
        playerRight->pause();
        playerCentre->pause();
        playerDrop->play(false);

    } else {
        playerCentre->play(false);
        playerFront->play(false);
        playerBack->play(false);
        playerLeft->play(false);
        playerRight->play(false);
        playerDrop->pause();

    }

}

void SuperpoweredExample::onTilt(int value) {

    pthread_mutex_lock(&mutex);
    crossValue = float(value) * 0.01f;
    if (crossValue < 0.01f) {
        volFront = 1.0f * headroom;
    } else if (crossValue > 0.99f) {
        volFront = 0.0f;
    } else { // constant power curve
        volFront = cosf(M_PI_2 * crossValue) * headroom;
    };
    pthread_mutex_unlock(&mutex);

}

void SuperpoweredExample::onFront(float value) {

    volFront=(value/100);

}

void SuperpoweredExample::onBack(float value) {

    if(value>5.0) {
        tempoMod = 1.0f - ((value/100)*0.25f);
        pthread_mutex_lock(&mutex);

////    playerFront->setTempo(0.75f, true);
////    playerBack->setTempo(0.75f, true);
////    playerLeft->setTempo(0.75f, true);
////    playerRight->setTempo(0.75f, true);
////    playerCentre->setTempo(0.75f, true);
//
        currentBPM = 125.0*tempoMod;
        pthread_mutex_unlock(&mutex);
    }

}

void SuperpoweredExample::onLeft(float value) {


    volLeft=(value/100);

}

void SuperpoweredExample::onRight(float value) {

    volRight=(value/100);

}

void SuperpoweredExample::onFxOff() {

    pthread_mutex_lock(&mutex);

    flanger->enable(false);
    filter->enable(false);
    reverb->enable(false);
    roll->enable(false);
    echo->enable(false);
    whoosh->enable(false);

    pthread_mutex_unlock(&mutex);

}

void SuperpoweredExample::onDrop() {
// Do the drop
   // __android_log_write(ANDROID_LOG_INFO, "CheckDrop", "In Drop");
   // example->onPlayPause(false);
    //playerDrop->play(true);

}

#define MINFREQ 60.0f
#define MAXFREQ 20000.0f

static inline float floatToFrequency(float value) {

    if (value > 0.97f) return MAXFREQ;
    if (value < 0.03f) return MINFREQ;
    value = powf(10.0f, (value + ((0.4f - fabsf(value - 0.4f)) * 0.3f)) * log10f(MAXFREQ - MINFREQ)) + MINFREQ;
    return value < MAXFREQ ? value : MAXFREQ;

}

void SuperpoweredExample::onFxSelect(int* value) {

    if(!activeFx){
        activeFx=true;
    }

    pthread_mutex_lock(&mutex);

    flanger->setWet(value[2]*0.01f);
    filter->setResonantParameters(floatToFrequency(1.0f - (value[3]*0.01f)/2.0f), 0.2f);
    reverb->setWet(value[1]*0.01);

    if(value[0]*0.01f>0.01f){
        whoosh->setFrequency((value[0]*0.01f)*7000);
        echo->decay=(value[0]*0.01f)*0.7;

        if(value[0]*0.01f>0.6f){
            roll->beats = 0.25f;
        }

        else if (value[0]*0.01f> 0.4f) {
            roll->beats = 0.5f;

        }

        else if (value[0]*0.01f> 0.2f) {
            roll->beats = 1.0f;
        }

        else {
            roll->beats = 2.0f;
        }

        roll->enable(true);
        whoosh->enable(true);
        echo->enable(true);
    }

    flanger->enable(true);
    filter->enable(true);
    reverb->enable(true);

    pthread_mutex_unlock(&mutex);
}

void SuperpoweredExample::onFxValue(int ivalue) {

}

void SuperpoweredExample::restartTracks(){

    pthread_mutex_lock(&mutex);


    pthread_mutex_unlock(&mutex);
}

bool SuperpoweredExample::process(short int *output, unsigned int numberOfSamples) {

    if(volFront > 1.0f){
        volFront = 1.0f;
    }
    if(volLeft > 1.0f){
        volLeft = 1.0f;
    }
    if(volRight > 1.0f){
        volRight = 1.0f;
    }

    bool masterIsA = true;
    double msElapsedSinceLastBeatA = playerCentre->msElapsedSinceLastBeat;

    pthread_mutex_lock(&mutex);

    bool silence = !playerCentre->process(stereoBuffer, !silence, numberOfSamples, volCentre,currentBPM, msElapsedSinceLastBeatA);
    if(playerFront->process(stereoBuffer, !silence, numberOfSamples, volFront, currentBPM, msElapsedSinceLastBeatA)) silence=false;
    if(playerBack->process(stereoBuffer, !silence, numberOfSamples, volBack, currentBPM, msElapsedSinceLastBeatA))  silence=false;
    if(playerLeft->process(stereoBuffer, !silence, numberOfSamples, volLeft, currentBPM, msElapsedSinceLastBeatA))  silence=false;
    if(playerRight->process(stereoBuffer, !silence, numberOfSamples, volRight, currentBPM, msElapsedSinceLastBeatA)) silence=false;
    if(playerDrop->process(stereoBuffer, !silence, numberOfSamples, 0.5f, currentBPM, msElapsedSinceLastBeatA)) silence=false;

    flanger->bpm=currentBPM;
    flanger->process(stereoBuffer, stereoBuffer, numberOfSamples);
    filter->process(stereoBuffer, stereoBuffer, numberOfSamples);
    reverb->process(stereoBuffer, stereoBuffer, numberOfSamples);
    roll->process(stereoBuffer, stereoBuffer, numberOfSamples);
    whoosh->process(stereoBuffer, stereoBuffer, numberOfSamples);
    echo->process(stereoBuffer, stereoBuffer, numberOfSamples);

    pthread_mutex_unlock(&mutex);

// The stereoBuffer is ready now, let's put the finished audio into the requested buffers.
    if (!silence)
    SuperpoweredFloatToShortInt(stereoBuffer, output, numberOfSamples);
    return !silence;

}

extern "C" {
	JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_SuperpoweredExample(JNIEnv *javaEnvironment, jobject self, jstring apkPath, jlongArray offsetAndLength);
    JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_changeTracks(JNIEnv *javaEnvironment, jobject self, jstring apkPath, jlongArray offsetAndLength);
    JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onPlayPause(JNIEnv *javaEnvironment, jobject self, jboolean play);
	JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onTilt(JNIEnv *javaEnvironment, jobject self, jint value);
    JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFront(JNIEnv *javaEnvironment, jobject self, jfloat value);
    JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onBack(JNIEnv *javaEnvironment, jobject self, jfloat value);
    JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onLeft(JNIEnv *javaEnvironment, jobject self, jfloat value);
    JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onRight(JNIEnv *javaEnvironment, jobject self, jfloat value);
	JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFxSelect(JNIEnv *javaEnvironment, jobject self, jintArray value);
	JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFxOff(JNIEnv *javaEnvironment, jobject self);
    JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onDrop(JNIEnv *javaEnvironment, jobject self);
	JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFxValue(JNIEnv *javaEnvironment, jobject self, jint value);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_SuperpoweredExample(JNIEnv *javaEnvironment, jobject self, jstring apkPath, jlongArray params) {
    delete example;
    // Convert the input jlong array to a regular int array.
    jlong *longParams = javaEnvironment->GetLongArrayElements(params, JNI_FALSE);
    int arr[14];

    for (int n = 0; n < 14; n++) {
        arr[n] = longParams[n];
    }

    javaEnvironment->ReleaseLongArrayElements(params, longParams, JNI_ABORT);
    const char *path = javaEnvironment->GetStringUTFChars(apkPath, JNI_FALSE);
    example = new SuperpoweredExample(path, arr);
    javaEnvironment->ReleaseStringUTFChars(apkPath, path);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_changeTracks(JNIEnv *javaEnvironment, jobject self, jstring apkPath, jlongArray params) {
    // Convert the input jlong array to a regular int array.
    jlong *longParams = javaEnvironment->GetLongArrayElements(params, JNI_FALSE);
    int arr[14];

    for (int n = 0; n < 14; n++) {
        arr[n] = longParams[n];
    }

    javaEnvironment->ReleaseLongArrayElements(params, longParams, JNI_ABORT);
    const char *path = javaEnvironment->GetStringUTFChars(apkPath, JNI_FALSE);
    example->changeTracks(path, arr);
    javaEnvironment->ReleaseStringUTFChars(apkPath, path);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onPlayPause(JNIEnv *javaEnvironment, jobject self, jboolean play) {
	example->onPlayPause(play);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onTilt(JNIEnv *javaEnvironment, jobject self, jint value) {
	example->onTilt(value);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFront(JNIEnv *javaEnvironment, jobject self, jfloat value) {
    example->onFront(value);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onBack(JNIEnv *javaEnvironment, jobject self, jfloat value) {
    example->onBack(value);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onLeft(JNIEnv *javaEnvironment, jobject self, jfloat value) {
    example->onLeft(value);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onRight(JNIEnv *javaEnvironment, jobject self, jfloat value) {
    example->onRight(value);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFxSelect(JNIEnv *javaEnvironment, jobject self, jintArray value) {

    jint *intParams = javaEnvironment->GetIntArrayElements(value, JNI_FALSE);
    int arr[4];
    for (int n = 0; n < 4; n++) arr[n] = intParams[n];
    javaEnvironment->ReleaseIntArrayElements(value, intParams, JNI_ABORT);

    example->onFxSelect(arr);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFxOff(JNIEnv *javaEnvironment, jobject self) {
	example->onFxOff();
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onDrop(JNIEnv *javaEnvironment, jobject self) {
    example->onPlayPause(false);
}

JNIEXPORT void Java_com_logitech_gmckee_edmstikclassicstable_SuperPoweredNativeCall_onFxValue(JNIEnv *javaEnvironment, jobject self, jint value) {
	example->onFxValue(value);
}
