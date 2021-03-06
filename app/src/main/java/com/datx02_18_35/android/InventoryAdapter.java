package com.datx02_18_35.android;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datx02_18_35.controller.Controller;
import com.datx02_18_35.controller.dispatch.actions.viewActions.RequestMoveFromInventoryAction;
import com.datx02_18_35.model.GameException;
import com.datx02_18_35.model.expression.Expression;

import java.util.ArrayList;
import java.util.List;

import game.logic_game.R;

/**
 * Created by Magnu on 2018-03-21.
 */

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> implements View.OnClickListener {
    private List<Expression> dataSet;
    GameBoard activity;


    public InventoryAdapter(List<Expression> dataSet, GameBoard activity){
        this.dataSet = dataSet;
        this.activity = activity;
    }


    @Override
    public InventoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_expression, parent,false);
        return new InventoryAdapter.ViewHolder(cardView);
    }

    @Override
    public void onClick(View view) {
        if (view!=null) {
            Expression selectedCard = dataSet.get((int) view.getTag());
            try {
                Controller.getSingleton().handleAction(new RequestMoveFromInventoryAction(GameBoard.boardCallback,selectedCard));
            } catch (GameException e) {
                e.printStackTrace();
            }
            Fx.blink_animation(view.getContext(), view);
        }
    }

    @Override
    public void onBindViewHolder(InventoryAdapter.ViewHolder holder, int position) {
        holder.cardView.setOnClickListener(this);
        holder.setIsRecyclable(false);
        holder.cardView.setTag(position);
        holder.cardView.setTag(R.string.viewholders,holder);
        holder.cardView.setBackgroundColor(Color.GRAY);
        if(dataSet.get(position)!= null & !holder.alreadyBound) {
            CardInflator.inflate(holder.cardView, dataSet.get(position),120,170,false);
            holder.alreadyBound = true;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        boolean alreadyBound=false;


        ViewHolder(CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }
}
