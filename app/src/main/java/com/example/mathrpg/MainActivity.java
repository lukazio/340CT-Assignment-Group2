package com.example.mathrpg;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button Mbtn_help,btnStage,btnLogin;
    AudioAttributes attrs;
    SoundPool sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SFX builders
        attrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        sp = new SoundPool.Builder().setAudioAttributes(attrs).build();

        //Loading SFX
        final int helpSound = sp.load(MainActivity.this, R.raw.help_open,1);
        final int btnSound = sp.load(MainActivity.this, R.raw.click,1);

        //Buttons and their onClickListeners
        Mbtn_help=(Button)findViewById(R.id.btn_help);
        btnStage = (Button)findViewById(R.id.btn_stage);
        btnLogin = (Button)findViewById(R.id.btn_login);

        Mbtn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(helpSound,1,1,1,0,1.0f);
                Intent i = new Intent(getApplicationContext(),Instructure.class);
                startActivity(i);
            }
        });
        btnStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(btnSound,1,1,1,0,1.0f);
                Intent intent = new Intent(MainActivity.this,StageActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(btnSound,1,1,1,0,1.0f);
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //Release SoundPool when activity is done
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp.release();
    }
}