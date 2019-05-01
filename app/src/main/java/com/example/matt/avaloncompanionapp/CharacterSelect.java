package com.example.matt.avaloncompanionapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

        Button nightPhaseBot = findViewById(R.id.night_phase_bot);
        Button nightPhaseHuman = findViewById(R.id.night_phase_human);
        Button startGame = findViewById(R.id.start_game);

        nightPhaseBot.setOnClickListener(view -> {
            if (nightPhaseBot.getText().toString().equals(getResources().getString(R.string.tts_stop))) {
                startGame.setEnabled(true);
                nightPhaseHuman.setEnabled(true);
                nightPhaseBot.setText(R.string.character_select_night_phase_bot);
                ttsManager.flushQueue();
            } else {
                GameInstance gameInstance = createGameInstance();
                ttsManager.addSegmentsToQueue(gameInstance.createNightPhaseSpeech());
                nightPhaseBot.setText(R.string.tts_stop);
                startGame.setEnabled(false);
                nightPhaseHuman.setEnabled(false);

                ttsManager.setUtteranceProgressListener(createUtteranceListener());
            }
        });

        nightPhaseHuman.setOnClickListener(view -> {
            if (nightPhaseHuman.getText().toString().equals(getResources().getString(R.string.tts_stop))) {
                startGame.setEnabled(true);
                nightPhaseBot.setEnabled(true);
                nightPhaseHuman.setText(R.string.character_select_night_phase_human);
                mediaPlayer.stop();
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    Log.e("error", "Could not play file");
                }
            } else {
                mediaPlayer.start();
                startGame.setEnabled(false);
                nightPhaseBot.setEnabled(false);
                nightPhaseHuman.setText(R.string.tts_stop);
            }
        });

        startGame.setOnClickListener(view -> {
            Intent intent = new Intent(CharacterSelect.this, Game.class);

            intent.putExtra(GameConstants.INSTANCE_KEY, createGameInstance());
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
        Button nightPhaseBot = findViewById(R.id.night_phase_bot);
        Button nightPhaseHuman = findViewById(R.id.night_phase_human);
        Button startGame = findViewById(R.id.start_game);

        return new UtteranceProgressListener() {
            @Override
            public void onStart(String s) { }

            @Override
            public void onDone(String s) {
                if (s != null && s.equals(GameConstants.UTTERANCE_FINISHED)) {
                    runOnUiThread(() -> {
                        startGame.setEnabled(true);
                        nightPhaseBot.setText(getResources().getString(R.string.character_select_night_phase_bot));
                        nightPhaseHuman.setText(getResources().getString(R.string.character_select_night_phase_human));
                    });
                }
            }

            public void onError(String s) { }
        };
    }

    private GameInstance createGameInstance() {
        ToggleButton merlin = findViewById(R.id.merlin);
        ToggleButton percival = findViewById(R.id.percival);
        ToggleButton assassin = findViewById(R.id.assassin);
        ToggleButton morgana = findViewById(R.id.morgana);
        ToggleButton mordred = findViewById(R.id.mordred);
        ToggleButton oberon = findViewById(R.id.oberon);

        RadioGroup playerSelector = findViewById(R.id.num_players);
        ToggleButton numPlayersButton = findViewById(playerSelector.getCheckedRadioButtonId());

        return new GameInstance(
                Integer.valueOf(numPlayersButton.getTextOn().toString()),
                merlin.isChecked(),
                percival.isChecked(),
                assassin.isChecked(),
                morgana.isChecked(),
                mordred.isChecked(),
                oberon.isChecked()
        );
    }
}
