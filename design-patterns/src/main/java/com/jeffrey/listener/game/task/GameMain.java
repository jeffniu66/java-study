package com.jeffrey.listener.game.task;

public class GameMain {

    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        Player player = new Player();

        Task killSlimeTask = new KillMonsterTask("slime", 3);
        player.acceptTask(killSlimeTask, eventManager);

        // 模拟击杀怪物事件
        player.triggerEvent(new KillMonsterEvent("slime"), eventManager);
        player.triggerEvent(new KillMonsterEvent("slime"), eventManager);
        player.triggerEvent(new KillMonsterEvent("slime"), eventManager);
        player.triggerEvent(new KillMonsterEvent("goblin"), eventManager); // 无效
    }
}
