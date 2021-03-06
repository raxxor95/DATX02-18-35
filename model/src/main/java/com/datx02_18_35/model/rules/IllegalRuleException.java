package com.datx02_18_35.model.rules;

import com.datx02_18_35.model.game.IllegalGameStateException;

/**
 * Created by robin on 2018-03-14.
 */

public class IllegalRuleException extends IllegalGameStateException {
    public final Rule rule;
    public IllegalRuleException(Rule rule, String message) {
        super("[" + rule.toString() + "]: " + message);
        this.rule = rule;
    }
}
