package com.datx02_18_35.model.expression;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.List;

/**
 * Created by robin on 2018-02-14.
 */

public class Rule {
    public final RuleType type;
    public final List<Expression> expressions;


    public Rule(RuleType type, List<Expression> expressions) {
        assert expressions != null;
        this.type = type;
        this.expressions = expressions;
    }

    public static Collection<Rule> getLegalRules( Expression assumption, Collection<Expression> expressions){

        List<Expression> exprs = new ArrayList<>(expressions);
        ArrayList<Rule> legalRules = new ArrayList<>();
        switch(exprs.size()) {
            case 0:
                //legalRules.add(new Rule(RuleType.LAW_OF_EXCLUDED_MIDDLE,exprs));

            case 1: {
                if (assumption != null) {
                    List<Expression> assumptionAndExpr = new ArrayList<>();
                    assumptionAndExpr.add(assumption);
                    assumptionAndExpr.addAll(exprs);
                    legalRules.add(new Rule(RuleType.IMPLICATION_INTRODUCTION, assumptionAndExpr));
                }

                if (exprs.get(0) instanceof Conjunction) {
                    List<Expression> conjunctionExpression = new ArrayList<>(exprs);
                    legalRules.add(new Rule(RuleType.CONJUNCTION_ELIMINATION, conjunctionExpression));

                } else if (exprs.get(0) instanceof Absurdity) {
                    List<Expression> absurdityExpressions = new ArrayList<>(exprs);
                    absurdityExpressions.add(null);
                    legalRules.add(new Rule(RuleType.ABSURDITY_ELIMINATION, absurdityExpressions));
                    List<Expression> reversedAbsurdityExpressions = new ArrayList<>(absurdityExpressions);
                    Collections.reverse(reversedAbsurdityExpressions);
                    legalRules.add(new Rule(RuleType.ABSURDITY_ELIMINATION,reversedAbsurdityExpressions));
                }

                List<Expression> disjunctionExpressions = new ArrayList<>(exprs);
                disjunctionExpressions.add(null);
                legalRules.add(new Rule(RuleType.DISJUNCTION_INTRODUCTION, disjunctionExpressions));
                List<Expression> reversedDisjunctionExpressions = new ArrayList<>(disjunctionExpressions);
                Collections.reverse(reversedDisjunctionExpressions);
                legalRules.add(new Rule(RuleType.DISJUNCTION_INTRODUCTION, reversedDisjunctionExpressions));
                break;
            }
            case 2:

                List<Expression> conjunctionExpressions= new ArrayList<>(exprs);
                legalRules.add(new Rule(RuleType.CONJUNCTION_INTRODUCTION, conjunctionExpressions));
                List<Expression> reverseConjunctionExpressions = new ArrayList<>(conjunctionExpressions);
                Collections.reverse(reverseConjunctionExpressions);
                legalRules.add(new Rule(RuleType.CONJUNCTION_INTRODUCTION, reverseConjunctionExpressions));

                if (exprs.get(0) instanceof Implication && ((Implication) exprs.get(0)).operand1.equals(exprs.get(1))) {
                    List<Expression> implicationExpressions = new ArrayList<>(exprs);
                    legalRules.add(new Rule(RuleType.IMPLICATION_ELIMINATION, implicationExpressions));

                } else if (exprs.get(1) instanceof Implication && ((Implication) exprs.get(1)).operand1.equals(exprs.get(0))) {
                    List<Expression> reverseImplicationExpressions = new ArrayList<>(exprs);
                    Collections.reverse(reverseImplicationExpressions);
                    legalRules.add(new Rule(RuleType.IMPLICATION_ELIMINATION, reverseImplicationExpressions));
                }
                /*
                if( exprs.get(0) instanceof Negation && ((Negation) exprs.get(0)).operand.equals(exprs.get(1))){
                    legalRules.add(new Rule(RuleType.ABSURDITY_INTRODUCTION,exprs));
                }else if (exprs.get(1) instanceof Negation && ((Negation) exprs.get(1)).operand.equals(exprs.get(0))){
                    legalRules.add(new Rule(RuleType.ABSURDITY_INTRODUCTION,reverseExprs));
                }*/
                break;

            case 3:
                Rule disjElimRule;

                if (exprs.get(0) instanceof Disjunction && exprs.get(1) instanceof Implication && exprs.get(2) instanceof Implication) {
                    // First (0) element is disjunction.
                    disjElimRule = createDisjunctionElimination(((Disjunction) exprs.get(0)), (Implication) exprs.get(1), (Implication) exprs.get(2));
                } else if (exprs.get(0) instanceof Implication && exprs.get(1) instanceof Disjunction && exprs.get(2) instanceof Implication) {
                    // Second (1) element is disjunction.
                    disjElimRule = createDisjunctionElimination(((Disjunction) exprs.get(1)), (Implication) exprs.get(0), (Implication) exprs.get(2));

                } else if (exprs.get(0) instanceof Implication && exprs.get(1) instanceof Implication && exprs.get(2) instanceof Disjunction) {
                    //Third (2) element is disjunction.
                    disjElimRule = createDisjunctionElimination(((Disjunction) exprs.get(2)), (Implication) exprs.get(0), (Implication) exprs.get(1));
                } else {
                    break;
                }
                if (disjElimRule != null) {
                    legalRules.add(disjElimRule);
                }
                break;
            default:
                break;
        }
        return legalRules;

    }

    //Help function to create a DisjunctionElimination rule with the order of the expressions: A or B, A>C, B>C
    private static Rule createDisjunctionElimination(Disjunction disj, Implication impl1, Implication impl2){
        ArrayList<Expression> exprsOrder = new ArrayList<Expression>();
        if(impl1.operand2.equals(impl2.operand2)) {
            if (disj.operand1.equals(impl1.operand1) && disj.operand2.equals(impl2.operand1)) {
                //Already in the correct order.
                exprsOrder.add(disj);
                exprsOrder.add(impl1);
                exprsOrder.add(impl2);

            }else if(disj.operand2.equals(impl1.operand1) && disj.operand1.equals(impl2.operand1)){
                //Change order of implications.
                exprsOrder.add(disj);
                exprsOrder.add(impl2);
                exprsOrder.add(impl1);

            }else{
                // Expressions don't match. Return null.
                return null;
            }
        }else{
            //Expressions don't match. Return null.
            return  null;
        }
        return new Rule(RuleType.DISJUNCTION_ELIMINATION,exprsOrder);
    }
    public static Rule finishIncompleteRule(Rule rule,Expression expression){
        List<Expression> expressions = new ArrayList<>();
        if(rule.type==RuleType.ABSURDITY_ELIMINATION||rule.type==RuleType.DISJUNCTION_INTRODUCTION){
            if(rule.expressions.size()==1){
                expressions.addAll(rule.expressions);
                expressions.add(expression);
                Rule newRule = new Rule(rule.type, expressions);
                return newRule;
            }
        }
        throw new IllegalArgumentException("Either RuleType or Expressions of rule is invalid as argument");
    }
}