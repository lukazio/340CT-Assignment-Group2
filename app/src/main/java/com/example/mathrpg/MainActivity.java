package com.example.mathrpg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button Mbtn_help,btnStage,btnLogin,btnLogout;
    AudioAttributes attrs;
    SoundPool sp;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);

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
        btnLogout = (Button)findViewById(R.id.btn_logout);

        if(prefs.contains("name")){
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            btnStage.setEnabled(true);
            btnStage.setAlpha(1.0f);
        }
        else{
            btnLogout.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            btnStage.setEnabled(false);
            btnStage.setAlpha(0.5f);
        }

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
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(btnSound,1,1,1,0,1.0f);

                AlertDialog.Builder logoutAlert = new AlertDialog.Builder(MainActivity.this);
                logoutAlert.setCancelable(false);
                logoutAlert.setTitle("Logout");
                logoutAlert.setMessage("Are you sure you want to logout?");

                logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "You have logged out", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();

                        finish();
                        startActivity(getIntent());
                    }
                });
                logoutAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Test successful cancellation
                        Toast.makeText(MainActivity.this, "Test success: Logout cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                logoutAlert.show();
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