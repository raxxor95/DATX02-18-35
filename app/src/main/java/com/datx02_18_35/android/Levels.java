package com.datx02_18_35.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.datx02_18_35.controller.Controller;
import com.datx02_18_35.controller.dispatch.ActionConsumer;
import com.datx02_18_35.controller.dispatch.UnhandledActionException;
import com.datx02_18_35.controller.dispatch.actions.Action;
import com.datx02_18_35.controller.dispatch.actions.RefreshLevelsAction;
import com.datx02_18_35.controller.dispatch.actions.RequestLevelsAction;
import com.datx02_18_35.model.game.Level;

import java.util.ArrayList;

import game.logic_game.R;

public class Levels extends AppCompatActivity {
    //callback
    LevelsCallback callback;

    //views
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    LevelsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        recyclerView = (RecyclerView) findViewById(R.id.levels_recyclerview);


        //dynamic span count later
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new LevelsAdapter(null, this);
        recyclerView.setAdapter(adapter);

        callback = new LevelsCallback();
        callback.start();

        try {
            Controller.getSingleton().sendAction(new RequestLevelsAction(callback));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public class LevelsCallback extends ActionConsumer {
        @Override
        public void handleAction(Action action) throws UnhandledActionException, InterruptedException {
            if(action instanceof RefreshLevelsAction){
               adapter.updateLevels( ((RefreshLevelsAction) action).levelList);
            }
        }
    }
}