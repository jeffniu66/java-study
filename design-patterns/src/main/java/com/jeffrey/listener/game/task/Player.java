package com.jeffrey.listener.game.task;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Task> tasks = new ArrayList<>();

    public void acceptTask(Task task, EventManager eventManager) {
        tasks.add(task);
        eventManager.register("KillMonster", task);
    }

    public void triggerEvent(GameEvent event, EventManager eventManager) {
        eventManager.notify(event);
    }
}
