package com.datx02_18_35.android;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.datx02_18_35.controller.Controller;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestRulesAction;
import com.datx02_18_35.model.GameException;
import com.datx02_18_35.model.expression.Expression;

import java.util.ArrayList;
import java.util.HashMap;

import game.logic_game.R;

/**
 * Created by raxxor on 2018-02-08.
 */

public class GameCardAdapter extends RecyclerView.Adapter<GameCardAdapter.ViewHolder> implements View.OnClickListener {
    private int currentHighestSelectedCard=0;
    private Expression goal;
    ArrayList<Expression> dataSet;
    private Expression assumptionOfCurrentScope;
    ArrayList<Integer> selected=new ArrayList<>();
    HashMap<Integer, CardView> selectedView = new HashMap<>();
    private boolean clickable=true;
    private GameBoard activity;
    private float cardHeight,cardWidth;


    GameCardAdapter(ArrayList<Expression> dataSet, GameBoard activity, float cardHWidth, float cardHeight){
        this.dataSet = dataSet;
        this.activity=activity;
        this.cardHeight=cardHeight;
        this.cardWidth=cardHWidth;
    }

    void updateBoard(final Iterable<Expression> data,final Expression assumption){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataSet.clear();
                for (Expression expression : data) {dataSet.add(expression);}
                assumptionOfCurrentScope=assumption;
                notifyDataSetChanged();
                restoreSelections();
                //scroll to last pos to show new card
                activity.recyclerViewLeft.scrollToPosition(dataSet.size()-1);
            }
        });

    }

    void updateGoal(Expression goal){
        this.goal=goal;
    }


    public void setUnclickable(){
        clickable=false;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_expression, parent,false);
        cardView.setCardBackgroundColor(Color.GRAY);
        cardView.setCardElevation(10);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //remember to check if selected and highlight on bind.
        holder.cardView.setOnClickListener(this);
        holder.cardView.setTag(position);
        holder.setIsRecyclable(false);
        if(selected.contains(position)){
            setAnimations(holder.cardView);
        }
        if(null != dataSet.get(position) & !holder.alreadyBound){
            if(dataSet.get(position).equals(assumptionOfCurrentScope)) {
                CardInflator.inflateAssumption(holder.cardView, dataSet.get(position), cardWidth, cardHeight, false);
            }else {
                CardInflator.inflate(holder.cardView, dataSet.get(position), cardWidth, cardHeight, false);
            }
            holder.alreadyBound=true;
        }
    }



    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    @Override
    public void onClick(View v) {
        if(clickable) {
            activity.newSelection(dataSet.get((int) v.getTag()), v);
            if (activity.infoWindowClicked) {
                activity.infoWindowClicked = false;
                activity.popupWindow.dismiss();
            }
        }
    }

    void setSelection(Expression expression, CardView v) {
        selected.add((int) v.getTag());
        selectedView.put((int) v.getTag(),v);
        v.findViewById(R.id.card_number_text_view).setVisibility(View.VISIBLE);
        currentHighestSelectedCard++;
        v.findViewById(R.id.card_number_text_view).setTag(R.id.card_number,currentHighestSelectedCard);
        ((TextView)v.findViewById(R.id.card_number_text_view)).setText(""+ v.findViewById(R.id.card_number_text_view).getTag(R.id.card_number));
        setAnimations(v);
    }

    void resetSelection(Expression expression, CardView v) {
        v.findViewById(R.id.card_number_text_view).setVisibility(View.GONE);
        int deSelectNumber = (int)v.findViewById(R.id.card_number_text_view).getTag(R.id.card_number);
        currentHighestSelectedCard--;
        int loopStop=selected.size();
        for(int x =0 ; loopStop > x ;x++){
            CardView cardViewNumberChange = selectedView.get(selected.get(x));
            cardViewNumberChange.setVisibility(View.GONE);
            TextView textView = cardViewNumberChange.findViewById(R.id.card_number_text_view);
            int selectionNumber = (int)textView.getTag(R.id.card_number);
            if(selectionNumber>deSelectNumber){
                textView.setTag(R.id.card_number,selectionNumber-1);
                textView.setText(""+textView.getTag(R.id.card_number));
            }
           if (selected.get(x)==v.getTag()){
                selected.remove(x);
                x--;
                loopStop--;
           }
        }
        CardView cardView = selectedView.get(v.getTag());
        restoreAnimations(cardView);
        selectedView.remove(v.getTag());

    }

    void restoreSelections(){
        for(CardView view : selectedView.values()){
            restoreAnimations(view);
            TextView textView=view.findViewById(R.id.card_number_text_view);
            textView.setVisibility(View.GONE);
        }
        currentHighestSelectedCard =0;
        selected.clear();
        selectedView.clear();
        try {
            if(!activity.isLevelCompleted()){
                Controller.getSingleton().handleAction(new RequestRulesAction(GameBoard.boardCallback, new ArrayList<Expression>()));
            }

        } catch (GameException e) {
            e.printStackTrace();
        }
    }
    private void restoreAnimations(CardView cardView){

        Fx.deselectAnimation(cardView.getContext(), cardView);
    }
    private void setAnimations(CardView cardView){

        Fx.selectAnimation(cardView.getContext(), cardView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        boolean alreadyBound=false;

        ViewHolder(CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }

}

