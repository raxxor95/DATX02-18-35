package com.datx02_18_35.lib.logicmodel;

import com.datx02_18_35.lib.logicmodel.expression.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Jonatan on 2018-02-15.
 */

public class Inventory {
    private Stack<HashSet<Expression>> inventories;
    private int length;
    Inventory(){
        inventories =new Stack<>();
        inventories.push(new HashSet<Expression>());
    }

    public void addExpression(Expression expression) {
        Collection<Expression> col = new ArrayList<>();
        col.add(expression);
        addExpressions(col);
    }

    public void addExpressions(Collection<Expression> expressions){
        ADD_NEXT:
        for (Expression expr : expressions) {
             for (Set<Expression> scopeInventory : inventories) {
                 if (scopeInventory.contains(expr)) {
                     continue ADD_NEXT;
                 }
             }
            inventories.peek().add(expr);
        }
    }
    public void addScope(){
        inventories.add(new HashSet<Expression>());
    }
    public void removeScope(){
        inventories.remove(inventories.size());
    }
    public Stack<HashSet<Expression>> getInventory(){
        return inventories;
    }

}