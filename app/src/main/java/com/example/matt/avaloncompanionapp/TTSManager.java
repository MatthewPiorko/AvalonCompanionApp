package com.example.matt.avaloncompanionapp;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import com.example.matt.avaloncompanionapp.TTSSegment.TTSSegment_Type;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nilanchala
 * http://www.stacktips.com
 */
public class TTSManager {
    private TextToSpeech mTts = null;
    private boolean isLoaded = false;

    public void init(Context context) {
        try {
            mTts = new TextToSpeech(context, onInitListener, "com.google.android.tts");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(Locale.US);
                isLoaded = true;

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "This Language is not supported");
                }

                mTts.setSpeechRate(.8f);
            } else {
                Log.e("error", "Initialization Failed!");
            }
        }
    };

    public void shutDown() {
        mTts.shutdown();
    }

    public void setUtteranceProgressListener(UtteranceProgressListener utteranceProgressListener) {
        mTts.setOnUtteranceProgressListener(utteranceProgressListener);
    }

    public void addToQueue(String text) {
        speak(text, TextToSpeech.QUEUE_ADD, true);
    }

    public void addSegmentsToQueue(List<TTSSegment> segments) {
        for (int i = 0; i < segments.size(); i++) {
            TTSSegment segment = segments.get(i);

            if (segment.getType().equals(TTSSegment_Type.PAUSE)) {
                pause(segment.getPauseDuration(), TextToSpeech.QUEUE_ADD, i == segments.size() - 1);
            } else if (segment.getType().equals(TTSSegment_Type.TEXT)) {
                speak(segment.getText(), TextToSpeech.QUEUE_ADD, i == segments.size() - 1);
            }
        }
    }

    public void flushQueue() {
        speak("", TextToSpeech.QUEUE_FLUSH, true);
    }

    private void speak(String text, int type, boolean finished) {
        if (isLoaded) {
            mTts.speak(text, type, null, finished ? GameConstants.TTS_UTTERANCE_FINISHED : null);
        } else {
            Log.e("error", "TTS Not Initialized");
        }
    }

    private void pause(long duration, int type, boolean finished) {
        if (isLoaded) {
            mTts.playSilentUtterance(duration, type, finished ? GameConstants.TTS_UTTERANCE_FINISHED : null);
        } else {
            Log.e("error", "TTS Not Initialized");
        }
    }
}