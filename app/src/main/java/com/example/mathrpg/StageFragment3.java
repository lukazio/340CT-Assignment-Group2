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

    private Button btnStage3,btnStageFinal,btnStageSecret;
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

        btnStage3 = (Button)view.findViewById(R.id.btn_stage3);
        btnStageFinal = (Button)view.findViewById(R.id.btn_stage_final);
        btnStageSecret = (Button)view.findViewById(R.id.btn_stage_secret);
        sp = new SoundPool.Builder().build();

        final int selectSound = sp.load(view.getContext(), R.raw.stage_select,1);
        final int confirmSound = sp.load(view.getContext(), R.raw.stage_confirm,1);
        storyAlertBuilder = new AlertDialog.Builder(view.getContext(), R.style.StoryDialogTheme);

        //Track player progression, must finish stages in order, each NEW completed stage increments progress by 1
        if(!(prefs.getString("name","404").contentEquals("admin") && DebugGame.isAllStages())){
            if(prefs.contains("progress")){
                //Stage 3
                if(prefs.getInt("progress",0) >= 6){
                    btnStage3.setEnabled(true);
                    btnStage3.setAlpha(1.0f);
                }
                else{
                    btnStage3.setEnabled(false);
                    btnStage3.setAlpha(0.5f);
                }
                //Final stage
                if(prefs.getInt("progress",0) >= 7){
                    btnStageFinal.setEnabled(true);
                    btnStageFinal.setAlpha(1.0f);
                }
                else{
                    btnStageFinal.setEnabled(false);
                    btnStageFinal.setAlpha(0.5f);
                }
                //Secret stage
                if(prefs.getInt("progress",0) >= 8){
                    btnStageSecret.setEnabled(true);
                    btnStageSecret.setVisibility(View.VISIBLE);
                }
                else{
                    btnStageSecret.setEnabled(false);
                    btnStageSecret.setVisibility(View.INVISIBLE);
                }
            }
        }

        btnStage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Stage 3");
                storyAlertBuilder.setMessage("SUGGESTED LEVEL: 12\n\nAfter a fierce fight with the dragon warlord, your party jumps through the portal and is taken to an unfamiliar location. An enormous castle looms over your party.\n\n\"Oh my god, this looks huge!\" Maria exclaims. \"I can feel tremendous power residing within the castle walls,\" she adds as she backs away cautiously.\n\nSuddenly, an earsplitting roar blasts your party from the portal behind, the red dragon climbs through the portal painfully. It looks at you with a deathly grin.\n\n\"Looks like this is its full power, be careful everyone!\" You warn the others.");
                storyAlertBuilder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(confirmSound,1,1,1,0,1.0f);
                        //Pass Stage 2-3 info to BattleActivity
                        Intent battleIntent = new Intent(getContext(), BattleActivity.class);
                        battleIntent.putExtra("enemy3_sprite",R.drawable.stage3_1_boss);
                        battleIntent.putExtra("enemy3_name","Lv.12 Angry Dragon Warlord");
                        battleIntent.putExtra("enemy3_hp",400);
                        battleIntent.putExtra("enemy3_attack",22);
                        battleIntent.putExtra("battle_bg", R.drawable.stage3_battle_bg);
                        battleIntent.putExtra("battle_music", R.raw.bgm_stagefinal_battle);
                        battleIntent.putExtra("exp",100);
                        battleIntent.putExtra("progress",7);
                        startActivity(battleIntent);
                        getActivity().finish();
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
        btnStageFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Final Battle");
                storyAlertBuilder.setMessage("SUGGESTED LEVEL: 15\n\n\"My lord, I have failed you...\" the red dragon mutters as it breathes its last.\n\nThe giant doors of the castle slowly open as if inviting your party into the building. Your group vigilantly proceeds into the castle, expecting danger from the shadows. A menacing blue dragon greets them in the main hall.\n\n\"You have done well to make it this far, but your adventure ends here!\" The dragon roars.\n\n\"This is the strongest beast we've faced thus far, stay sharp!\" You caution the others as you tighten your grip on your staff.\n\nIt dashes towards your party, \"I, THE DARK DRAGON LORD, WILL DESTROY ALL HUMANS AND THE WORLD!\"");
                storyAlertBuilder.setPositiveButton("To Battle!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(confirmSound,1,1,1,0,1.0f);
                        //Pass Final Stage info to BattleActivity, including all 3 forms of final boss
                        Intent battleIntent = new Intent(getContext(), BattleActivity.class);
                        battleIntent.putExtra("enemy1_sprite",R.drawable.stagefinal_boss1);
                        battleIntent.putExtra("enemy2_sprite",R.drawable.stagefinal_boss2);
                        battleIntent.putExtra("enemy3_sprite",R.drawable.stagefinal_boss3);
                        battleIntent.putExtra("enemy1_name","Lv.15 Dark Dragon Lord α");
                        battleIntent.putExtra("enemy2_name","Lv.15 Dark Dragon Lord β");
                        battleIntent.putExtra("enemy3_name","Lv.15 Supreme Lord of Darkness");
                        battleIntent.putExtra("enemy1_hp",250);
                        battleIntent.putExtra("enemy2_hp",200);
                        battleIntent.putExtra("enemy3_hp",444);
                        battleIntent.putExtra("enemy1_attack",19);
                        battleIntent.putExtra("enemy2_attack",22);
                        battleIntent.putExtra("enemy3_attack",24);
                        battleIntent.putExtra("battle_bg", R.drawable.stagefinal_battle_bg);
                        battleIntent.putExtra("battle_music", R.raw.bgm_stagefinal_finalboss);
                        battleIntent.putExtra("exp",150);
                        battleIntent.putExtra("progress",8);
                        startActivity(battleIntent);
                        getActivity().finish();
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
                if(prefs.getInt("level",1) >= 20 || (DebugGame.isSecretUnlocked() && prefs.getString("name","404").contentEquals("admin"))){
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
                                    battleIntent.putExtra("enemy3_hp",1666);
                                    battleIntent.putExtra("enemy3_attack",33);
                                    battleIntent.putExtra("battle_bg", R.drawable.stagesecret_battle_bg);
                                    battleIntent.putExtra("battle_music", R.raw.bgm_stagesecret_finalboss);
                                    battleIntent.putExtra("exp",169);
                                    Toast.makeText(getContext(),"[WARNING] MAXIMUM LEVEL D-D-DANGER THERE IS N̴͖̺͈̑́͗̒͠-̷̡̘͕̻̓̇̄N̸̲͑̋̂̅Ȍ̷͇͕̬̀̂͒ ̵̦͊̆͗Ë̷̢̛̪̹͕́̄͘S̶͙͖͓͍̓̍͘͘C̴̘̅̊A̶̪͊P̸̯̝̖̀̓E̷͚͓̭̺̹̒̄̑", Toast.LENGTH_LONG).show();
                                    startActivity(battleIntent);
                                    getActivity().finish();
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
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(view.getContext(), R.style.StoryDialogTheme);
                    alertBuilder.setTitle("?????");
                    alertBuilder.setMessage("Reach Level 20");
                    AlertDialog alertDialog = alertBuilder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6E2C00")));
                    alertDialog.show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}