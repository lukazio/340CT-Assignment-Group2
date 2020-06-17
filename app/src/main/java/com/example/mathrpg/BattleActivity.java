package com.example.mathrpg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class BattleActivity extends AppCompatActivity {

    private ConstraintLayout clBgStage;
    private TextView tvEnemyName,tvQuestion,tvTimer;
    private ImageView ivEnemy;
    private SharedPreferences prefs;
    private MediaPlayer mp;
    private SoundPool sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);

        clBgStage = (ConstraintLayout)findViewById(R.id.cl_bg_stage);
        tvEnemyName = (TextView)findViewById(R.id.tv_enemy_name);
        tvQuestion = (TextView)findViewById(R.id.tv_question);
        tvTimer = (TextView)findViewById(R.id.tv_timer);
        ivEnemy = (ImageView)findViewById(R.id.iv_enemy);

        sp = new SoundPool.Builder().setMaxStreams(5).build();

        //TODO: This area will get enemy stats info for each stage and also player info passed from the stage selection activities, for now this is a test
        //Get enemy names
        if(getIntent().hasExtra("enemy1_name") && getIntent().hasExtra("enemy2_name") && getIntent().hasExtra("enemy3_name")){
            //If non-boss stage (e.g. not Stage 1-3 or 2-3), get 3 names of enemies
        }
        else if(getIntent().hasExtra("enemy3_name"))
            tvEnemyName.setText(getIntent().getStringExtra("enemy3_name"));
        else
            tvEnemyName.setText("Lv.X EnemyName");
        //Get enemy sprites
        if(getIntent().hasExtra("enemy1_sprite") && getIntent().hasExtra("enemy2_sprite") && getIntent().hasExtra("enemy3_sprite")){
            //If non-boss stage (e.g. not Stage 1-3 or 2-3), get 3 images of enemies
        }
        else if(getIntent().hasExtra("enemy3_sprite"))
            ivEnemy.setImageDrawable(getDrawable(getIntent().getIntExtra("enemy3_sprite",0)));
        else
            ivEnemy.setImageDrawable(getDrawable(R.drawable.stage1_1_boss));
        //Get enemy stats

        //Get stage background
        if(getIntent().hasExtra("battle_bg"))
            clBgStage.setBackground(getDrawable(getIntent().getIntExtra("battle_bg",0)));
        //Get stage battle music
        if(getIntent().hasExtra("battle_music")) {
            mp = MediaPlayer.create(this, getIntent().getIntExtra("battle_music", 0));
            mp.setLooping(true);
            mp.start();
        }

        //Only show this AlertDialog if data is passed from secret stage
        if(getIntent().hasExtra("enemy3_sprite")){
            if(getIntent().getIntExtra("enemy3_sprite",0) == R.drawable.stagesecret_boss){
                AlertDialog.Builder secretAlertBuilder = new AlertDialog.Builder(this, R.style.StoryDialogTheme);
                secretAlertBuilder.setMessage("H̵̛̘̆̽̏͗́̊̀̉͐̈̋͘͝A̷̼͙̞͍̱̩̺̱͍̙̹͍̯̦̽̔͆̎̋̀ͅH̶̡͔̭͖͑̓̄̔̂̽Ą̵̧̡͚̦̹́̽̋̇̂͗̕̚͜ͅH̶̰͉͙͎̞͎͖͛̿̊̉̆͂̽A̶̦̟͑̃̍̉H̸̨̢̛̭͔̲̬̺͕̊͐͑̄͐̋́͜A̷̢̘̺̥͈͕̟̼͔͎̪͙̬͇̗̋̈́̉̅̿͌̔͑̋̉͝H̷̤̥͖̠̫͙̠̫̹͉͓͇̗̩͛̽̌̂̿͐̚Ą̷̧̧̧̩̬͍̯̹͖̝͉͐̃̈́̓̓̄̓̄̋͌́͘̚̕͝Ḩ̷́̆͗̎̈́̀Ą̸͖̭̬̭̩̞̺̰̉̍͊̈̌̀͊̈́́͘͜͠");
                secretAlertBuilder.setCancelable(false);
                secretAlertBuilder.setPositiveButton("Impending Doom", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog secretDialog = secretAlertBuilder.create();
                Objects.requireNonNull(secretDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                secretDialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getIntExtra("enemy3_sprite",0) == R.drawable.stagesecret_boss)
            Toast.makeText(this,"THERE IS N̷̜̭̙͐Ö̶̮̲́ ̴̳̗̖͇̞͛̈́Ȩ̶̧̳͙̑͂S̷̤̰̩̎̍̾C̴̭̥͖͘Ą̸͎̹̲̔͂̕̚ͅP̴̖͋̓̌E̵̪̥̫͗̋̎̒", Toast.LENGTH_SHORT).show();
        else{
            //TODO: Override to bring up pause menu instead of finishing activity
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(getIntent().getIntExtra("enemy3_sprite",0) == R.drawable.stagesecret_boss)
            Toast.makeText(this,"C̵̡͕̫͊̍H̵͖̻͖̗̑̾̾̆̂Ẹ̶͒͐̔A̷̳̭͉̅Ț̴̨̱͉̺͗͌͛̉͝E̴̢̛̮̩͎̣͆͌̐̽R̶͙͚̾̒̔̾", Toast.LENGTH_LONG).show();

        mp.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp.seekTo(mp.getCurrentPosition());
        mp.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
        sp.release();
    }
}