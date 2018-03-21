package com.datx02_18_35.android;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.datx02_18_35.controller.Controller;
import com.datx02_18_35.controller.dispatch.ActionConsumer;
import com.datx02_18_35.controller.dispatch.UnhandledActionException;
import com.datx02_18_35.controller.dispatch.actions.Action;
import com.datx02_18_35.controller.dispatch.actions.OpenSandboxAction;
import com.datx02_18_35.controller.dispatch.actions.RefreshGameboardAction;
import com.datx02_18_35.controller.dispatch.actions.RefreshInventoryAction;
import com.datx02_18_35.controller.dispatch.actions.RefreshRulesAction;
import com.datx02_18_35.controller.dispatch.actions.RequestAbortSessionAction;
import com.datx02_18_35.controller.dispatch.actions.RequestApplyRuleAction;
import com.datx02_18_35.controller.dispatch.actions.RequestAssumptionAction;
import com.datx02_18_35.controller.dispatch.actions.RequestGameboardAction;
import com.datx02_18_35.controller.dispatch.actions.RequestRulesAction;
import com.datx02_18_35.controller.dispatch.actions.RequestStartNewSessionAction;
import com.datx02_18_35.controller.dispatch.actions.SaveUserDataAction;
import com.datx02_18_35.controller.dispatch.actions.VictoryConditionMetAction;
import com.datx02_18_35.model.expression.Expression;
import com.datx02_18_35.model.expression.Rule;
import com.datx02_18_35.model.game.Level;
import com.datx02_18_35.model.game.LevelParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import game.logic_game.R;

import static com.datx02_18_35.controller.dispatch.actions.OpenSandboxAction.Reason.ASSUMPTION;

public class GameBoard extends AppCompatActivity  {

    Toolbar toolbar;
    FrameLayout layout;
    public static BoardCallback boardCallback;
    public static OpenSandboxAction sandboxAction=null;
    public final Semaphore gameChange = new Semaphore(1);
    public static boolean victory=false;
    public static Iterable<Iterable<Expression>> inventories;
    public static Iterable<Expression> assumptions;


    //recyclerviews
    public RecyclerView recyclerViewLeft,recyclerViewRight;
    public GridLayoutManager gridLayoutManagerLeft, gridLayoutManagerRight;
    public GameRuleAdapter adapterRight;
    public GameCardAdapter adapterLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        boardCallback = new BoardCallback();
        boardCallback.start();
        Intent myIntent= getIntent();
        try {
            gameChange.acquire();
            int levelInt=myIntent.getIntExtra("levelInt",1);
            Controller.getSingleton().sendAction(new RequestStartNewSessionAction(boardCallback,Controller.getSingleton().getLevels().get(levelInt)));
        } catch (Exception e) {
            e.printStackTrace();
        }


        initLeftSide();
        initRightSide();
        initInventory();
        try {
            Controller.getSingleton().sendAction(new RequestGameboardAction(boardCallback));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
                
        //Set toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        gameChange.release();
    }

    private void initRightSide() {

        recyclerViewRight = (RecyclerView) findViewById(R.id.game_right_side);
        // use a grid layout manager
        gridLayoutManagerRight = new GridLayoutManager(getApplication(), 1);
        recyclerViewRight.setLayoutManager(gridLayoutManagerRight);

        ArrayList<Rule> list = new ArrayList<>();
        list.add(null);
        //attach list to adapter
        adapterRight = new GameRuleAdapter(list,this);

        //attach adapter
        recyclerViewRight.setAdapter(adapterRight);
    }

    private void initLeftSide() {
        //"screen" re-size
        int spanCount;
        int widthDP=Math.round(Tools.getWidthDp(getApplication().getApplicationContext())) - 130*2;
        for (spanCount=0; 130*spanCount < widthDP ;spanCount++);

        recyclerViewLeft = (RecyclerView) findViewById(R.id.game_left_side);
        // use a grid layout manager
        gridLayoutManagerLeft = new GridLayoutManager(getApplication(), spanCount);
        recyclerViewLeft.setLayoutManager(gridLayoutManagerLeft);


        adapterLeft = new GameCardAdapter(new ArrayList<Expression>(),this);

        recyclerViewLeft.setAdapter(adapterLeft);

    }
    private void initInventory() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft =fm.beginTransaction();
        View gameView = this.findViewById(android.R.id.content);


        layout = (FrameLayout)findViewById(R.id.inventory_container);
        ft.replace(R.id.inventory_container, new FragmentInventory()).commit();
        layout.setVisibility(View.GONE);

        gameView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                showInventory();
            }
            public void onSwipeLeft() {
                closeInventory();
            }

        });
    }

    public void closeInventory(){
        if (layout.isShown()) {
            Tools.slide_left(this, layout);
            layout.setVisibility(View.GONE);

        }
    }
    public void showInventory(){
        if (!layout.isShown()){
            Tools.slide_right(this, layout);
            layout.setVisibility(View.VISIBLE);

        }
    }

    public synchronized void newSelection(Object object, View v) {
        try {
            gameChange.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (object instanceof Expression){
            Expression expression = (Expression) object;
            //already selected
            if (adapterLeft.selected.contains((int)v.getTag())){
                adapterLeft.resetSelection(expression, (CardView) v);
            }
            //not selected
            else if(!adapterLeft.selected.contains((int)v.getTag())){
                adapterLeft.setSelection(expression, (CardView) v);
            }
            //update rightside
            try {
                ArrayList<Expression> sendList = new ArrayList<>();
                for (Integer i : adapterLeft.selected){
                    sendList.add(adapterLeft.dataSet.get(i));
                }
                Controller.getSingleton().sendAction(new RequestRulesAction(boardCallback, sendList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(object instanceof Rule){
            try {
                Controller.getSingleton().sendAction(new RequestApplyRuleAction(boardCallback,(Rule)object));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        gameChange.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //shutdown session
        try {
            if(!victory){
                Controller.getSingleton().sendAction(new RequestAbortSessionAction());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar, menu); //add more items under dir menu -> toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        Intent i = null;
        switch(menu.getItemId()){
            case R.id.item_assumption:
                try {
                    Controller.getSingleton().sendAction((new RequestAssumptionAction(boardCallback)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }


    public class BoardCallback extends ActionConsumer {
        @Override
        public void handleAction(Action action) throws UnhandledActionException, InterruptedException {
            gameChange.acquire();
            if (action instanceof RefreshGameboardAction){
                Iterable<Expression> data =  ((RefreshGameboardAction) action).boardExpressions;
                adapterLeft.updateBoard(data);

            }
            else if (action instanceof SaveUserDataAction){
                Tools.writeUserData( ((SaveUserDataAction) action).userData, getApplicationContext());
            }
            else if (action instanceof RefreshRulesAction){
                Collection<Rule> data = ((RefreshRulesAction) action).rules;
                adapterRight.updateBoard(data);
            }
            else if (action instanceof OpenSandboxAction){
                String reason="";
                sandboxAction =(OpenSandboxAction) action;
                switch(((OpenSandboxAction) action).reason){
                    case ASSUMPTION:{
                        reason = "Assumption";
                        break;
                    }
                    case ABSURDITY_ELIMINATION: {
                        reason = "Absurdity elimination";
                        break;
                    }
                    case DISJUNCTION_INTRODUCTION: {
                        reason = "Disjunction elimination";
                        break;
                    }

                }
                Intent i = new Intent(getApplicationContext(),Sandbox.class);
                //sandboxAction=(OpenSandboxAction) action;
                //i.putExtra("STRING_I_NEED", reason);
                startActivity(i);
            }
            else if (action instanceof RefreshInventoryAction){
                inventories = ((RefreshInventoryAction) action).inventories;
                assumptions = ((RefreshInventoryAction) action).assumptions;
            }
            else if(action instanceof VictoryConditionMetAction){
                victory=true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"You are winner!",Toast.LENGTH_LONG).show();
                    }
                });
                finish();
            }
            else if(action instanceof SaveUserDataAction){
                return;
            }
            gameChange.release();
        }
    }

}


