package com.example.mathrpg;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 */
public class StageFragment1 extends Fragment {

    private Button btnBattleTest;

    public StageFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stage_fragment1, container, false);

        //TODO: Remove test battle button when done
        btnBattleTest = (Button)view.findViewById(R.id.btn_battletest);
        btnBattleTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent battleTestIntent = new Intent(view.getContext(),BattleActivity.class);
                startActivity(battleTestIntent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}