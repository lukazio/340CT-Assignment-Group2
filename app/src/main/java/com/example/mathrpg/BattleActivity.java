package com.example.mathrpg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class BattleActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 15000; //for countdown timer
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private ConstraintLayout clBgStage;
    private TextView tvEnemyName,tvQuestion,tvTimer,tvBarHpValue,hpBar,tvCombo,tvTurn,tvBarEneHpValue,eneHpBar;
    private ImageView ivEnemy;
    private SharedPreferences prefs;
    private MediaPlayer mp;
    private SoundPool sp;
    private Button btnPause,btnAns1,btnAns2,btnAns3, btnBeginTurn,btnreset;
    private AlertDialog.Builder pauseAlertBuilder;
    private AlertDialog pauseDialog;
    private Guideline hpGuideline,eneHpGuideline;

    //Battle stage variables (player's current HP, monster stats, stage EXP, etc.)
    private int currentHp,maxHp,stageExp,playerAttack,currentEneHp;
    private double totalDmg, dmgTaken;
    private Enemy enemy1,enemy2,enemy3;

    //Battle gameplay variables (correct answer button, turn, etc.)
    private int answerButton,combo,round=1;
    private boolean playerTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        prefs = getSharedPreferences("User", Context.MODE_PRIVATE);

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
        ivEnemy = (ImageView)findViewById(R.id.iv_enemy);
        btnPause = (Button)findViewById(R.id.btn_pause);
        btnAns1 = (Button)findViewById(R.id.btn_ans1);
        btnAns2 = (Button)findViewById(R.id.btn_ans2);
        btnAns3 = (Button)findViewById(R.id.btn_ans3);
        hpGuideline = (Guideline)findViewById(R.id.guideline_hp);
        eneHpGuideline = (Guideline)findViewById(R.id.guideline_ene_hp);
        btnBeginTurn=(Button)findViewById(R.id.btn_begin) ;
        //btnreset=(Button)findViewById(R.id.btn_reset);

        tvQuestion.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);
        tvCombo.setVisibility(View.GONE);
        tvTurn.setVisibility(View.GONE);

        //set up countdown timer
        btnBeginTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBeginTurn.setVisibility(View.GONE);  //Hide button
                startTimer();   //Start timer countdown
                updateCountDownText();
                playerTurn = true;  //Start player turn
                updateTurnLabel();
                generateQuestion(); //Start generating question
                btnAns1.setVisibility(View.VISIBLE);
                btnAns2.setVisibility(View.VISIBLE);
                btnAns3.setVisibility(View.VISIBLE);
                tvQuestion.setVisibility(View.VISIBLE);
                tvTimer.setVisibility(View.VISIBLE);
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


        //Set battle variables (player's current HP, monster stats, stage EXP etc.)
        currentHp = maxHp = prefs.getInt("hp",1);
        playerAttack = prefs.getInt("attack", 0);
        totalDmg = 0;
        updatePlayerHealthBar();
        enemy1 = new Enemy();
        enemy2 = new Enemy();
        enemy3 = new Enemy();
        stageExp = getIntent().getIntExtra("exp",0);


        sp = new SoundPool.Builder().setMaxStreams(5).build();
        final int selectSound = sp.load(this, R.raw.stage_select,1);

        pauseAlertBuilder = new AlertDialog.Builder(this, R.style.StoryDialogTheme);

        //TODO: Test button for various functions to test, remove when gameplay completed
        //Now testing: question and answer generation
        Button btnTest = (Button)findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQuestion();
            }
        });
        //Now testing: switching rounds to display enemies of each round
        Button btnTest2 = (Button)findViewById(R.id.btn_test2);
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                round++;
                if(round > 3){round = 1;}
                updateEnemyDisplay();
            }
        });

        //TODO: Test answering questions, move to code proper location when implementing turn-based battle mechanic
        btnAns1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerButton == 1) {
                    onCorrect();
                }
                else {
                    onWrong();
                }
            }
        });
        btnAns2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerButton == 2) {
                    onCorrect();
                }
                else {
                    onWrong();
                }
            }
        });
        btnAns3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerButton == 3) {
                    onCorrect();
                }
                else {
                    onWrong();
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
        else if(getIntent().hasExtra("enemy3_hp"))
            enemy3.setStats(getIntent().getIntExtra("enemy3_hp",1), getIntent().getIntExtra("enemy3_attack",0));
        //Get stage background
        if(getIntent().hasExtra("battle_bg"))
            clBgStage.setBackground(getDrawable(getIntent().getIntExtra("battle_bg",0)));
        //Get stage battle music
        if(getIntent().hasExtra("battle_music")) {
            mp = MediaPlayer.create(this, getIntent().getIntExtra("battle_music", 0));
            mp.setLooping(true);
            mp.start();
        }

        //Show first enemy onstartup
        currentEneHp = enemy1.getHp();
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
                    pauseTimer();
            }
        });
    } //END OF onCreate

    //On answering correctly
    private void onCorrect(){
        //When it's player's turn
        if(playerTurn) {
           // Toast.makeText(BattleActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
            combo += 1;
            if(combo==1){
                tvCombo.setVisibility(View.VISIBLE);
            }
            tvCombo.setText("COMBO: "+combo);
            //TODO: Round off value(if needed)
            totalDmg = (1.0 + 0.2*(combo-1))*playerAttack;
            generateQuestion();
        }

        //When it's enemy's turn
        else{
            Toast.makeText(BattleActivity.this, "Correct! Reducing damage...", Toast.LENGTH_SHORT).show();

            //Calculated reduced dmg taken here
            switch (round){
                case 1: dmgTaken = enemy1.getAttack()/2;
                break;
                case 2: dmgTaken = enemy2.getAttack()/2;
                break;
                case 3: dmgTaken = enemy3.getAttack()/2;
                break;
                default:
            }

            showEndTurnDialog(0,(int)dmgTaken);
            playerTurn = true;
            updateTurnLabel();
        }

    }

    //On answering wrongly
    private void onWrong(){
        //When it's player's turn
        if(playerTurn) {
         //   Toast.makeText(BattleActivity.this, "Wrong...It's enemy's turn now", Toast.LENGTH_SHORT).show();
            showEndTurnDialog((int)totalDmg,0);
            playerTurn = false;
            updateTurnLabel();
            combo = 0;
            tvCombo.setVisibility(View.GONE);
        }

        //When it's enemy's turn
        else{
            Toast.makeText(BattleActivity.this, "Taking full damage!", Toast.LENGTH_SHORT).show();

            //Inserted full damage taken value here
            switch (round){
                case 1: dmgTaken = enemy1.getAttack();
                    break;
                case 2: dmgTaken = enemy2.getAttack();
                    break;
                case 3: dmgTaken = enemy3.getAttack();
                    break;
                default:
            }

            showEndTurnDialog(0,(int)dmgTaken);
            playerTurn = true;
            updateTurnLabel();
        }

    }

    //END TURN dialog
    private void showEndTurnDialog(int dmgMade, int dmgReceived){

        stopTimer();
        resetTimer();

        AlertDialog.Builder dialog = new AlertDialog.Builder(BattleActivity.this);
        dialog.setCancelable(false);

        if(playerTurn){
            dialog.setTitle("End of your Turn");
            dialog.setMessage("\nTotal damage dealt: "+dmgMade+"\nDefend yourself now!");
            //Reduced monster hp here
            currentEneHp = currentEneHp - dmgMade;
            tvCombo.setVisibility(View.GONE);
            updateEnemyHealthBar();
        }
        else{
            dialog.setTitle("End of enemy's Turn");
            dialog.setMessage("You've received "+dmgReceived+" damage from the enemy's attack!\nIt's your turn now!");
            currentHp = currentHp-dmgReceived;
            updatePlayerHealthBar();
        }

        dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startNew();
            }
        });
        dialog.show();

    }

    //Start new turn
    private void startNew(){
        generateQuestion();
        startTimer();
        totalDmg = 0;
        dmgTaken = 0;
        combo = 0;
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
    private void startTimer() {
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
                    Toast.makeText(BattleActivity.this, "It's enemy turn now!", Toast.LENGTH_SHORT).show();
                    showEndTurnDialog((int)totalDmg,0);
                    playerTurn = false;
                    updateTurnLabel();
                }

                //When it's enemy's turn
                else{
                    Toast.makeText(BattleActivity.this, "Taking full damage!", Toast.LENGTH_SHORT).show();
                    //Inserted full damage taken value
                    switch (round){
                        case 1: dmgTaken = enemy1.getAttack();
                            break;
                        case 2: dmgTaken = enemy2.getAttack();
                            break;
                        case 3: dmgTaken = enemy3.getAttack();
                            break;
                        default:
                    }
                    showEndTurnDialog(0,5);
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
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    //Shows the pause menu with 2 buttons Resume and Quit
    public void showPauseMenu() {
        pauseAlertBuilder.setTitle("Pause");
        pauseAlertBuilder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startTimer();
            }
        });
        pauseAlertBuilder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BattleActivity.super.onBackPressed();
            }
        });
        pauseDialog = pauseAlertBuilder.create();
        Objects.requireNonNull(pauseDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6E2C00")));
        pauseDialog.show();
    }

    //Update the HP bar's length and colour according to player's health level
    public void updatePlayerHealthBar(){
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
    }

    //TODO:Update the HP bar's length and colour according to monster's health level
    public void updateEnemyHealthBar(){

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

        //If enemy died
        if(currentEneHp <= 0) {
            currentEneHp = 0;
            changeRound();
        }

        if((float)currentEneHp/currentEnemy.getHp() <= 0.25)
            eneHpBar.setBackgroundColor(Color.parseColor("#E53935"));
        else if((float)currentEneHp/currentEnemy.getHp() > 0.25 && (float)currentEneHp/currentEnemy.getHp() <= 0.5)
            eneHpBar.setBackgroundColor(Color.parseColor("#FBC02D"));
        else
            eneHpBar.setBackgroundColor(Color.parseColor("#43A047"));

        float enmHP=currentEnemy.getHp();
        eneHpGuideline.setGuidelinePercent((float)currentEneHp/enmHP);
        tvBarEneHpValue.setText(currentEneHp + " / " + currentEnemy.getHp());

    }

    //Change Round FUnction
    private void changeRound(){
        Enemy newEnemy = new Enemy();
        Toast.makeText(this, "Next Stage...", Toast.LENGTH_SHORT).show();
        round++;
        if(round>3){
            startActivity(new Intent(BattleActivity.this,StageActivity.class));
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
            pauseTimer();
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