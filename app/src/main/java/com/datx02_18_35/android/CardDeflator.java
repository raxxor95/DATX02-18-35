package com.datx02_18_35.android;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.datx02_18_35.model.expression.Absurdity;
import com.datx02_18_35.model.expression.Conjunction;
import com.datx02_18_35.model.expression.Disjunction;
import com.datx02_18_35.model.expression.Expression;
import com.datx02_18_35.model.expression.Implication;
import com.datx02_18_35.model.expression.Operator;
import com.datx02_18_35.model.expression.Proposition;

import java.util.Map;

import game.logic_game.R;

/**
 * Created by robin on 2018-03-27.
 */

public class CardDeflator{
    private CardDeflator(){};

    private static final String dots = " .. ";


    public static void deflate(CardView cardView, Expression expr, Map<String,String> symbolMap){

        //whole card one symbol
        if(expr instanceof Proposition | expr instanceof Absurdity){
            cardView.removeAllViews();

            CardView card =  new CardView(cardView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(5,5,5,5);
            card.setLayoutParams(params);
            ImageView imageView = new ImageView(cardView.getContext());
            card.addView(imageView);
            cardView.addView(card);
            sSymbol(expr,imageView,symbolMap);
        }
        else {
            Operator op = (Operator) expr;
            Expression op1 = op.getOperand1();
            Expression op2 = op.getOperand2();


            cardView.findViewById(R.id.card_frame_middle).setBackgroundColor(Color.WHITE);
            ImageView middleImage = cardView.findViewById(R.id.card_image_middle);
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
                rmView(R.id.card_frame_lower,cardView);
                rmView(R.id.card_frame_upper,cardView);
                rmView(R.id.card_card_1_1,cardView);
                rmView(R.id.card_card_2_4,cardView);

                mParent(R.id.card_card_1_2,cardView);
                mParent(R.id.card_card_2_3,cardView);

                if(op1 instanceof Proposition) {
                    sSymbol((Proposition) op1, cardView, R.id.card_image_2, symbolMap);
                }else {
                    sSymbol((Absurdity) op1,cardView,R.id.card_image_2,symbolMap);
                }
                if(op2 instanceof Proposition) {
                    sSymbol((Proposition) op2, cardView, R.id.card_image_3, symbolMap);
                }else {
                    sSymbol((Absurdity) op2, cardView, R.id.card_image_3, symbolMap);
                }
            }
            else{
                ImageView upperImage = cardView.findViewById(R.id.card_image_upper);
                ImageView lowerImage = cardView.findViewById(R.id.card_image_lower);
                cardView.findViewById(R.id.card_frame_lower).setBackgroundColor(Color.WHITE);
                cardView.findViewById(R.id.card_frame_upper).setBackgroundColor(Color.WHITE);



                if( op1 instanceof Operator &  (op2 instanceof Proposition | op2 instanceof Absurdity) ) {
                    //upper
                    Operator upper = (Operator) op1;
                    Expression upper_left = upper.getOperand1();
                    Expression upper_right = upper.getOperand2();

                    //Upper left
                    if( upper_left instanceof Operator ){
                        sDotsSymbol(cardView,R.id.card_image_2);
                    }
                    else{
                        sSymbol( upper_left,cardView,R.id.card_image_2,symbolMap);
                    }

                    //Upper right
                    if( upper_right instanceof Operator ){
                        sDotsSymbol(cardView,R.id.card_image_1);
                    }
                    else{
                        sSymbol( upper_right,cardView,R.id.card_image_1,symbolMap);
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
                    rmView(R.id.card_frame_lower,cardView);
                    rmView(R.id.card_card_2_4,cardView);
                    mParent(R.id.card_card_2_3,cardView);

                    sSymbol(op2,cardView,R.id.card_image_3,symbolMap);
                }

                if( (op1 instanceof Proposition | op1 instanceof Absurdity) & op2 instanceof Operator  ) {

                    //lower
                    Operator lower = (Operator) op2;
                    Expression lower_left = lower.getOperand1();
                    Expression lower_right = lower.getOperand2();

                    //lower left
                    if(lower_left instanceof Operator){
                        sDotsSymbol(cardView,R.id.card_image_3);
                    }
                    else{
                        sSymbol(lower_left,cardView,R.id.card_image_3,symbolMap);
                    }

                    //lower left
                    if(lower_right instanceof Operator){
                        sDotsSymbol(cardView,R.id.card_image_4);
                    }
                    else{
                        sSymbol( lower_right,cardView,R.id.card_image_4,symbolMap);
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
                    rmView(R.id.card_frame_upper,cardView);
                    rmView(R.id.card_card_1_1,cardView);
                    mParent(R.id.card_card_1_2,cardView);
                    sSymbol(op1,cardView,R.id.card_image_2,symbolMap);
                }

                if( op1 instanceof Operator & op2 instanceof Operator ){
                    //lower and upper
                    Operator lower = (Operator) op2;
                    Expression lower_left = lower.getOperand1();
                    Expression lower_right = lower.getOperand2();

                    //lower left
                    if(lower_left instanceof Operator){
                        sDotsSymbol(cardView,R.id.card_image_3);
                    }
                    else{
                        sSymbol( lower_left,cardView,R.id.card_image_3,symbolMap);
                    }

                    //lower left
                    if(lower_right instanceof Operator){
                        sDotsSymbol(cardView,R.id.card_image_4);
                    }
                    else{
                        sSymbol(lower_right,cardView,R.id.card_image_4,symbolMap);
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
                        sDotsSymbol(cardView,R.id.card_image_2);
                    }
                    else{
                        sSymbol( upper_left,cardView,R.id.card_image_2,symbolMap);
                    }

                    //Upper right
                    if( upper_right instanceof Operator ){
                        sDotsSymbol(cardView,R.id.card_image_1);
                    }
                    else{
                        sSymbol( upper_right,cardView,R.id.card_image_1,symbolMap);
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
    private static void sSymbol(Expression expression,CardView cardView,int rId, Map<String,String> symbolMap){
        ImageView imageView = cardView.findViewById(rId);
        String symbol = "";
        if(symbolMap.containsKey(expression.toString())){
            symbol = symbolMap.get(expression.toString());
        }

        switch (symbol.toLowerCase()){
            case "redball":
                Tools.setImage(imageView,R.drawable.redball);
                break;
            case "blueball" :
                Tools.setImage(imageView,R.drawable.blueball);
                break;
            case "greentriangle" :
                Tools.setImage(imageView,R.drawable.greentriangle);
                break;
            case "yellowrectangle":
                Tools.setImage(imageView,R.drawable.yellowrectangle);
                break;
            case "absurdity":
                Tools.setImage(imageView,R.drawable.absurdity);
                break;
            default:
                Tools.setImage(imageView,R.drawable.dots);
                break;

        }
    }
    private static void sSymbol(Expression expression,ImageView imageView, Map<String,String> symbolMap){
        String symbol = "";
        if(symbolMap.containsKey(expression.toString())){
            symbol = symbolMap.get(expression.toString());
        }

        switch (symbol.toLowerCase()){
            case "redball":
                Tools.setImage(imageView,R.drawable.redball);
                break;
            case "blueball" :
                Tools.setImage(imageView,R.drawable.blueball);
                break;
            case "greentriangle" :
                Tools.setImage(imageView,R.drawable.greentriangle);
                break;
            case "yellowrectangle":
                Tools.setImage(imageView,R.drawable.yellowrectangle);
                break;
            case "absurdity":
                Tools.setImage(imageView,R.drawable.absurdity);
                break;
            default:
                Tools.setImage(imageView,R.drawable.dots);
                break;

        }
    }

    private static void sDotsSymbol(CardView cardView,int rId){
        ( (ImageView) cardView.findViewById(rId) ).setImageResource(R.drawable.dots);
    }
}