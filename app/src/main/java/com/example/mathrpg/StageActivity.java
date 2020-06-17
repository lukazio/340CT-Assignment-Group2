package com.example.mathrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.interfaces.RSAKey;
import java.util.Objects;

public class StageActivity extends AppCompatActivity {

    private ImageView ivStagePlayer,btnProfile;
    private TextView tvPlayerStageName,tvPlayerStageLevel;
    private SharedPreferences prefs;
    private SoundPool sp;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        sp = new SoundPool.Builder().setMaxStreams(5).build();
        final int profileSound = sp.load(StageActivity.this, R.raw.profile_open,1);
        final int pageSound = sp.load(StageActivity.this, R.raw.stage_swipe,1);

        ivStagePlayer = (ImageView)findViewById(R.id.iv_stage_player);
        btnProfile = (ImageView)findViewById(R.id.btn_profile);
        tvPlayerStageName = (TextView)findViewById(R.id.tv_player_stage_name);
        tvPlayerStageLevel = (TextView)findViewById(R.id.tv_player_stage_level);

        //Get level when it is stored in database
        db.collection("Users").document(prefs.getString("uid","NO_UID_FOUND")).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        //Updating into local SharedPreferences on each Stages click
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("level",task.getResult().getLong("level").intValue());
                        editor.putInt("attack",task.getResult().getLong("attack").intValue());
                        editor.putInt("defense",task.getResult().getLong("defense").intValue());
                        editor.putInt("exp",task.getResult().getLong("exp").intValue());
                        editor.putInt("progress",task.getResult().getLong("progress").intValue());
                        editor.apply();

                        //TODO: level is already read from database, use it from SharedPreferences to conduct necessary action.
                        //TODO: Firebase is async, subsequent actions have to be called in here.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),e.toString(),Snackbar.LENGTH_LONG).show();
                    }
                });


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
                sp.play(pageSound,1,1,1,0,1.0f);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(profileSound,1,1,1,0,1.0f);
                Intent intent = new Intent(StageActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp.release();
    }
}