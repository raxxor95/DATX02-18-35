package com.datx02_18_35.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.datx02_18_35.controller.Controller;
import com.datx02_18_35.model.game.LevelParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.logic_game.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MODEL_CONFIG_PATH = "modelConfig";
    private Map<String, String> modelConfigFiles = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Add listener
        Button start_button = findViewById(R.id.start_button); //grab a view and convert it to a button class
        start_button.setOnClickListener(this); //this indicates that the onClick will be called
        Button quit_button = findViewById(R.id.quit_button);
        quit_button.setOnClickListener(this);


        // Read model config files
        try {
            AssetManager assets = getApplicationContext().getAssets();
            String[] modelConfigFilenames = assets.list(MODEL_CONFIG_PATH);

            for (String filename : modelConfigFilenames) {
                String filepath = MODEL_CONFIG_PATH + "/" + filename;
                InputStream inputStream = assets.open(filepath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while (null != (line = reader.readLine())) {
                    stringBuilder.append(line).append("\n");
                }
                modelConfigFiles.put(filename, stringBuilder.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //TODO: Pass list of level files as Strings
            Controller.init(modelConfigFiles, Tools.getUserData(getApplicationContext()));
            Controller.getSingleton().start();
        } catch (LevelParseException e) {
            //TODO: Handle this properly
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_button: {
                Intent intent = new Intent(this, Levels.class); //create intent
                startActivity(intent); //start intent
                break;
            }
            case R.id.quit_button: {
                finish();
                break;
            }
        }
    }
}
