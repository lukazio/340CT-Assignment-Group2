package com.example.mathrpg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Test code to go into ProfileActivity, can remove after main menu is designed
    public void onClick_btnTest(View view) {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }
}