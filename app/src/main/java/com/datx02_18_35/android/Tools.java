package com.datx02_18_35.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.datx02_18_35.model.expression.Absurdity;
import com.datx02_18_35.model.expression.Conjunction;
import com.datx02_18_35.model.expression.Disjunction;
import com.datx02_18_35.model.expression.Expression;
import com.datx02_18_35.model.expression.Implication;
import com.datx02_18_35.model.expression.Operator;
import com.datx02_18_35.model.expression.Proposition;

import game.logic_game.R;

/**
 * Created by raxxor on 2018-03-15.
 */

class Tools {

    public static final String debug = "test123";

    //screen
    static float getWidthDp(Context context){
        float px = Resources.getSystem().getDisplayMetrics().widthPixels;
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
    //"generate card"

    static class CardDeflator{
        final CardView topCardView;
        final String dots = " .. ";


        CardDeflator(CardView cardView, Expression expr){
            topCardView = cardView;

            //whole card one symbol
            if(expr instanceof Proposition | expr instanceof Absurdity){
                topCardView.removeAllViews();
                TextView text = new TextView(topCardView.getContext());
                text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                text.setGravity(Gravity.CENTER);
                text.setText(expr.toString());
                text.setTextSize(40);
                text.setTextColor(Color.BLUE);
                topCardView.addView(text);
            }
            else {
                Operator op = (Operator) expr;
                Expression op1 = op.getOperand1();
                Expression op2 = op.getOperand2();


                topCardView.findViewById(R.id.card_frame_middle).setBackgroundColor(Color.WHITE);
                ImageView middleImage = topCardView.findViewById(R.id.card_image_middle);
                if(op instanceof Implication){
                    middleImage.setBackgroundResource(R.drawable.vertical_implication);
                }
                else if(op instanceof Disjunction){
                    middleImage.setBackgroundResource(R.drawable.horizontal_disjunction);
                }
                else if(op instanceof Conjunction){
                    middleImage.setBackgroundResource(R.drawable.horizontal_conjunction);
                }

                // set big operator in middle + no complex on up/down card.
                if ((op1 instanceof Proposition | op1 instanceof Absurdity) &  (op2 instanceof Proposition | op2 instanceof Absurdity) ){
                    rmView(R.id.card_frame_lower,topCardView);
                    rmView(R.id.card_frame_upper,topCardView);
                    rmView(R.id.card_card_1_1,topCardView);
                    rmView(R.id.card_card_2_4,topCardView);

                    mParent(R.id.card_card_1_2,topCardView);
                    mParent(R.id.card_card_2_3,topCardView);


                    sText(R.id.card_text_2,op1.toString(),topCardView);
                    sText(R.id.card_text_3,op2.toString(),topCardView);
                }
                else{
                    ImageView upperImage = topCardView.findViewById(R.id.card_image_upper);
                    ImageView lowerImage = topCardView.findViewById(R.id.card_image_lower);
                    topCardView.findViewById(R.id.card_frame_lower).setBackgroundColor(Color.WHITE);
                    topCardView.findViewById(R.id.card_frame_upper).setBackgroundColor(Color.WHITE);



                    if( op1 instanceof Operator &  (op2 instanceof Proposition | op2 instanceof Absurdity) ) {
                        //upper
                        Operator upper = (Operator) op1;
                        Expression upper_left = upper.getOperand1();
                        Expression upper_right = upper.getOperand2();

                        //Upper left
                        if( upper_left instanceof Operator ){
                            sText(R.id.card_text_2, dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_2, upper_left.toString(),topCardView);
                        }

                        //Upper right
                        if( upper_right instanceof Operator ){
                            sText(R.id.card_text_1, dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_1, upper_right.toString(),topCardView);
                        }
                        //Upper middle
                        if(upper instanceof Implication){
                            upperImage.setBackgroundResource(R.drawable.horizontal_implication);
                        }
                        else if(upper instanceof Disjunction){
                            upperImage.setBackgroundResource(R.drawable.vertical_disjunction);
                        }
                        else if(upper instanceof Conjunction){
                            upperImage.setBackgroundResource(R.drawable.vertical_conjunction);
                        }



                        //lower
                        rmView(R.id.card_frame_lower,topCardView);
                        rmView(R.id.card_card_2_4,topCardView);
                        mParent(R.id.card_card_2_3,topCardView);
                        sText(R.id.card_text_3,op2.toString(),topCardView);
                    }

                    if( (op1 instanceof Proposition | op1 instanceof Absurdity) & op2 instanceof Operator  ) {

                        //lower
                        Operator lower = (Operator) op2;
                        Expression lower_left = lower.getOperand1();
                        Expression lower_right = lower.getOperand2();

                        //lower left
                        if(lower_left instanceof Operator){
                            sText(R.id.card_text_3,dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_3,lower_left.toString(),topCardView);
                        }

                        //lower left
                        if(lower_right instanceof Operator){
                            sText(R.id.card_text_4,dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_4,lower_right.toString(),topCardView);
                        }
                        //Lower middle
                        if(lower instanceof Implication){
                            lowerImage.setBackgroundResource(R.drawable.horizontal_implication);
                        }
                        else if(lower instanceof Disjunction){
                            lowerImage.setBackgroundResource(R.drawable.vertical_disjunction);
                        }
                        else if(lower instanceof Conjunction){
                            lowerImage.setBackgroundResource(R.drawable.vertical_conjunction);
                        }



                        //upper
                        rmView(R.id.card_frame_upper,topCardView);
                        rmView(R.id.card_card_1_1,topCardView);
                        mParent(R.id.card_card_1_2,topCardView);
                        sText(R.id.card_text_2,op1.toString(),topCardView);
                    }

                    if( op1 instanceof Operator & op2 instanceof Operator ){
                        //lower and upper
                        Operator lower = (Operator) op2;
                        Expression lower_left = lower.getOperand1();
                        Expression lower_right = lower.getOperand2();

                        //lower left
                        if(lower_left instanceof Operator){
                            sText(R.id.card_text_3,dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_3,lower_left.toString(),topCardView);
                        }

                        //lower left
                        if(lower_right instanceof Operator){
                            sText(R.id.card_text_4,dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_4,lower_right.toString(),topCardView);
                        }
                        //Lower middle
                        if(lower instanceof Implication){
                            lowerImage.setBackgroundResource(R.drawable.horizontal_implication);
                        }
                        else if(lower instanceof Disjunction){
                            lowerImage.setBackgroundResource(R.drawable.vertical_disjunction);
                        }
                        else if(lower instanceof Conjunction){
                            lowerImage.setBackgroundResource(R.drawable.vertical_conjunction);
                        }




                        Operator upper = (Operator) op1;
                        Expression upper_left = upper.getOperand1();
                        Expression upper_right = upper.getOperand2();

                        //Upper left
                        if( upper_left instanceof Operator ){
                            sText(R.id.card_text_2, dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_2, upper_left.toString(),topCardView);
                        }

                        //Upper right
                        if( upper_right instanceof Operator ){
                            sText(R.id.card_text_1, dots,topCardView);
                        }
                        else{
                            sText(R.id.card_text_1, upper_right.toString(),topCardView);
                        }
                        //Upper middle
                        if(upper instanceof Implication){
                            upperImage.setBackgroundResource(R.drawable.horizontal_implication);
                        }
                        else if(upper instanceof Disjunction){
                            upperImage.setBackgroundResource(R.drawable.vertical_disjunction);
                        }
                        else if(upper instanceof Conjunction){
                            upperImage.setBackgroundResource(R.drawable.vertical_conjunction);
                        }

                    }
                }
            }
        }

        //remove view by id
        private static void rmView(int rId,CardView card){
            View view = card.findViewById(rId);
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.removeView(view);
        }
        //expand to fill by id
        private static void mParent(int rId,CardView card){
            View view = card.findViewById(rId);
            view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        //sets text in textview by id.
        private static void sText(int rId,String s,CardView card){
            TextView t = card.findViewById(rId);
            t.setText(s);
        }
    }
}
