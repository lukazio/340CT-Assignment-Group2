package com.example.mathrpg;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;

public class BattleActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private SoundPool sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //TODO: Override to bring up pause menu instead of finishing activity
    }
}