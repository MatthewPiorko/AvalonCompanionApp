package com.example.matt.avaloncompanionapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class Game extends AppCompatActivity {

    private GameInstance gameInstance;
    private TextView[] missions;
    private int currentRound;

    private int[] timers;
    private boolean timerRunning;
    private long timeRemaining;

    private TTSManager ttsManager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ttsManager = new TTSManager();
        ttsManager.init(this);

        //TODO make which music file played configurable
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(true);

        Intent intent = getIntent();
        gameInstance = (GameInstance) intent.getExtras().getSerializable(GameConstants.INSTANCE_KEY);
        timers = gameInstance.getPlayersPerMission();
        currentRound = 0;
        gameInstance.setGameState(GameInstance.GameState.DELIBERATION);

        missions = new TextView[] {
                findViewById(R.id.mission1),
                findViewById(R.id.mission2),
                findViewById(R.id.mission3),
                findViewById(R.id.mission4),
                findViewById(R.id.mission5)};

        for (int i = 0; i < 5; i++) {
            missions[i].setText(String.valueOf(timers[i]));
        }

        final Chronometer timer = findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime() + (timers[currentRound] * GameConstants.TIME_PER_PLAYER));
        timer.start();
        timerRunning = true;

        timer.setOnChronometerTickListener(chronometer -> {
            if (SystemClock.elapsedRealtime() > chronometer.getBase()) {
                timer.stop();
                timerRunning = false;
                mediaPlayer.start();
            }
        });

        timer.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() > timer.getBase()) {
                if (gameInstance.getGameState().equals(GameInstance.GameState.DELIBERATION)) {
                    stopMediaPlayer();
                    timer.setBase(SystemClock.elapsedRealtime() + GameConstants.KING_ONLY_TIME);
                    timer.start();
                    timerRunning = true;
                } else if (gameInstance.getGameState().equals(GameInstance.GameState.KING_ONLY)) {
                    stopMediaPlayer();
                }
                gameInstance.advanceGameState();
            } else {
                if (timerRunning) {
                    stopMediaPlayer();
                    timer.stop();
                    timerRunning = false;
                    timeRemaining = timer.getBase() - SystemClock.elapsedRealtime();
                } else {
                    timer.setBase(SystemClock.elapsedRealtime() + timeRemaining);
                    timer.start();
                    timerRunning = true;
                }
            }
        });

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            timer.setBase(SystemClock.elapsedRealtime() + (timers[currentRound] * GameConstants.TIME_PER_PLAYER));
            timer.start();
            timerRunning = true;
            gameInstance.resetGameState();
        });

        Button success = findViewById(R.id.success);
        success.setOnClickListener(view -> {
            updateMission(R.drawable.mission_success);
        });

        Button fail = findViewById(R.id.fail);
        fail.setOnClickListener(view -> {
            updateMission(R.drawable.mission_failed);
        });
    }

    private void stopMediaPlayer() {
        mediaPlayer.stop();
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("error", "Could not prepare media player");
        }
    }

    private void updateMission(int status) {
        final Chronometer timer = findViewById(R.id.timer);

        missions[currentRound].setBackgroundResource(status);
        currentRound++;
        if (currentRound < 5) {
            missions[currentRound].setBackgroundResource(R.drawable.mission_current);

            timer.setBase(SystemClock.elapsedRealtime() + (timers[currentRound] * 60 * 1000));
            timer.start();
            timerRunning = true;
        } else {
            //TODO handle end game properly
            startActivity(new Intent(Game.this, MainMenu.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        gameInstance.resetGameState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }
}
