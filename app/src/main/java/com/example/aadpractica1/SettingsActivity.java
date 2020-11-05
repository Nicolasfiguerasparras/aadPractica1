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
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup rgGroup;
    RadioButton rbExternal, rbInternal;
    Button btSavePreferences;
    TextView tvSettingsResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(rgGroup.getCheckedRadioButtonId() == R.id.rbExternal){
            tvSettingsResult.setText("Almacenamiento externo");
        }else if(rgGroup.getCheckedRadioButtonId() == R.id.rbInternal){
            tvSettingsResult.setText("Almacenamiento interno");
        }

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbExternal){
                    tvSettingsResult.setText("Almacenamiento externo");
                }else if(checkedId == R.id.rbInternal){
                    tvSettingsResult.setText("Almacenamiento interno");
                }
            }
        });

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
        tvSettingsResult = findViewById(R.id.tvSettingsResult);
        loadPreferences();
    }

    public void savePreferences(){
        if(tvSettingsResult.getText().toString().equals("Almacenamiento externo")){
            modifyPreference("External");
        }else{
            modifyPreference("Internal");
        }
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        Log.v("xyz", preferences.getString("storage", "abc"));
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void modifyPreference(String string){
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("storage", string);
        editor.apply();
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