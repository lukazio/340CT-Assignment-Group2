package com.example.mathrpg;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
public class StageFragment3 extends Fragment {

    private Button btnStageFinal,btnStageSecret;
    private AlertDialog.Builder storyAlertBuilder;
    private AlertDialog storyDialog;

    public StageFragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stage_fragment3, container, false);

        btnStageFinal = (Button)view.findViewById(R.id.btn_stage_final);
        btnStageSecret = (Button)view.findViewById(R.id.btn_stage_secret);

        storyAlertBuilder = new AlertDialog.Builder(view.getContext(), R.style.StoryDialogTheme);

        btnStageFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storyAlertBuilder.setTitle("Final Battle");
                storyAlertBuilder.setMessage("Display final stage story");
                storyAlertBuilder.setPositiveButton("Showtime!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
                storyAlertBuilder.setTitle("?????");
                storyAlertBuilder.setMessage("Display secret stage story");
                storyAlertBuilder.setPositiveButton("Explore?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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

        //TODO: Hide secret stage button, display button if player account has completed all previous stages including final stage

        // Inflate the layout for this fragment
        return view;
    }
}