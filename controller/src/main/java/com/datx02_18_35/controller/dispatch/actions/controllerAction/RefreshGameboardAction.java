package com.datx02_18_35.controller.dispatch.actions.controllerAction;

import com.datx02_18_35.controller.dispatch.actions.Action;
import com.datx02_18_35.model.expression.Expression;

/**
 * Created by raxxor on 2018-03-07.
 */


/*
Model requests view to update gameboard Expressions
*/
public class RefreshGameboardAction extends Action {
    public final Iterable<Expression> boardExpressions;
    public final Expression assumptionExpression;

    public RefreshGameboardAction(Iterable<Expression> boardExpressions, Expression assumption){
        if (boardExpressions == null) {
            throw new IllegalArgumentException("boardExpressions can't be null");
        }
        this.boardExpressions=boardExpressions;
        this.assumptionExpression = assumption;
    }
}
