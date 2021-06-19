package com.example.matt.avaloncompanionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import java.io.IOException;

public class CharacterSelect extends AppCompatActivity {
    private TTSManager ttsManager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_character_select);

        ttsManager = new TTSManager();
        ttsManager.init(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.night_human);

        Resources resources = getResources();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String voice = settings.getString("voice", "");

        Button nightPhase = findViewById(R.id.night_phase);
        Button startGame = findViewById(R.id.start_game);

        nightPhase.setOnClickListener(view -> {
            Boolean isRobot = voice.equals(resources.getString(R.string.settings_voice_robot_id));
            Boolean isHuman = voice.equals(resources.getString(R.string.settings_voice_human_id));

            if (nightPhase.getText().toString().equals(getResources().getString(R.string.tts_stop))) {
                startGame.setEnabled(true);
                nightPhase.setText(R.string.character_select_night_phase);
                if (isRobot) {
                    ttsManager.flushQueue();
                } else if (isHuman) {
                    mediaPlayer.stop();

                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        Log.e("error", "Could not play file");
                    }
                }
            } else {
                nightPhase.setText(R.string.tts_stop);
                startGame.setEnabled(false);

                if (isRobot) {
                    GameInstance gameInstance = createGameInstance(settings, resources);
                    long longPauseDuration = Long.parseLong(settings.getString(resources.getString(R.string.settings_voice_long_pause_duration_id),
                            String.valueOf(resources.getInteger(R.integer.default_long_pause_duration)))) * GameConstants.MILLIS_IN_SECOND;
                    long shortPauseDuration = Long.parseLong(settings.getString(resources.getString(R.string.settings_voice_short_pause_duration_id),
                            String.valueOf(resources.getInteger(R.integer.default_short_pause_duration)))) * GameConstants.MILLIS_IN_SECOND;

                    ttsManager.addSegmentsToQueue(gameInstance.createNightPhaseSpeech(longPauseDuration, shortPauseDuration));
                    ttsManager.setUtteranceProgressListener(createUtteranceListener());
                } else if (isHuman) {
                    mediaPlayer.start();
                }
            }
        });

        startGame.setOnClickListener(view -> {
            Intent intent = new Intent(CharacterSelect.this, Game.class);

            intent.putExtra(GameConstants.TTS_INSTANCE_KEY, createGameInstance(settings, resources));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        RadioGroup playerSelector = findViewById(R.id.num_players);
        playerSelector.setOnCheckedChangeListener((radioGroup, i) -> {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }

    public void onToggle(View view) {
        ((RadioGroup) view.getParent()).check(view.getId());
    }

    private UtteranceProgressListener createUtteranceListener() {
        Button nightPhase = findViewById(R.id.night_phase);
        Button startGame = findViewById(R.id.start_game);

        return new UtteranceProgressListener() {
            @Override
            public void onStart(String s) { }

            @Override
            public void onDone(String s) {
                if (s != null && s.equals(GameConstants.TTS_UTTERANCE_FINISHED)) {
                    runOnUiThread(() -> {
                        startGame.setEnabled(true);
                        nightPhase.setText(getResources().getString(R.string.character_select_night_phase));
                    });
                }
            }

            public void onError(String s) { }
        };
    }

    private GameInstance createGameInstance(SharedPreferences settings, Resources resources) {
        ToggleButton merlin = findViewById(R.id.merlin);
        ToggleButton percival = findViewById(R.id.percival);
        ToggleButton lovers = findViewById(R.id.lovers);
        ToggleButton assassin = findViewById(R.id.assassin);
        ToggleButton morgana = findViewById(R.id.morgana);
        ToggleButton mordred = findViewById(R.id.mordred);
        ToggleButton oberon = findViewById(R.id.oberon);

        RadioGroup playerSelector = findViewById(R.id.num_players);
        ToggleButton numPlayersButton = findViewById(playerSelector.getCheckedRadioButtonId());

        String timer = settings.getString("timer", resources.getString(R.string.settings_timer_full_id));

        return new GameInstance(
                resources,
                Integer.parseInt(numPlayersButton.getTextOn().toString()),
                merlin.isChecked(),
                percival.isChecked(),
                lovers.isChecked(),
                assassin.isChecked(),
                morgana.isChecked(),
                mordred.isChecked(),
                oberon.isChecked(),
                timer
        );
    }
}
