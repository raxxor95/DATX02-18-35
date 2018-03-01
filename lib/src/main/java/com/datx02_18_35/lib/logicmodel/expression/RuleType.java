package com.datx02_18_35.lib.logicmodel.expression;

/**
 * Created by robin on 2018-02-14.
 */

public enum RuleType {
    IMPLICATION_ELIMINATION,
    IMPLICATION_INTRODUCTION, //special case in view
    CONJUNCTION_ELIMINATION,
    CONJUNCTION_INTRODUCTION,
    DISJUNCTION_ELIMINATION,
    DISJUNCTION_INTRODUCTION, //special case in view
    ABSURDITY_ELIMINATION, //special case in view
}
