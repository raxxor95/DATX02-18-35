package com.datx02_18_35.android;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.datx02_18_35.controller.Controller;
import com.datx02_18_35.controller.dispatch.ActionConsumer;
import com.datx02_18_35.controller.dispatch.IllegalActionException;
import com.datx02_18_35.controller.dispatch.actions.Action;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.OpenSandboxAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.RefreshCurrentLevelAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.RefreshGameboardAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.RefreshHypothesisAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.RefreshInventoryAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.RefreshRulesAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.RefreshScopeLevelAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.SaveUserDataAction;
import com.datx02_18_35.controller.dispatch.actions.controllerAction.VictoryConditionMetAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestAbortSessionAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestApplyRuleAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestAssumptionAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestCloseScopeAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestCurrentLevelAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestDeleteFromGameboardAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestGameboardAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestHypothesisAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestInventoryAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestRulesAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestScopeLevelAction;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestStartNextLevelAction;
import com.datx02_18_35.model.GameException;
import com.datx02_18_35.model.expression.Expression;
import com.datx02_18_35.model.game.VictoryInformation;
import com.datx02_18_35.model.level.Level;
import com.datx02_18_35.model.rules.Rule;
import com.datx02_18_35.model.rules.RuleType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import game.logic_game.R;

public class GameBoard extends AppCompatActivity implements View.OnClickListener,Animation.AnimationListener {
    private TextView scoreView;
    private Button nextLevel;
    private Button mainMenu;
    private Toolbar toolbar;
    private TextView scopeLevel;
    private ConstraintLayout inventoryLayout;
    private ConstraintLayout victoryScreen;
    private Animation slide_left,delete,slide_right;
    private boolean clickable=true;
    private boolean sandboxOpened = false;
    private boolean victory=false;
    private Iterable<Expression> hypothesis;
    private Iterable<Iterable<Expression>> inventories;
    private Iterable<Expression> assumptions;
    private int scopeLevelInt;

    public boolean infoWindowClicked=true;
    public PopupWindow popupWindow;
    public View popUpView;
    public View.OnClickListener clickListener;
    public CardView winningAnimationCard=null;

    public static BoardCallback boardCallback;
    public static OpenSandboxAction sandboxAction=null;
    public static Level level;

    //recyclerviews
    public RecyclerView recyclerViewLeft,recyclerViewRight,parentInvRecyclerView;
    public GridLayoutManager gridLayoutManagerLeft, gridLayoutManagerRight;
    public LinearLayoutManager parentInvRecLayoutManager;
    public ScopeHolderAdapter parentHolderAdapter;
    public GameRuleAdapter adapterRight;
    public GameCardAdapter adapterLeft;


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //inflate so pop-up window can be added.
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") final View contentView = inflater.inflate(R.layout.activity_game, null);
        setContentView(contentView);

        boardCallback = new BoardCallback();

        try {
            Controller.getSingleton().handleAction(new RequestInventoryAction(GameBoard.boardCallback));
        }
        catch (GameException e){
            e.printStackTrace();
        }
        try {
            Controller.getSingleton().handleAction(new RequestHypothesisAction(boardCallback));
        } catch (GameException e) {
            e.printStackTrace();
        }



        //screen size
        Tools.GameBoardScreenInfo gameBoardScreenInfo = new Tools.GameBoardScreenInfo();



        initLeftSide(gameBoardScreenInfo.cardWidth, gameBoardScreenInfo.cardHeight, gameBoardScreenInfo.spanCounts);
        initRightSide(gameBoardScreenInfo.cardWidth, gameBoardScreenInfo.cardHeight);
        initInventory();
        try {
            Controller.getSingleton().handleAction(new RequestGameboardAction(boardCallback));
            Controller.getSingleton().handleAction(new RequestCurrentLevelAction(boardCallback));
        } catch (GameException e) {
            e.printStackTrace();
        }


        //Set up victory screen buttons and layout
        victoryScreen = findViewById(R.id.victory_screen);
        victoryScreen.setVisibility(View.GONE);
        nextLevel = findViewById(R.id.victory_screen_next_level_button);
        nextLevel.setOnClickListener(this);
        mainMenu = findViewById(R.id.victory_screen_main_menu_button);
        mainMenu.setOnClickListener(this);
        scoreView = findViewById(R.id.win_score);


        findViewById(R.id.inventory_button).setOnClickListener(this);
        findViewById(R.id.open_inventory).setOnClickListener(this);
        findViewById(R.id.close_inventory).setOnClickListener(this);

        //Set toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        scopeLevel = findViewById(R.id.toolbar_text);
        scopeLevel.setText("scope 0");

        //pop-up window for goal and description
        ImageView infoButton = findViewById(R.id.toolbar_goal);
        infoButton.setOnClickListener(this);
        ImageView trashCan = findViewById(R.id.trash_can);
        trashCan.setOnClickListener(this);
        ImageView assumption = findViewById(R.id.item_assumption);
        assumption.setOnClickListener(this);


        //Animations
        slide_left = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slide_left.setAnimationListener(this);
        delete = AnimationUtils.loadAnimation(this, R.anim.delete);
        delete.setAnimationListener(this);

        slide_right = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        slide_right.setAnimationListener(this);




        //load pop-up window with goal
        loadPopUpWindow(contentView);
        clickListener=this;

        //set goal on board
        CardInflator.inflate((CardView) findViewById(R.id.gameBoard_goal),level.goal,gameBoardScreenInfo.cardWidth,gameBoardScreenInfo.cardHeight,false);


    }


    //if app has been down for a while memory will be trimmed and the app will crash
    //unless we release memory, i.e lets kill it in a controlled way
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(TRIM_MEMORY_COMPLETE==level){
            finish();
        }
    }

    public boolean isLevelCompleted() {
        return victory;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        // if winning animation ongoing, on click abort it.
        if(winningAnimationCard!=null && victory){
            winningAnimationCard.animate().cancel();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void loadPopUpWindow(final View contentView){
        contentView.post(new Runnable() {
            @SuppressLint("InflateParams")
            @Override
            public void run() {
                if(popupWindow==null){
                    // Inflate the custom layout/view
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    popUpView = Objects.requireNonNull(inflater).inflate(R.layout.pop_up_window,null);
                    popUpView.findViewById(R.id.popup_exit_button).setOnClickListener(clickListener);
                    popupWindow = new PopupWindow(popUpView);

                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if(infoWindowClicked){
                                infoWindowClicked=false;
                                popupWindow.dismiss();
                            }

                        }
                    });
                    popupWindow.setOutsideTouchable(true);

                    View bigView = findViewById(R.id.game_board_bottom);
                    int height = bigView.getHeight() * 4 / 5;
                    int width = bigView.getWidth()  - bigView.getWidth() / 15;
                    popupWindow.setWidth(width);
                    popupWindow.setHeight(height);
                    ((TextView)popUpView.findViewById(R.id.popup_level_title)).setText(level.title);
                    ((TextView)popUpView.findViewById(R.id.popup_level_description)).setText(level.description);
                }
                popupWindow.showAtLocation(contentView, Gravity.CENTER,0,0);
            }
        });



    }



    //only use one spanCount as rules don't need 2 columns
    private void initRightSide(float cardWidth, float cardHeight) {

        recyclerViewRight = findViewById(R.id.game_right_side);
        // use a grid layout manager
        gridLayoutManagerRight = new GridLayoutManager(getApplication(), 1);
        recyclerViewRight.setLayoutManager(gridLayoutManagerRight);

        ArrayList<Rule> list = new ArrayList<>();
        //attach list to adapter
        adapterRight = new GameRuleAdapter(list,this,cardWidth,cardHeight);

        //attach adapter
        recyclerViewRight.setAdapter(adapterRight);
    }

    //use remaining spanCounts, spanCount-1
    private void initLeftSide(float cardWidth, float cardHeight, int spanCount) {

        recyclerViewLeft = findViewById(R.id.game_left_side);
        // use a grid layout manager
        gridLayoutManagerLeft = new GridLayoutManager(getApplication(), spanCount-1);
        recyclerViewLeft.setLayoutManager(gridLayoutManagerLeft);


        adapterLeft = new GameCardAdapter(new ArrayList<Expression>(),this, cardWidth, cardHeight);

        recyclerViewLeft.setAdapter(adapterLeft);

    }
    private void initInventory() {
        parentInvRecyclerView = (RecyclerView) findViewById(R.id.inv_recycler_view);
        parentInvRecLayoutManager = new LinearLayoutManager(this);
        parentInvRecyclerView.setLayoutManager(parentInvRecLayoutManager);
        parentInvRecyclerView.setHasFixedSize(false);
        parentHolderAdapter = new ScopeHolderAdapter(this);
        parentInvRecyclerView.setAdapter(parentHolderAdapter);


        View gameView = this.findViewById(android.R.id.content);
        inventoryLayout = findViewById(R.id.inventory_container);
        inventoryLayout.setVisibility(View.GONE);

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
        if (inventoryLayout.isShown()) {
            startAnimation(slide_left, inventoryLayout);
        }
    }
    public void showInventory(){

        try {
            Controller.getSingleton().handleAction(new RequestScopeLevelAction(boardCallback));
            Controller.getSingleton().handleAction(new RequestInventoryAction(boardCallback));
            Controller.getSingleton().handleAction(new RequestHypothesisAction(boardCallback));
        } catch (GameException e) {
            e.printStackTrace();
        }

        if (!inventoryLayout.isShown()){
            parentHolderAdapter.updateInventory(hypothesis, assumptions, inventories);
            startAnimation(slide_right, inventoryLayout);
        }
    }
    public void deleteSelection(){
        for(CardView view : adapterLeft.selectedView.values()){
            startAnimation(delete, view);
        }
    }
    public void startAnimation(Animation a, View v ){
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public synchronized void newSelection(Object object, View v) {
        if (object instanceof Expression){
            Expression expression = (Expression) object;
            //already selected
            if (adapterLeft.selected.contains(v.getTag())){
                adapterLeft.resetSelection(expression, (CardView) v);

            }
            //not selected
            else if(!adapterLeft.selected.contains(v.getTag())){
                adapterLeft.setSelection(expression, (CardView) v);
            }
            //update rightside
            try {
                ArrayList<Expression> sendList = new ArrayList<>();
                for (Integer i : adapterLeft.selected){
                    sendList.add(adapterLeft.dataSet.get(i));
                }
                Controller.getSingleton().handleAction(new RequestRulesAction(boardCallback, sendList));
            } catch (GameException e) {
                e.printStackTrace();
            }

        }
        else if(object instanceof Rule){
            try {
                Controller.getSingleton().handleAction(new RequestApplyRuleAction(boardCallback,(Rule)object));
            } catch (GameException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //shutdown session
        try {
            if(!victory){
                Controller.getSingleton().handleAction(new RequestAbortSessionAction());
            }
            if(infoWindowClicked){
                popupWindow.dismiss();
            }
        } catch (GameException e) {
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
        switch(menu.getItemId()){
            case R.id.item_exit:
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if(animation==slide_left) {
            recyclerViewRight.setVisibility(View.VISIBLE);
            recyclerViewLeft.setVisibility(View.VISIBLE);
        }
        else if(animation==slide_right){
            inventoryLayout.setVisibility(View.VISIBLE);
            parentInvRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation==slide_left) {
            inventoryLayout.setVisibility(View.GONE);
            parentInvRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.close_inventory).setClickable(false);
            inventoryLayout.setVisibility(View.GONE);
            parentInvRecyclerView.setVisibility(View.GONE);
            if(sandboxOpened){
                try {
                    Controller.getSingleton().handleAction((new RequestAssumptionAction(boardCallback)));
                    scopeLevel.setText("");
                } catch (GameException e) {
                    e.printStackTrace();
                }
                sandboxOpened=false;
            }
        }
        else if(animation==slide_right){
            recyclerViewRight.setVisibility(View.GONE);
            recyclerViewLeft.setVisibility(View.GONE);
            findViewById(R.id.close_inventory).setClickable(true);
            inventoryLayout.setVisibility(View.VISIBLE);
            parentInvRecyclerView.setVisibility(View.VISIBLE);

        }
        else if(animation==delete){
            ArrayList<Expression> sendList = new ArrayList<>();
            for (Integer i : adapterLeft.selected){
                sendList.add(adapterLeft.dataSet.get(i));
            }
            for(CardView view : adapterLeft.selectedView.values()){
                view.setVisibility(View.GONE);
                view.setClickable(false);
            }
            adapterLeft.selected.clear();
            adapterLeft.selectedView.clear();
            try {

                Controller.getSingleton().handleAction(new RequestDeleteFromGameboardAction(boardCallback,sendList));
            } catch (GameException e) {
                e.printStackTrace();
            }
            sendList.clear();
            try {
                Controller.getSingleton().handleAction(new RequestRulesAction(boardCallback, sendList));
            } catch (GameException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    public class BoardCallback extends ActionConsumer {
        @Override
        public void handleAction(final Action action) throws GameException {
            if (action instanceof RefreshGameboardAction){
                Iterable<Expression> data =  ((RefreshGameboardAction) action).boardExpressions;
                Expression assumption = ((RefreshGameboardAction) action).assumptionExpression;
                adapterLeft.updateBoard(data,assumption);

            }
            else if (action instanceof SaveUserDataAction){
                Tools.writeUserData( ((SaveUserDataAction) action).userData, getApplicationContext());
            }
            else if (action instanceof RefreshCurrentLevelAction){
                level = ((RefreshCurrentLevelAction) action).level;
                findViewById(R.id.item_assumption).setVisibility(
                        level.ruleSet.contains(RuleType.IMPLICATION_INTRODUCTION) ? View.VISIBLE : View.INVISIBLE);
                adapterLeft.updateGoal(level.goal);
            }
            else if (action instanceof RefreshRulesAction){
                Collection<Rule> data = ((RefreshRulesAction) action).rules;
                adapterRight.updateBoard(data);
            }
            else if (action instanceof OpenSandboxAction){
                String reason;
                sandboxAction =(OpenSandboxAction) action;
                switch(sandboxAction.reason){
                    case ASSUMPTION:{
                        reason = "assumption";
                        break;
                    }
                    case ABSURDITY_ELIMINATION: {
                        reason = "absurdity elimination";
                        break;
                    }
                    case DISJUNCTION_INTRODUCTION: {
                        reason = "disjunction introduction";
                        break;
                    }
                    case LAW_OF_EXCLUDING_MIDDLE: {
                        reason = "law of excluding middle";
                        break;
                    }
                    default: {
                        throw new IllegalActionException(action, "Unknown reason: " + sandboxAction.reason);
                    }

                }
                Intent i = new Intent(getApplicationContext(),Sandbox.class);
                //sandboxAction=(OpenSandboxAction) action;
                i.putExtra("reason", reason);
                startActivity(i);
            }
            else if (action instanceof RefreshInventoryAction){
                inventories = ((RefreshInventoryAction) action).inventories;
                assumptions = ((RefreshInventoryAction) action).assumptions;
            }
            else if (action instanceof RefreshHypothesisAction){
                hypothesis = ((RefreshHypothesisAction) action).hypothesis;
            }
            else if(action instanceof RefreshScopeLevelAction){
                scopeLevelInt=((RefreshScopeLevelAction) action).scopeLevel;
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        scopeLevel.setText("Scope: " + ((RefreshScopeLevelAction) action).scopeLevel);
                    }
                });
            }
            else if(action instanceof VictoryConditionMetAction){
                victory=true;
                adapterLeft.setUnclickable();
                adapterRight.setUnclickable();
                clickable=false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final VictoryInformation victoryInformation = ((VictoryConditionMetAction) action).victoryInformation;
                        Expression goal= victoryInformation.goal;
                        int position=adapterLeft.dataSet.indexOf(goal);
                        adapterLeft.dataSet.remove(position);
                        adapterLeft.notifyItemRemoved(position);
                        winningAnimationCard = (CardView) LayoutInflater.from(
                                Objects.requireNonNull(getCurrentFocus()).getContext()).inflate
                                (R.layout.card_expression,(ViewGroup) getCurrentFocus().getParent(),false);

                        CardInflator.inflate(winningAnimationCard,victoryInformation.goal,120,170,false);
                        ((ViewGroup)findViewById(android.R.id.content)).addView(winningAnimationCard);
                        CardInflator.inflate((CardView) findViewById(R.id.victoryScreen_goalCard),victoryInformation.goal,120,170,false);
                        winningAnimationCard.animate().setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }
                            private String returnStepOrSteps(int step) {
                                if(step==1) return "step";
                                return "steps";
                            }

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                ((ViewGroup)winningAnimationCard.getParent()).removeView(winningAnimationCard);
                                if(!victoryInformation.hasNextLevel){
                                    nextLevel.setVisibility(View.GONE);
                                }
                                victoryScreen.setVisibility(View.VISIBLE);

                                final String s = "You've completed the goal! Good job! \n";
                                if(victoryInformation.previousScore<0) {
                                    scoreView.setText(s + "You finished in " + victoryInformation.newScore + " " + returnStepOrSteps(victoryInformation.newScore) + "." +"\n" +
                                            "No previous finish.");
                                }
                                else {
                                    scoreView.setText(s + "You finished in " + victoryInformation.newScore + " " + returnStepOrSteps(victoryInformation.newScore) + "." +"\n" +
                                            "Your previous best finish was " + victoryInformation.previousScore + " " + returnStepOrSteps(victoryInformation.newScore) + ".");
                                }
                                winningAnimationCard=null; //restore to not perhaps maybe accidentally trigger animation cancel in disptachTouchevent :)
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int cardHeight=winningAnimationCard.getLayoutParams().height;
                        int cardWidth=winningAnimationCard.getLayoutParams().width;
                        winningAnimationCard.setX((float) displayMetrics.widthPixels/2-cardWidth/2 );
                        winningAnimationCard.setY((float) displayMetrics.heightPixels/2-cardHeight/2);
                        winningAnimationCard.animate().setDuration(5000).scaleY(2).scaleX(2).start();
                    }
                });
            }
        }
    }
    @Override
    public void onBackPressed(){
        if(infoWindowClicked){
            popupWindow.dismiss();
            infoWindowClicked=false;
        }
        else if(inventoryLayout.isShown()){
            closeInventory();
        }
        else if(scopeLevelInt>=1){
            try{
                Controller.getSingleton().handleAction(new RequestCloseScopeAction(GameBoard.boardCallback));
            }
            catch (GameException e) {
                e.printStackTrace();
            }
        }
        else {
            super.onBackPressed();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        adapterLeft.restoreSelections();
        try {
            Controller.getSingleton().handleAction(new RequestScopeLevelAction(GameBoard.boardCallback));
        }
        catch (GameException e){
            e.printStackTrace();
        }
    }



    public void onClick(View view){
        switch (view.getId()){
            case R.id.popup_exit_button :
                if(clickable && infoWindowClicked){
                    this.popupWindow.dismiss();
                }
                break;
            case R.id.toolbar_goal :
                if(clickable&& !infoWindowClicked){
                    infoWindowClicked=true;
                    View rootView = Objects.requireNonNull(getCurrentFocus()).getRootView();
                    if(rootView!=null){
                        loadPopUpWindow(getCurrentFocus().getRootView());
                    }

                }
                break;
            case R.id.victory_screen_next_level_button:{
                try {
                    Controller.getSingleton().handleAction(new RequestStartNextLevelAction(GameBoard.boardCallback));
                    Controller.getSingleton().handleAction(new RequestRulesAction(GameBoard.boardCallback,new ArrayList<Expression>()));
                    Intent intent = new Intent(this, GameBoard.class); //create intent
                    startActivity(intent); //start intent
                    finish();
                }
                catch (GameException e){
                    e.printStackTrace();
                }
                break;
            }
            case R.id.victory_screen_main_menu_button: {
                try {
                    Controller.getSingleton().handleAction(new RequestAbortSessionAction());
                    finish();
                }
                catch (GameException e){
                    e.printStackTrace();
                }
                break;
            }
            case R.id.inventory_button:{
                if(clickable) {
                    if (inventoryLayout.isShown()) {
                        closeInventory();
                    } else {
                        showInventory();
                    }
                    break;
                }
            }
            case R.id.open_inventory:{
                if(clickable) {
                    if (inventoryLayout.isShown()) {
                        closeInventory();
                    } else {
                        showInventory();
                    }
                    break;
                }
            }
            case R.id.close_inventory:{
                if(clickable) {
                    if (inventoryLayout.isShown()) {
                        closeInventory();
                    } else {
                        showInventory();
                    }
                    break;
                }
            }
            case R.id.trash_can:{
                if(clickable) {
                    deleteSelection();
                    break;
                }
            }
            case R.id.item_assumption: {
                if (clickable) {
                    if (inventoryLayout.isShown()) {
                        sandboxOpened = true;
                        closeInventory();
                    } else {
                        try {
                            Controller.getSingleton().handleAction((new RequestAssumptionAction(boardCallback)));
                            scopeLevel.setText("");
                        } catch (GameException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        }
    }

}


