package com.example.mathrpg;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class BattleActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 15000; //for countdown timer
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private ConstraintLayout clBgStage;
    private TextView tvEnemyName,tvQuestion,tvTimer,tvBarHpValue,hpBar,tvCombo,tvTurn,tvBarEneHpValue,eneHpBar,tvEnemyDamage,tvPlayerDamage;
    private ImageView ivEnemy;
    private SharedPreferences prefs;
    private MediaPlayer mp;
    private SoundPool sp;
    private Button btnPause,btnAns1,btnAns2,btnAns3,btnBeginTurn;
    private AlertDialog.Builder pauseAlertBuilder;
    private AlertDialog pauseDialog;
    private Guideline hpGuideline,eneHpGuideline;

    //Battle stage variables (player's current HP, monster stats, stage EXP, etc.)
    private int currentHp,maxHp,playerAttack,currentEneHp;
    private double totalDmg,dmgTaken;
    private Enemy enemy1,enemy2,enemy3;

    //Battle gameplay variables (correct answer button, turn, etc.)
    private int answerButton,combo,round=1;
    private boolean playerTurn;

    //Damage splash
    private Handler mHandler = new Handler();
    private Animation myanim;

    //Firebase
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        clBgStage = (ConstraintLayout)findViewById(R.id.cl_bg_stage);
        tvEnemyName = (TextView)findViewById(R.id.tv_enemy_name);
        tvQuestion = (TextView)findViewById(R.id.tv_question);
        tvTimer = (TextView)findViewById(R.id.tv_timer);
        tvCombo = (TextView)findViewById(R.id.tv_combo);
        tvTurn = (TextView) findViewById(R.id.tv_turn);
        tvBarHpValue = (TextView)findViewById(R.id.tv_bar_hp_value);
        hpBar = (TextView)findViewById(R.id.hp_bar);
        eneHpBar = (TextView)findViewById(R.id.ene_hp_bar);
        tvBarEneHpValue = (TextView)findViewById(R.id.tv_bar_ene_hp_value);
        tvEnemyDamage = (TextView)findViewById(R.id.tv_enemy_damage);
        tvPlayerDamage = (TextView)findViewById(R.id.tv_player_damage);
        ivEnemy = (ImageView)findViewById(R.id.iv_enemy);
        btnPause = (Button)findViewById(R.id.btn_pause);
        btnAns1 = (Button)findViewById(R.id.btn_ans1);
        btnAns2 = (Button)findViewById(R.id.btn_ans2);
        btnAns3 = (Button)findViewById(R.id.btn_ans3);
        hpGuideline = (Guideline)findViewById(R.id.guideline_hp);
        eneHpGuideline = (Guideline)findViewById(R.id.guideline_ene_hp);
        btnBeginTurn = (Button)findViewById(R.id.btn_begin);

        tvQuestion.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);
        tvCombo.setVisibility(View.GONE);
        tvTurn.setVisibility(View.GONE);

        //Set battle variables (player's current HP, monster stats, stage EXP etc.)
        currentHp = maxHp = prefs.getInt("hp",1);
        playerAttack = prefs.getInt("attack", 0);
        totalDmg = 0;
        updatePlayerHealthBar();
        enemy1 = new Enemy();
        enemy2 = new Enemy();
        enemy3 = new Enemy();

        //Sound effects
        sp = new SoundPool.Builder().setMaxStreams(5).build();
        final int beginSound = sp.load(this, R.raw.player_begin,1);
        final int correctSound = sp.load(this, R.raw.ans_correct,1);
        final int guardSound = sp.load(this, R.raw.guard,1);
        final int attackSound = sp.load(this, R.raw.player_attack,1);
        final int failSound = sp.load(this, R.raw.player_fail,1);
        final int hurtSound = sp.load(this, R.raw.player_hurt,1);
        final int secretBossIntro = sp.load(this, R.raw.secretboss_intro,1);

        //Damage splash effect
        myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);

        //set up countdown timer
        btnBeginTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.play(beginSound,1,1,1,0,1.0f);
                totalDmg = 0;
                dmgTaken = 0;
                combo = 0;
                btnBeginTurn.setVisibility(View.GONE);  //Hide button
                startTimer(attackSound,failSound);   //Start timer countdown
                updateCountDownText();
                playerTurn = true;  //Start player turn
                generateQuestion(); //Start generating question
                btnAns1.setVisibility(View.VISIBLE);
                btnAns2.setVisibility(View.VISIBLE);
                btnAns3.setVisibility(View.VISIBLE);
                tvQuestion.setVisibility(View.VISIBLE);
                tvTimer.setVisibility(View.VISIBLE);
                updateTurnLabel();
            }
        });

//        btnreset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopTimer();
//                resetTimer();
//                startTimer();
//            }
//        });

        pauseAlertBuilder = new AlertDialog.Builder(this, R.style.StoryDialogTheme);

        //TODO: Test answering questions, move to code proper location when implementing turn-based battle mechanic
        btnAns1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerButton == 1) {
                    if(playerTurn)
                        onCorrect(correctSound);
                    else
                        onCorrect(guardSound);
                }
                else {
                    if(playerTurn)
                        onWrong(attackSound,failSound);
                    else
                        onWrong(hurtSound,0);
                }
            }
        });
        btnAns2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerButton == 2) {
                    if(playerTurn)
                        onCorrect(correctSound);
                    else
                        onCorrect(guardSound);
                }
                else {
                    if(playerTurn)
                        onWrong(attackSound,failSound);
                    else
                        onWrong(hurtSound,0);
                }
            }
        });
        btnAns3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerButton == 3) {
                    if(playerTurn)
                        onCorrect(correctSound);
                    else
                        onCorrect(guardSound);
                }
                else {
                    if(playerTurn)
                        onWrong(attackSound,failSound);
                    else
                        onWrong(hurtSound,0);
                }
            }
        });

        //TODO: This area will get enemy stats info for each stage and also player info passed from the stage selection activities
        updateEnemyDisplay();
        //Get enemy stats (HP and Attack)
        if(getIntent().hasExtra("enemy1_hp") && getIntent().hasExtra("enemy2_hp") && getIntent().hasExtra("enemy3_hp")){
            enemy1.setStats(getIntent().getIntExtra("enemy1_hp",1), getIntent().getIntExtra("enemy1_attack",0));
            enemy2.setStats(getIntent().getIntExtra("enemy2_hp",1), getIntent().getIntExtra("enemy2_attack",0));
            enemy3.setStats(getIntent().getIntExtra("enemy3_hp",1), getIntent().getIntExtra("enemy3_attack",0));
        }
        else if(getIntent().hasExtra("enemy3_hp")){
            enemy3.setStats(getIntent().getIntExtra("enemy3_hp",1), getIntent().getIntExtra("enemy3_attack",0));
            round = 3;
        }
        //Get stage background
        if(getIntent().hasExtra("battle_bg"))
            clBgStage.setBackground(getDrawable(getIntent().getIntExtra("battle_bg",0)));
        //Get stage battle music
        if(getIntent().hasExtra("battle_music")) {
            mp = MediaPlayer.create(this, getIntent().getIntExtra("battle_music", 0));
            mp.setLooping(true);
            mp.setVolume(0.75f,0.75f);
            mp.start();
        }

        //Show first enemy onstartup
        if(getIntent().hasExtra("enemy1_hp") && getIntent().hasExtra("enemy2_hp") && getIntent().hasExtra("enemy3_hp"))
            currentEneHp = enemy1.getHp();
        else if(getIntent().hasExtra("enemy3_hp"))
            currentEneHp = enemy3.getHp();
        updateEnemyHealthBar();

        //Only show this AlertDialog if data is passed from secret stage
        if(getIntent().hasExtra("enemy3_sprite")){
            if(getIntent().getIntExtra("enemy3_sprite",0) == R.drawable.stagesecret_boss){
                AlertDialog.Builder secretAlertBuilder = new AlertDialog.Builder(this, R.style.StoryDialogTheme);
                secretAlertBuilder.setMessage("H̵̛̘̆̽̏͗́̊̀̉͐̈̋͘͝A̷̼͙̞͍̱̩̺̱͍̙̹͍̯̦̽̔͆̎̋̀ͅH̶̡͔̭͖͑̓̄̔̂̽Ą̵̧̡͚̦̹́̽̋̇̂͗̕̚͜ͅH̶̰͉͙͎̞͎͖͛̿̊̉̆͂̽A̶̦̟͑̃̍̉H̸̨̢̛̭͔̲̬̺͕̊͐͑̄͐̋́͜A̷̢̘̺̥͈͕̟̼͔͎̪͙̬͇̗̋̈́̉̅̿͌̔͑̋̉͝H̷̤̥͖̠̫͙̠̫̹͉͓͇̗̩͛̽̌̂̿͐̚Ą̷̧̧̧̩̬͍̯̹͖̝͉͐̃̈́̓̓̄̓̄̋͌́͘̚̕͝Ḩ̷́̆͗̎̈́̀Ą̸͖̭̬̭̩̞̺̰̉̍͊̈̌̀͊̈́́͘͜͠");
                secretAlertBuilder.setCancelable(false);
                secretAlertBuilder.setPositiveButton("Impending Doom", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(secretBossIntro,1,1,1,0,1.0f);
                    }
                });
                AlertDialog secretDialog = secretAlertBuilder.create();
                Objects.requireNonNull(secretDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                secretDialog.show();
            }
        }

        //Pause function
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getIntExtra("enemy3_sprite",0) == R.drawable.stagesecret_boss)
                    Toast.makeText(BattleActivity.this, "THERE IS N̷̜̭̙͐Ö̶̮̲́ ̴̳̗̖͇̞͛̈́Ȩ̶̧̳͙̑͂S̷̤̰̩̎̍̾C̴̭̥͖͘Ą̸͎̹̲̔͂̕̚ͅP̴̖͋̓̌E̵̪̥̫͗̋̎̒", Toast.LENGTH_SHORT).show();
                else
                    showPauseMenu();
            }
        });
    } //END OF onCreate

    //On answering correctly
    private void onCorrect(int sound){
        //When it's player's turn
        if(playerTurn) {
           // Toast.makeText(BattleActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
            sp.play(sound,1,1,1,0,1.0f);
            combo += 1;
            if(combo==1){
                tvCombo.setVisibility(View.VISIBLE);
            }
            tvCombo.setText(combo + " COMBO!");
            //TODO: Round off value(if needed)
            totalDmg = (1.0 + 0.2*(combo-1))*playerAttack;
            generateQuestion();
        }

        //When it's enemy's turn
        else{
            sp.play(sound,1,1,1,0,1.0f);
            tvPlayerDamage.setBackground(getDrawable(R.drawable.player_guard));

            //Calculated reduced dmg taken here
            switch (round){
                case 1: dmgTaken = (enemy1.getAttack() - prefs.getInt("defense",3)) / 2;
                break;
                case 2: dmgTaken = (enemy2.getAttack() - prefs.getInt("defense",3)) / 2;
                break;
                case 3: dmgTaken = (enemy3.getAttack() - prefs.getInt("defense",3)) / 2;
                break;
                default:
            }

            showEndTurnDialog(0,(int)dmgTaken);
            playerTurn = true;
            updateTurnLabel();
        }

    }

    //On answering wrongly
    private void onWrong(int sound, int failSound){
        //When it's player's turn
        if(playerTurn) {
         //   Toast.makeText(BattleActivity.this, "Wrong...It's enemy's turn now", Toast.LENGTH_SHORT).show();
            if(combo > 0)
                sp.play(sound,1,1,1,0,1.0f);
            else
                sp.play(failSound,1,1,1,0,1.0f);

            showEndTurnDialog((int)totalDmg,0);
            playerTurn = false;
            updateTurnLabel();
            combo = 0;
            tvCombo.setVisibility(View.GONE);
        }

        //When it's enemy's turn
        else{
            sp.play(sound,1,1,1,0,1.0f);
            tvPlayerDamage.setBackground(getDrawable(R.drawable.player_hurt));

            //Inserted full damage taken value here
            switch (round){
                case 1: dmgTaken = enemy1.getAttack() - prefs.getInt("defense",3);
                    break;
                case 2: dmgTaken = enemy2.getAttack() - prefs.getInt("defense",3);
                    break;
                case 3: dmgTaken = enemy3.getAttack() - prefs.getInt("defense",3);
                    break;
                default:
            }

            showEndTurnDialog(0,(int)dmgTaken);
            playerTurn = true;
            updateTurnLabel();
        }
    }

    //END TURN dialog
    private void showEndTurnDialog(final int dmgMade, int dmgReceived) {
        stopTimer();
        resetTimer();

        AlertDialog.Builder dialog = new AlertDialog.Builder(BattleActivity.this);
        dialog.setCancelable(false);

        if (playerTurn) {
            btnAns1.setVisibility(View.GONE);
            btnAns2.setVisibility(View.GONE);
            btnAns3.setVisibility(View.GONE);
            tvQuestion.setVisibility(View.GONE);
            tvTimer.setVisibility(View.GONE);
            tvTurn.setVisibility(View.GONE);

            //dialog.setTitle("End of your Turn");
            //dialog.setMessage("\nTotal damage dealt: " + dmgMade + "\nDefend yourself now!");
            //Reduced monster hp here
            currentEneHp = currentEneHp - dmgMade;
            tvCombo.setVisibility(View.GONE);
            updateEnemyHealthBar();

            tvEnemyDamage.setText(""+dmgMade);
            tvEnemyDamage.setVisibility(View.VISIBLE);
            tvEnemyDamage.startAnimation(myanim);

            if(currentEneHp > 0) {
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        tvEnemyDamage.setVisibility(View.INVISIBLE);
                        startNew();
                    }
                }, 1500);
            }

            /*
            dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startNew();
                }
            });
            dialog.show();
             */
        }
        else {
            btnAns1.setVisibility(View.GONE);
            btnAns2.setVisibility(View.GONE);
            btnAns3.setVisibility(View.GONE);
            tvQuestion.setVisibility(View.GONE);
            tvTimer.setVisibility(View.GONE);
            tvTurn.setVisibility(View.GONE);

            if(dmgReceived < 1)
                dmgReceived = 1;

            currentHp = currentHp - dmgReceived;
            boolean isGameOver = updatePlayerHealthBar();

            if(isGameOver){
                dialog.setTitle("GAME OVER");
                //dialog.setCancelable(false);
                dialog.setMessage("Better luck next time!");
                dialog.setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        BattleActivity.super.onBackPressed();
                    }
                });
                dialog.show();
            }
            else{
                tvPlayerDamage.setText(""+dmgReceived);
                tvPlayerDamage.setVisibility(View.VISIBLE);
                tvPlayerDamage.startAnimation(myanim);

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        btnBeginTurn.setVisibility(View.VISIBLE);
                        tvPlayerDamage.setVisibility(View.INVISIBLE);
                    }
                },1500);
                //dialog.setTitle("End of enemy's Turn");
                //dialog.setMessage("You've received " + dmgReceived + " damage from the enemy's attack!");
                //dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                //    @Override
                //    public void onClick(DialogInterface dialog, int which) {
                //        dialog.dismiss();
                //    }
                //});
            }
        }

    }

    //Start new turn
    private void startNew(){
        generateQuestion();
        startTimer(0,0);
        totalDmg = 0;
        dmgTaken = 0;
        combo = 0;

        btnAns1.setVisibility(View.VISIBLE);
        btnAns2.setVisibility(View.VISIBLE);
        btnAns3.setVisibility(View.VISIBLE);
        tvQuestion.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.VISIBLE);
        tvTurn.setVisibility(View.VISIBLE);
    }

    //Function to update Turn Label
    private void updateTurnLabel(){
        if(playerTurn){
            tvTurn.setVisibility(View.VISIBLE);
            tvTurn.setTextColor(Color.parseColor("#42A5F5"));
            tvTurn.setText("Your Turn: Attack now!");
        }
        else{
            tvTurn.setVisibility(View.VISIBLE);
            tvTurn.setTextColor(Color.parseColor("#f54242"));
            tvTurn.setText("Enemy's Turn: Defend yourself now!");
        }
    }

    //start countdown timer
    private void startTimer(final int succSound, final int failSound) {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                //When it's player's turn
                if(playerTurn){
                    if(combo > 0)
                        sp.play(succSound,1,1,1,0,1.0f);
                    else
                        sp.play(failSound,1,1,1,0,1.0f);

                    showEndTurnDialog((int)totalDmg,0);
                    playerTurn = false;
                    updateTurnLabel();
                }

                //When it's enemy's turn
                else{
                    tvPlayerDamage.setBackground(getDrawable(R.drawable.player_hurt));
                    //Inserted full damage taken value
                    switch (round){
                        case 1: dmgTaken = enemy1.getAttack() - prefs.getInt("defense",3);
                            break;
                        case 2: dmgTaken = enemy2.getAttack() - prefs.getInt("defense",3);
                            break;
                        case 3: dmgTaken = enemy3.getAttack() - prefs.getInt("defense",3);
                            break;
                        default:
                    }
                    showEndTurnDialog(0,(int)dmgTaken);
                    playerTurn = true;
                    updateTurnLabel();
                }

            }
        }.start();
        mTimerRunning = true;
    }

    //stop timer before reset
    private void stopTimer(){
        mCountDownTimer.cancel();
    }

    //pause timer
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    //reset countdown timer for new round
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    //update timer text
    private void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    //Shows the pause menu with 2 buttons Resume and Quit
    public void showPauseMenu() {
        pauseAlertBuilder.setTitle("Pause");
        pauseAlertBuilder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        pauseAlertBuilder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mCountDownTimer != null)
                    mCountDownTimer.cancel();

                mTimerRunning = false;
                BattleActivity.super.onBackPressed();
                BattleActivity.this.finish();
            }
        });
        pauseDialog = pauseAlertBuilder.create();
        Objects.requireNonNull(pauseDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6E2C00")));
        pauseDialog.show();
    }

    //Update the HP bar's length and colour according to player's health level
    public boolean updatePlayerHealthBar(){
        if(currentHp < 0)
            currentHp = 0;

        if((float)currentHp/maxHp <= 0.25)
            hpBar.setBackgroundColor(Color.parseColor("#E53935"));
        else if((float)currentHp/maxHp > 0.25 && (float)currentHp/maxHp <= 0.5)
            hpBar.setBackgroundColor(Color.parseColor("#FBC02D"));
        else
            hpBar.setBackgroundColor(Color.parseColor("#43A047"));

        hpGuideline.setGuidelinePercent((float)currentHp/maxHp);
        tvBarHpValue.setText(currentHp + " / " + maxHp);

        if(currentHp<=0)
            return true;
        else
            return false;
    }

    //TODO:Update the HP bar's length and colour according to monster's health level
    public void updateEnemyHealthBar(){
        //If enemy died
        if(currentEneHp <= 0) {
            currentEneHp = 0;
            changeRound();
        }

        Enemy currentEnemy = new Enemy();
        switch (round){
            case 1:{
                currentEnemy = enemy1;
                break;
            }
            case 2:{
                currentEnemy = enemy2;
                break;
            }
            case 3:{
                currentEnemy = enemy3;
                break;
            }
            default:
        }

        if((float)currentEneHp/currentEnemy.getHp() <= 0.25)
            eneHpBar.setBackgroundColor(Color.parseColor("#E53935"));
        else if((float)currentEneHp/currentEnemy.getHp() > 0.25 && (float)currentEneHp/currentEnemy.getHp() <= 0.5)
            eneHpBar.setBackgroundColor(Color.parseColor("#FBC02D"));
        else
            eneHpBar.setBackgroundColor(Color.parseColor("#43A047"));

        eneHpGuideline.setGuidelinePercent((float)currentEneHp/currentEnemy.getHp());
        tvBarEneHpValue.setText(currentEneHp + " / " + currentEnemy.getHp());
    }

    //Change Round Function
    private void changeRound(){
        Enemy newEnemy = new Enemy();
        if(round>=3)
            Toast.makeText(this, "Stage complete! +" + getIntent().getIntExtra("exp",0) + " EXP", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Next Stage...", Toast.LENGTH_SHORT).show();

        round++;
        if(round>3){
            if(mCountDownTimer != null)
                mCountDownTimer.cancel();

            mTimerRunning = false;

            processProgress();
            processExp();

        }
        else {
            updateEnemyDisplay();
            switch (round){
                case 1: newEnemy = enemy1;
                    break;
                case 2: newEnemy = enemy2;
                    break;
                case 3: newEnemy = enemy3;
                    break;
                default:
            }
            currentEneHp = newEnemy.getHp();
            updateEnemyHealthBar();
        }
    }

    //Update player's stage progress
    public void processProgress(){
        if(prefs.getInt("progress",0) < getIntent().getIntExtra("progress",0)){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("progress", getIntent().getIntExtra("progress",0));
            editor.apply();
        }
    }

    //Global dismissable Syncing AlertDialog
    private Dialog syncDialog;
    //Give player EXP and process level up after every stage
    public void processExp(){

        //Dialog Layout
        LinearLayout ll = new LinearLayout(BattleActivity.this);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(32,32,32,32);
        ProgressBar pb = new ProgressBar(BattleActivity.this);
        ll.addView(pb);

        AlertDialog.Builder syncingDialog = new AlertDialog.Builder(BattleActivity.this);
        syncingDialog.setTitle("Syncing Data")
                .setCancelable(false)
                .setMessage("Syncing Data to Cloud...Please wait...")
                .setView(ll);

        syncDialog = syncingDialog.create();
        syncDialog.show();

        int currentExp = prefs.getInt("exp",0) + getIntent().getIntExtra("exp",0);

        if(prefs.getInt("level",1) < 20){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("exp", currentExp);
            editor.apply();
        }

        //Level up
        if(prefs.getInt("exp",0) >= prefs.getInt("level",1)*10 && prefs.getInt("level",1) < 20){
            int currLevel = prefs.getInt("level",1) + 1;
            int currHp = prefs.getInt("hp",10) + 4;
            int currAttack = prefs.getInt("attack",3) + 2;
            int currDefense = prefs.getInt("defense",1) + 1;

            SharedPreferences.Editor levelupEditor = prefs.edit();
            levelupEditor.putInt("exp", 0);
            levelupEditor.putInt("level", currLevel);
            levelupEditor.putInt("hp", currHp);
            levelupEditor.putInt("attack", currAttack);
            levelupEditor.putInt("defense", currDefense);
            levelupEditor.apply();
        }

        db.collection("Users").document(prefs.getString("uid",""))
                .update(
                        "progress",prefs.getInt("progress",0),
                        "exp",prefs.getInt("exp",0),
                        "level", prefs.getInt("level",0),
                        "hp", prefs.getInt("hp",0),
                        "attack", prefs.getInt("attack",0),
                        "defense", prefs.getInt("defense",0)
                        )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        syncDialog.dismiss();
                        Intent intent = new Intent(BattleActivity.this, StageActivity.class);
                        startActivity(intent);
                        BattleActivity.super.onBackPressed();
                        BattleActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BattleActivity.this, "Syncing Failed : \n"+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //Generate a random math question and its answers
    public void generateQuestion(){
        int a;
        int x = ThreadLocalRandom.current().nextInt(1,11);
        int y = ThreadLocalRandom.current().nextInt(1,11);

        if(ThreadLocalRandom.current().nextBoolean()){
            a = x+y;
            tvQuestion.setText(x + " + " + y);
        }
        else{
            a = x-y;
            tvQuestion.setText(x + " - " + y);
        }

        answerButton = ThreadLocalRandom.current().nextInt(1,4);
        switch(answerButton){
            case 1:
                btnAns1.setText("" + a);
                btnAns2.setText("" + (a+1));
                btnAns3.setText("" + (a+2));
                break;
            case 2:
                btnAns1.setText("" + (a-1));
                btnAns2.setText("" + a);
                btnAns3.setText("" + (a+1));
                break;
            case 3:
                btnAns1.setText("" + (a-2));
                btnAns2.setText("" + (a-1));
                btnAns3.setText("" + a);
                break;
            default:
        }
    }

    //Update enemy name and image
    public void updateEnemyDisplay(){
        //Get enemy names
        if(getIntent().hasExtra("enemy1_name") && getIntent().hasExtra("enemy2_name") && getIntent().hasExtra("enemy3_name")){
            //If non-boss stage (e.g. not Stage 1-3 or 2-3), get 3 names of enemies
            switch(round){
                case 1:
                    tvEnemyName.setText(getIntent().getStringExtra("enemy1_name"));
                    break;
                case 2:
                    tvEnemyName.setText(getIntent().getStringExtra("enemy2_name"));
                    break;
                case 3:
                    tvEnemyName.setText(getIntent().getStringExtra("enemy3_name"));
                    break;
            }
        }
        else if(getIntent().hasExtra("enemy3_name"))
            tvEnemyName.setText(getIntent().getStringExtra("enemy3_name"));
        else
            tvEnemyName.setText("Lv.X EnemyName");
        //Get enemy sprites
        if(getIntent().hasExtra("enemy1_sprite") && getIntent().hasExtra("enemy2_sprite") && getIntent().hasExtra("enemy3_sprite")){
            //If non-boss stage (e.g. not Stage 1-3 or 2-3), get 3 sprites of enemies
            switch(round){
                case 1:
                    ivEnemy.setImageDrawable(getDrawable(getIntent().getIntExtra("enemy1_sprite",0)));
                    break;
                case 2:
                    ivEnemy.setImageDrawable(getDrawable(getIntent().getIntExtra("enemy2_sprite",0)));
                    break;
                case 3:
                    ivEnemy.setImageDrawable(getDrawable(getIntent().getIntExtra("enemy3_sprite",0)));
                    break;
            }
        }
        else if(getIntent().hasExtra("enemy3_sprite"))
            ivEnemy.setImageDrawable(getDrawable(getIntent().getIntExtra("enemy3_sprite",0)));
        else
            ivEnemy.setImageDrawable(getDrawable(R.drawable.stage1_1_boss));
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getIntExtra("enemy3_sprite",0) == R.drawable.stagesecret_boss) {
            //Secret boss prevents player from escaping by denying the pause menu
            Toast.makeText(this, "THERE IS N̷̜̭̙͐Ö̶̮̲́ ̴̳̗̖͇̞͛̈́Ȩ̶̧̳͙̑͂S̷̤̰̩̎̍̾C̴̭̥͖͘Ą̸͎̹̲̔͂̕̚ͅP̴̖͋̓̌E̵̪̥̫͗̋̎̒", Toast.LENGTH_SHORT).show();
        }
        else{
            showPauseMenu();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Secret boss calls player a "cheater" when attempting to quit app with the Home or Overview button
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