package com.example.mathrpg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvPlayerName,tvPlayerLevel,tvPlayerNextLevel,tvPlayerMaxHP,tvPlayerAttack,tvPlayerDefense;
    private ImageView ivCharacter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);

        tvPlayerName = (TextView)findViewById(R.id.tv_player_name);
        tvPlayerLevel = (TextView)findViewById(R.id.tv_player_level);
        tvPlayerNextLevel = (TextView)findViewById(R.id.tv_player_nextlevel);
        tvPlayerMaxHP = (TextView)findViewById(R.id.tv_player_maxhp);
        tvPlayerAttack = (TextView)findViewById(R.id.tv_player_attack);
        tvPlayerDefense = (TextView)findViewById(R.id.tv_player_defense);
        ivCharacter = (ImageView)findViewById(R.id.iv_character);

        //TODO: get player information from database when logged in after database, login, register are complete
        if(prefs.contains("name")){
            tvPlayerName.setText(prefs.getString("name","ERROR: 'name' not specified"));
        }
        if(prefs.contains("gender")){
            switch(Objects.requireNonNull(prefs.getString("gender", "ERROR: 'gender' not specified"))){
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
}