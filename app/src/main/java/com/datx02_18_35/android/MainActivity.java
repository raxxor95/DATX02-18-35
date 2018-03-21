package com.datx02_18_35.android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Spinner;

import com.datx02_18_35.controller.Controller;
import com.datx02_18_35.controller.dispatch.actions.RequestStartNewSessionAction;
import com.datx02_18_35.model.expression.ExpressionFactory;
import com.datx02_18_35.model.game.Level;
import com.datx02_18_35.model.game.LevelParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import game.logic_game.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    List<String> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Add listener
        Button start_button = (Button) findViewById(R.id.start_button); //grab a view and convert it to a button class
        start_button.setOnClickListener(this); //this indicates that the onClick will be called
        Button quit_button = (Button) findViewById(R.id.quit_button);
        quit_button.setOnClickListener(this);

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("levels.txt"), "UTF-8"));
            String line;
            BufferedReader bufferLine = null;
            while ((line = bufferedReader.readLine()) != null) {
                bufferLine = new BufferedReader(new InputStreamReader(getAssets().open(line), "UTF-8"));
                String level = "", lineInside;
                while ((lineInside = bufferLine.readLine()) != null) {
                    level = level + lineInside + '\n';
                }
                list.add(level);

            }
            bufferLine.close();
            bufferedReader.close();
        } catch (Exception e) {
            Log.d(Tools.debug, "onCreate : MainActivity : read in levels failed: " + e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //TODO: Pass list of level files as Strings
            Controller.init(list, Tools.getUserData(getApplicationContext()));
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
