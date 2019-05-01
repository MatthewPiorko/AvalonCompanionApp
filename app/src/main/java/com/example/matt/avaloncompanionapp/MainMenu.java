package com.example.matt.avaloncompanionapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);

        try {
            getActionBar().hide();
        } catch (NullPointerException npe) {
            Log.e("error", "Could not find action bar");
        }

        Button startGame = findViewById(R.id.start_game);
        startGame.setOnClickListener(view -> {
            startActivity(new Intent(MainMenu.this, CharacterSelect.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Button settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> Snackbar.make(view, "Not made yet!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}
