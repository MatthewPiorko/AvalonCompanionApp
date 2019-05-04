package com.example.matt.avaloncompanionapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            Log.e("error", "Could not find action bar");
        } else {
            actionBar.hide();
        }

        Button startGame = findViewById(R.id.start_game);
        startGame.setOnClickListener(view -> {
            startActivity(new Intent(MainMenu.this, CharacterSelect.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Button settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            startActivity(new Intent(MainMenu.this, Settings.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}
