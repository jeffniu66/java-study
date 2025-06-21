package com.jeffrey.listener.game.task;

public class KillMonsterEvent implements GameEvent {

    private final String monsterId;

    public KillMonsterEvent(String monsterId) {
        this.monsterId = monsterId;
    }

    public String getMonsterId() {
        return monsterId;
    }

    @Override
    public String getEventType() {
        return "KillMonster";
    }
}
