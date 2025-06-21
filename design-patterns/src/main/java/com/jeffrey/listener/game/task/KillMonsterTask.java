package com.jeffrey.listener.game.task;

public class KillMonsterTask extends Task {
    private final String targetMonsterId;
    private final int requiredCount;

    private int currentCount = 0;

    public KillMonsterTask(String targetMonsterId, int requiredCount) {
        this.targetMonsterId = targetMonsterId;
        this.requiredCount = requiredCount;
    }


    @Override
    public void onEvent(GameEvent event) {
        if (event instanceof KillMonsterEvent) {
            KillMonsterEvent e = (KillMonsterEvent) event;
            if (e.getMonsterId().equals(targetMonsterId)) {
                currentCount++;
                System.out.println("已击杀：" + currentCount + "/" + requiredCount);
                if (currentCount >= requiredCount) {
                    completed = true;
                    System.out.println("任务完成：击杀 " + targetMonsterId);
                }
            }
        }
    }
}
