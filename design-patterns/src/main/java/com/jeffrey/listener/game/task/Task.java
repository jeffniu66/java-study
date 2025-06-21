package com.jeffrey.listener.game.task;

public abstract class Task implements GameEventListener {
    protected boolean completed = false;

    public boolean isCompleted() {
        return completed;
    }

    public abstract void onEvent(GameEvent event);
}
