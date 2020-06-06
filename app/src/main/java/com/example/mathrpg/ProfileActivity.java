package com.example.mathrpg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    TextView tvPlayerName,tvPlayerLevel,tvPlayerNextLevel,tvPlayerMaxHP,tvPlayerAttack,tvPlayerDefense,tvPlayerDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvPlayerName = (TextView)findViewById(R.id.tv_player_name);
        tvPlayerLevel = (TextView)findViewById(R.id.tv_player_level);
        tvPlayerNextLevel = (TextView)findViewById(R.id.tv_player_nextlevel);
        tvPlayerMaxHP = (TextView)findViewById(R.id.tv_player_maxhp);
        tvPlayerAttack = (TextView)findViewById(R.id.tv_player_attack);
        tvPlayerDefense = (TextView)findViewById(R.id.tv_player_defense);
        tvPlayerDifficulty = (TextView)findViewById(R.id.tv_player_difficulty);
    }

    //TODO: get player information from database when logged in after database, login, register are complete

    public void onClick_btnLogout(View view) {
        AlertDialog.Builder logoutAlert = new AlertDialog.Builder(this);
        logoutAlert.setCancelable(false);
        logoutAlert.setTitle("Logout");
        logoutAlert.setMessage("Are you sure you want to logout?");

        logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ProfileActivity.this, "You have logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        logoutAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Test successful cancellation
                Toast.makeText(ProfileActivity.this, "Logout cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        logoutAlert.show();
    }
}