package com.example.mathrpg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class StageFragment3 extends Fragment {

    private Button btnStageFinal,btnStageSecret;
    private AlertDialog.Builder storyAlertBuilder;
    private AlertDialog storyDialog;
    private SoundPool sp;
    private SharedPreferences prefs;

    public StageFragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stage_fragment3, container, false);

        prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);

        btnStageFinal = (Button)view.findViewById(R.id.btn_stage_final);
        btnStageSecret = (Button)view.findViewById(R.id.btn_stage_secret);
        sp = new SoundPool.Builder().build();

        final int selectSound = sp.load(view.getContext(), R.raw.stage_select,1);
        final int confirmSound = sp.load(view.getContext(), R.raw.stage_confirm,1);
        storyAlertBuilder = new AlertDialog.Builder(view.getContext(), R.style.StoryDialogTheme);

        //Track player progression, must finish stages in order, each NEW completed stage increments progress by 1
        if(!(prefs.getString("name","404").contentEquals("admin") && DebugGame.isAllStages())){
            if(prefs.contains("progress")){
                //Final stage
                if(prefs.getInt("progress",0) >= 6){
                    btnStageFinal.setEnabled(true);
                    btnStageFinal.setAlpha(1.0f);
                }
                else{
                    btnStageFinal.setEnabled(false);
                    btnStageFinal.setAlpha(0.5f);
                }
                //Secret stage
                if(prefs.getInt("progress",0) >= 7){
                    btnStageSecret.setEnabled(true);
                    btnStageSecret.setVisibility(View.VISIBLE);
                }
                else{
                    btnStageSecret.setEnabled(false);
                    btnStageSecret.setVisibility(View.INVISIBLE);
                }
            }
        }

        btnStageFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Final Battle");
                storyAlertBuilder.setMessage("\"My lord, I have failed you...\" the red dragon mutters as it breathes its last.\n\nYour party jumps through the portal and is taken to an unfamiliar location. An enormous castle looms over your party.\n\n\"Oh my god, this looks huge!\" Marie exclaims. \"I can feel tremendous power residing within the castle walls,\" she adds as she backs away cautiously.\n\nThe giant doors of the castle slowly open as if inviting your party into the building. Your group vigilantly proceeds into the castle, expecting danger from the shadows. A menacing blue dragon greets them in the main hall.\n\n\"You have done well to make it this far, but your adventure ends here!\" The dragon roars.\n\n\"This is the strongest beast we've faced thus far, stay sharp!\" You caution the others as you tighten your grip on your staff.\n\nIt dashes towards your party, \"I, THE DARK DRAGON LORD, WILL DESTROY ALL HUMANS AND THE WORLD!\"");
                storyAlertBuilder.setPositiveButton("To Battle!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(confirmSound,1,1,1,0,1.0f);
                        //Pass Final Stage info to BattleActivity, including all 3 forms of final boss
                        Intent battleIntent = new Intent(getContext(), BattleActivity.class);
                        battleIntent.putExtra("enemy3_sprite",R.drawable.stagefinal_boss3);
                        battleIntent.putExtra("enemy3_name","Lv.15 Dark Dragon Lord");
                        battleIntent.putExtra("battle_bg", R.drawable.stagefinal_battle_bg);
                        battleIntent.putExtra("battle_music", R.raw.bgm_stagefinal_finalboss);
                        startActivity(battleIntent);
                    }
                });
                storyAlertBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                storyDialog = storyAlertBuilder.create();
                Objects.requireNonNull(storyDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6E2C00")));
                storyDialog.show();
            }
        });
        btnStageSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                if(prefs.getInt("level",1) >= 20 || DebugGame.isSecretUnlocked()){
                    storyAlertBuilder.setTitle("?????");
                    storyAlertBuilder.setMessage("Your party gets sucked into a strange portal that appears out of nowhere. A menacing man appears out of thin air and slowly approaches you.\n\n\"HUH? WHAT JUST HAPPENED?? WHERE ARE WE???\"\n\n\"Oh no, is that...? OH GOD OH NO OH SH-\"");
                    storyAlertBuilder.setPositiveButton("Explore?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AlertDialog.Builder secretAlertBuilder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.StoryDialogTheme);
                            secretAlertBuilder.setTitle("E̸̳̣͙͆͑̎̀͘R̶͍̹͌̿̈́̿͐̈R̸̢̲̬̹̞̹̼̃̔͗͋O̸̪͈͍͇̫̖͎̠̐R̶̨̪̰̰̪̘̾̓_̸̱̙͉̈́̔͜Ç̶̢͉̠̱̘̅̕ͅO̶̧̲̩͍͔̙̖̍͌̽̐̔̄̎ͅD̷̢̮̤̘̖̖̫̃̋͗E̸̠̱̐͒͂̚͘");
                            secretAlertBuilder.setMessage("[i̸-̴i̷-̷i̵N̸c̶o̷M̶i̷N̷G̸]> V̶̤̣͈̠͒̂̒͝ḯ̴̧̨͓̈́̓̔͜ͅ ̴̭͍̯̋̇̔̆̚ş̸͙̣̣̉̀ì̶̬̲͇̩͛͆ţ̸̙̪͓̺͋͛͘t̶̢̝̺͙̿̂͛͗e̴̫̹̻̝̋̋̓̇̈́͜r̸̩̾̀̑͠ ̵͙̈́̈́̈h̵̲̙̄͘ä̶͔̗̹̙̈́̏̈́̄r̷͕̺̭̈́́͝͠ ̷̛͔͌̀̄̊i̶̖̗͈͙͂̔̿ ̷̨̮͕̮́̐̄͒V̴̲̥͈͍̌̍̚͝é̸̖̙n̶̨̼̖̞̐͋̄̿̾t̶̨̊̌͑̇̈͜e̸̗͓̭͂̕͜n̶̠͉̫̜̻̄̃̏̈");
                            secretAlertBuilder.setCancelable(false);
                            secretAlertBuilder.setPositiveButton("OH NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sp.play(confirmSound,1,1,1,0,1.0f);
                                    //Pass Secret Stage info to BattleActivity
                                    Intent battleIntent = new Intent(getContext(), BattleActivity.class);
                                    battleIntent.putExtra("enemy3_sprite",R.drawable.stagesecret_boss);
                                    battleIntent.putExtra("enemy3_name","Lv.20 Milos the Midnight Dancer");
                                    battleIntent.putExtra("battle_bg", R.drawable.stagesecret_battle_bg);
                                    battleIntent.putExtra("battle_music", R.raw.bgm_stagesecret_finalboss);
                                    Toast.makeText(getContext(),"[WARNING] MAXIMUM LEVEL D-D-DANGER THERE IS N̴͖̺͈̑́͗̒͠-̷̡̘͕̻̓̇̄N̸̲͑̋̂̅Ȍ̷͇͕̬̀̂͒ ̵̦͊̆͗Ë̷̢̛̪̹͕́̄͘S̶͙͖͓͍̓̍͘͘C̴̘̅̊A̶̪͊P̸̯̝̖̀̓E̷͚͓̭̺̹̒̄̑", Toast.LENGTH_LONG).show();
                                    startActivity(battleIntent);
                                }
                            });
                            storyDialog = secretAlertBuilder.create();
                            Objects.requireNonNull(storyDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                            storyDialog.show();
                        }
                    });
                    storyAlertBuilder.setNegativeButton("Turn Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    storyDialog = storyAlertBuilder.create();
                    Objects.requireNonNull(storyDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6E2C00")));
                    storyDialog.show();
                }
                else{
                    storyAlertBuilder.setTitle("?????");
                    storyAlertBuilder.setMessage("Reach Level 20");
                    storyDialog = storyAlertBuilder.create();
                    Objects.requireNonNull(storyDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6E2C00")));
                    storyDialog.show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}