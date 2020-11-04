package com.example.aadpractica1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup rgGroup;
    RadioButton rbExternal, rbInternal;
    Button btSavePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        init();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btSavePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });
    }


    public void init(){
        rgGroup = findViewById(R.id.rgGroup);
        rbExternal = findViewById(R.id.rbExternal);
        rbInternal = findViewById(R.id.rbInternal);
        btSavePreferences = findViewById(R.id.btSavePreferences);
        loadPreferences();
    }

    public void savePreferences(){
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("storage", "abc");
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbExternal){
                    editor.putString("storage", "External");
                    Log.v("xyzyx", "ha entrado aqui");
                }else if(checkedId == R.id.rbInternal){
                    editor.putString("storage", "Internal");
                }
            }
        });
        editor.apply();
        Log.v("xyzyx", preferences.getString("storage", "abc"));
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void loadPreferences(){
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);

        switch (preferences.getString("storage", "abc")){
            case "External":
                rbExternal.setChecked(true);
                break;
            case "Internal":
                rbInternal.setChecked(true);
                break;
        }
    }
}