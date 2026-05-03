package com.monstertrials.model;

public class StatusEffect {
    private String id;
    private String name;
    private int remainingTurns;
    private int stacks = 1;

    public StatusEffect() {
    }

    public StatusEffect(String id, String name, int remainingTurns) {
        this(id, name, remainingTurns, 1);
    }

    public StatusEffect(String id, String name, int remainingTurns, int stacks) {
        this.id = id;
        this.name = name;
        this.remainingTurns = remainingTurns;
        this.stacks = Math.max(1, stacks);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getRemainingTurns() { return remainingTurns; }
    public void setRemainingTurns(int remainingTurns) { this.remainingTurns = remainingTurns; }
    public int getStacks() { return stacks; }
    public void setStacks(int stacks) { this.stacks = Math.max(1, stacks); }
    public void incrementStacks(int maxStacks) { this.stacks = Math.min(maxStacks, this.stacks + 1); }
    public void decrement() { remainingTurns = Math.max(0, remainingTurns - 1); }
}
