package com.jeffrey.listener.game.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private final Map<String, List<GameEventListener>> listeners = new HashMap<>();

    public void register(String eventType, GameEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unregister(String eventType, GameEventListener listener) {
        List<GameEventListener> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    public void notify(GameEvent event) {
        List<GameEventListener> list = listeners.get(event.getEventType());
        if (list != null) {
            for (GameEventListener listener : new ArrayList<>(list)) {
                listener.onEvent(event);
            }
        }
    }
}
