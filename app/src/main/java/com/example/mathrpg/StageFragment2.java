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

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class StageFragment2 extends Fragment {

    private Button btnStage21,btnStage22,btnStage23;
    private AlertDialog.Builder storyAlertBuilder;
    private AlertDialog storyDialog;
    private SoundPool sp;
    private SharedPreferences prefs;

    public StageFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stage_fragment2, container, false);

        prefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);

        btnStage21 = (Button)view.findViewById(R.id.btn_stage2_1);
        btnStage22 = (Button)view.findViewById(R.id.btn_stage2_2);
        btnStage23 = (Button)view.findViewById(R.id.btn_stage2_3);
        sp = new SoundPool.Builder().build();

        final int selectSound = sp.load(view.getContext(), R.raw.stage_select,1);
        final int confirmSound = sp.load(view.getContext(), R.raw.stage_confirm,1);
        storyAlertBuilder = new AlertDialog.Builder(view.getContext(), R.style.StoryDialogTheme);

        //Track player progression, must finish stages in order, each NEW completed stage increments progress by 1
        //TODO: Multi-line comment on this section is to enable devs to test every stage, comment out when all stages and gameplay are complete
        /*
        if(prefs.contains("progress")){
            //Stage 2-1
            if(prefs.getInt("progress",0) >= 3){
                btnStage21.setEnabled(true);
                btnStage21.setAlpha(1.0f);
            }
            else{
                btnStage21.setEnabled(false);
                btnStage21.setAlpha(0.5f);
            }
            //Stage 2-2
            if(prefs.getInt("progress",0) >= 4){
                btnStage22.setEnabled(true);
                btnStage22.setAlpha(1.0f);
            }
            else{
                btnStage22.setEnabled(false);
                btnStage22.setAlpha(0.5f);
            }
            //Stage 2-3
            if(prefs.getInt("progress",0) >= 5){
                btnStage23.setEnabled(true);
                btnStage23.setAlpha(1.0f);
            }
            else{
                btnStage23.setEnabled(false);
                btnStage23.setAlpha(0.5f);
            }
        }
        */


        btnStage21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Stage 2-1");
                storyAlertBuilder.setMessage("\"I may be defeated, but humanity will soon end,\" the forest dragon growls as it spews mist throughout the battlefield.\n\nIn just mere moments, the dragon is nowhere to be seen. The forest quickly falls into an eerie silence.\n\n\"Dang, it got away!\" You curse as you scan your surroundings. \"No use complaining, let's keep pushing forward.\"\n\nYour party continues north and soon reaches the icy forest. However, it seems that the creatures there are not welcoming to travellers.\n\n\"Heads up everyone, beware of the ice monsters,\" Galter warns.");
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
        btnStage22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Stage 2-2");
                storyAlertBuilder.setMessage("As your party ventures further north through the icy forest, you eventually reach the frozen sea.\n\n\"Time to put this spell to good use!\" Maria says as she conjures up a boat that is able to glide through the ice.\n\n\"Nice, your spells can be really useful at times, Maria.\" Galter acknowledges. \"But it's too bad that journeys will never be smooth sailing these days.\"\n\nShark monsters suddenly burst through the ice around your party, brandishing weapons.");
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
        btnStage23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.play(selectSound,1,1,1,0,1.0f);
                storyAlertBuilder.setTitle("Stage 2-3");
                storyAlertBuilder.setMessage("Your party has beaten the shark monsters and resumes sailing further north throughout the night. The next day, your group reaches a small mysterious island with an eerie atmosphere. The three of you decide to split up and search your surroundings.\n\n\"Guys come here, I found something!\" Marie called out. \"This doesn't look good!\"\n\nYou and Galter hurry over towards her direction, she points at a tall black portal with ancient carvings.\n\n\"I assume this is what we have to investigate for the quest, it sure looks mysterious enough,\" Galter says.\n\nSuddenly, the ground trembles and the portal lights up, a red dragon with a threatening glare climbs out of the portal.\n\nYou alarm the others, \"Be cautious everyone, it looks dangerous!\"");
                storyAlertBuilder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sp.play(confirmSound,1,1,1,0,1.0f);
                        //Pass Stage 2-3 info to BattleActivity
                        Intent battleIntent = new Intent(getContext(), BattleActivity.class);
                        battleIntent.putExtra("enemy3_sprite",R.drawable.stage2_3_boss);
                        battleIntent.putExtra("enemy3_name","Lv.10 Dragon Warlord");
                        battleIntent.putExtra("battle_bg", R.drawable.stage2_battle_bg);
                        battleIntent.putExtra("battle_music", R.raw.bgm_stage2_finalboss);
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