package com.example.mathrpg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class DebugActivity extends AppCompatActivity {

    private Switch switchAllStages;
    private Button btnDebugApply;

    private boolean statusAllStages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        statusAllStages = DebugGame.isAllStages();

        switchAllStages = (Switch)findViewById(R.id.switch_allStages);
        btnDebugApply = (Button)findViewById(R.id.btn_debug_apply);

        switchAllStages.setChecked(statusAllStages);

        switchAllStages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    statusAllStages = true;
                else
                    statusAllStages = false;
            }
        });

        btnDebugApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugGame.setAllStages(statusAllStages);

                Toast.makeText(DebugActivity.this,"Debug settings applied",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}