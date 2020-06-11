package com.example.mathrpg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button Mbtn_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Test code to go into LoginActivity, can remove after main menu is designed
    public void onClick_btnTestLogin(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    //Test code to go into StageActivity, can remove after main menu is designed
    public void onClick_btnTestStage(View view) {
        Intent intent = new Intent(this,StageActivity.class);
        startActivity(intent);
    }
}