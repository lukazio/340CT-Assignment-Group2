package com.example.mathrpg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class StageFragment1 extends Fragment {

    private Button btnStage11,btnStage12,btnStage13;
    private AlertDialog.Builder storyAlertBuilder;
    private AlertDialog storyDialog;
    private SoundPool sp;
    private SharedPreferences prefs;

    public StageFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stage_fragment1, container, false);

        prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);

        btnStage11 = (Button)view.findViewById(R.id.btn_stage1_1);
        btnStage12 = (Button)view.findViewById(R.id.btn_stage1_2);
        btnStage13 = (Button)view.findViewById(R.id.btn_stage1_3);
        sp = new SoundPool.Builder().build();

        final int selectSound = sp.load(view.getContext(), R.raw.stage_select,1);
        final int confirmSound = sp.load(view.getContext(), R.raw.stage_confirm,1);
        storyAlertBuilder = new AlertDialog.Builder(view.getContext(), R.style.StoryDialogTheme);

        //Track player progression, must finish stages in order, each NEW completed stage increments progress by 1
        //TODO: Multi-line comment on this section is to enable devs to test every stage, comment out when all stages and gameplay are complete
        /*
        if(prefs.contains("progress")){
            //Stage 1-2
            if(prefs.getInt("progress",0) >= 1){
                btnStage12.setEnabled(true);
                btnStage12.setAlpha(1.0f);
            }
            else{
                btnStage12.setEnabled(false);
                btnStage12.setAlpha(0.5f);
            }
            //Stage 1-3
            if(prefs.getInt("progress",0) >= 2){
                btnStage13.setEnabled(true);
                btnStage13.setAlpha(1.0f);
            }
            else{
                btnStage13.setEnabled(false);
                btnStage13.setAlpha(0.5f);
            }
        }
        */

        btnStage11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Stage 1-1");
                storyAlertBuilder.setMessage("You are a leader of a small adventuring group which consists of yourself, a magician, and your two close friends, Galter the knight and Maria the cleric. Your group accepts an unnamed quest at the guild, which is to investigate a mystery in the neighbouring land.\n\n\"Let's go, friends! This way to the gate of Graycott Town and into the forest we go!\" You say as you merrily march towards the exit.\n\nA journey awaits you with unexpected events in the vast open world.");
                storyAlertBuilder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(confirmSound,1,1,1,0,1.0f);
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
        btnStage12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Stage 1-2");
                storyAlertBuilder.setMessage("After dispatching a group of frog monsters, your party sets up a quick camp nearby to take a break. You begin to wonder what is the real goal of this quest.\n\n\"Gosh, the quest only tells us to 'investigate a mystery to the north' and nothing else,\" Maria sighs.\n\n\"Well, guess the only way to find out is to continue travelling,\" Galter shrugs. \"Hope the reward will be worth our troubles.\"\n\nSuddenly, you hear screeching noises off in the distance. It appears to be growing closer to your party.");
                storyAlertBuilder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(confirmSound,1,1,1,0,1.0f);
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
        btnStage13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Stage 1-3");
                storyAlertBuilder.setMessage("You let out a sigh of relief as the last of the plant monsters fall. However, this victory did not last long, a threatening roar echoes the forest shortly after the battle.\n\n\"That does not sound good at all...\" Galter says, with an increasingly worried look.\n\nMaria looks into the sky. \"Oh no, that's a dragon, it's heading this way!\" She exclaims.\n\nYou ready your magical powers and shout, \"Be careful everyone, it looks strong!\"");
                storyAlertBuilder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(confirmSound,1,1,1,0,1.0f);
                        //Pass Stage 1-3 info to BattleActivity
                        Intent battleIntent = new Intent(getContext(), BattleActivity.class);
                        battleIntent.putExtra("enemy3_sprite",R.drawable.stage1_3_boss);
                        battleIntent.putExtra("enemy3_name","Lv.5 Forest Dragon");
                        battleIntent.putExtra("battle_bg", R.drawable.stage1_battle_bg);
                        battleIntent.putExtra("battle_music", R.raw.bgm_stage1_finalboss);
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

        // Inflate the layout for this fragment
        return view;
    }
}