package com.example.mathrpg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class StageActivity extends AppCompatActivity {

    ImageView ivStagePlayer;
    TextView tvPlayerStageName,tvPlayerStageLevel;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);

        ivStagePlayer = (ImageView)findViewById(R.id.iv_stage_player);
        tvPlayerStageName = (TextView)findViewById(R.id.tv_player_stage_name);
        tvPlayerStageLevel = (TextView)findViewById(R.id.tv_player_stage_level);

        //TODO: get level when it is stored in database
        if(prefs.contains("name")){
            tvPlayerStageName.setText(prefs.getString("name","ERROR: 'name' not specified"));
        }
        if(prefs.contains("gender")){
            switch(Objects.requireNonNull(prefs.getString("gender", "ERROR: 'gender' not specified"))){
                case "boy":
                    ivStagePlayer.setImageDrawable(getDrawable(R.drawable.player_male));
                    break;
                case "girl":
                    ivStagePlayer.setImageDrawable(getDrawable(R.drawable.player_female));
                    break;
                default:
                    ivStagePlayer.setImageDrawable(getDrawable(R.drawable.ic_person_placeholder));
                    break;
            }
        }

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tl_stage);
        tabLayout.addTab(tabLayout.newTab().setText("Stage 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Stage 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Stage 3"));

        final ViewPager viewPager = (ViewPager)findViewById(R.id.vp_stage);
        StagePagerAdapter mPagerAdapter = new StagePagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(mPagerAdapter);
        
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void onClick_btnProfile(View view) {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }
}