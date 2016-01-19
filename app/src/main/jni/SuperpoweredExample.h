#ifndef Header_SuperpoweredExample
#define Header_SuperpoweredExample
#include <math.h>
#include <pthread.h>
#include "SuperpoweredExample.h"
#include "SuperpoweredAdvancedAudioPlayer.h"
#include "SuperpoweredAndroidAudioIO.h"
#include "SuperpoweredFlanger.h"
#include "SuperpoweredFilter.h"
#include "SuperpoweredReverb.h"
#include "SuperpoweredRoll.h"
#include "SuperpoweredWhoosh.h"
#include "SuperpoweredEcho.h"

#define NUM_BUFFERS 2
#define HEADROOM_DECIBEL 3.0f
static const float headroom = powf(10.0f, -HEADROOM_DECIBEL * 0.025);

class SuperpoweredExample {
public:

	SuperpoweredExample(const char *path, int *params);
	~SuperpoweredExample();

	bool process(short int *output, unsigned int numberOfSamples);
	void onPlayPause(bool play);
	void onTilt(int value);
	void onFront(float value);
	void onBack(float value);
	void onLeft(float value);
	void onRight(float value);
	void onFxSelect(int* value);
	void onFxOff();
	void onDrop();
	void onFxValue(int value);
	void restartTracks();
	void changeTracks(const char *path, int *params);

private:
    pthread_mutex_t mutex;
    SuperpoweredAndroidAudioIO *audioSystem;
    SuperpoweredAdvancedAudioPlayer *playerFront, *playerBack, *playerLeft, *playerRight, *playerCentre, *playerDrop;
	SuperpoweredRoll *roll;
	SuperpoweredFilter *filter;
	SuperpoweredFlanger *flanger;
	SuperpoweredReverb *reverb;
	SuperpoweredWhoosh *whoosh;
	SuperpoweredEcho *echo;

    float *stereoBuffer;
    unsigned char activeFx;
    float crossValue, volFront, volBack, volLeft, volRight, volCentre, tempoMod;
	double currentBPM, trackBPM;
};

#endif
