package com.example.mathrpg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    TextView tvPlayerName,tvPlayerLevel,tvPlayerNextLevel,tvPlayerMaxHP,tvPlayerAttack,tvPlayerDefense,tvPlayerDifficulty;
    ImageView ivCharacter;
    SharedPreferences spProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        spProfile = getSharedPreferences("User", Context.MODE_PRIVATE);

        tvPlayerName = (TextView)findViewById(R.id.tv_player_name);
        tvPlayerLevel = (TextView)findViewById(R.id.tv_player_level);
        tvPlayerNextLevel = (TextView)findViewById(R.id.tv_player_nextlevel);
        tvPlayerMaxHP = (TextView)findViewById(R.id.tv_player_maxhp);
        tvPlayerAttack = (TextView)findViewById(R.id.tv_player_attack);
        tvPlayerDefense = (TextView)findViewById(R.id.tv_player_defense);
        tvPlayerDifficulty = (TextView)findViewById(R.id.tv_player_difficulty);
        ivCharacter = (ImageView)findViewById(R.id.iv_character);

        //TODO: get player information from database when logged in after database, login, register are complete
        if(spProfile.contains("name")){
            tvPlayerName.setText(spProfile.getString("name","ERROR: 'name' not specified"));
        }
        if(spProfile.contains("gender")){
            switch(Objects.requireNonNull(spProfile.getString("gender", "ERROR: 'gender' not specified"))){
                case "boy":
                    ivCharacter.setImageDrawable(getDrawable(R.drawable.player_male));
                    break;
                case "girl":
                    ivCharacter.setImageDrawable(getDrawable(R.drawable.player_female));
                    break;
                default:
                    ivCharacter.setImageDrawable(getDrawable(R.drawable.ic_person_placeholder));
                    break;
            }
        }
    }

    public void onClick_btnLogout(View view) {
        AlertDialog.Builder logoutAlert = new AlertDialog.Builder(this);
        logoutAlert.setCancelable(false);
        logoutAlert.setTitle("Logout");
        logoutAlert.setMessage("Are you sure you want to logout?");

        logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ProfileActivity.this, "You have logged out", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = spProfile.edit();
                editor.clear();
                editor.apply();

                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                finish();
            }
        });
        logoutAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Test successful cancellation
                Toast.makeText(ProfileActivity.this, "Test success: Logout cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        logoutAlert.show();
    }
}