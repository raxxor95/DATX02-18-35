package com.datx02_18_35.controller.dispatch.actions;

import com.datx02_18_35.controller.dispatch.ActionConsumer;

/**
 * Created by robin on 2018-03-01.
 */

public abstract class Action {
    private ActionConsumer callback;

    /**
     * Use this super-constructor to register a callback.
     * @param callback
     */
    protected Action(ActionConsumer callback) {
        this.callback = callback;
    }

    protected Action() {
        this.callback = null;
    }

    /**
     * Only call this function if the extending class registers a callback by calling super(callback) in its constructor
     * @param action
     * @throws InterruptedException
     */
    public void callback(Action action) throws InterruptedException {
        assert callback != null;
        this.callback.sendAction(action);
    }
}