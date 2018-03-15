package com.datx02_18_35.android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datx02_18_35.model.expression.Expression;

import java.util.ArrayList;

import game.logic_game.R;


/**
 * Created by Magnus on 2018-02-14.
 */


public class FragmentInventory extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recAdapter;
    private RecyclerView.LayoutManager recLayoutManager;
    private ItemTouchHelper itemTouchHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = (RecyclerView) frag.findViewById(R.id.game_recycler_view);
        // use a grid layout manager
        recLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(recLayoutManager);


        // specify an adapter (see also next example)
        ArrayList<Expression> list = new ArrayList<Expression>();


        recAdapter = new GameCardAdapter(list);



        recyclerView.setAdapter(recAdapter);

        return frag;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //do your stuff for your fragment here


    }


    public void addToInventory(/*Expression newCard*/){
        //list.add(newCard);
        //recyclerView.notifyDataSetChanged();
    }


}
