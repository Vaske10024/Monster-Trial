package com.monstertrials.model;

public class Potion {
    private String id;
    private String name;
    private int cost;
    private String effect;
    private String effectDescription;
    private boolean oneUsePerBattle;
    private boolean consumesAction;

    public Potion() {
    }

    public Potion(String id, String name, int cost, String effect, String effectDescription, boolean oneUsePerBattle, boolean consumesAction) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.effect = effect;
        this.effectDescription = effectDescription;
        this.oneUsePerBattle = oneUsePerBattle;
        this.consumesAction = consumesAction;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = Math.max(0, cost); }
    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }
    public String getEffectDescription() { return effectDescription; }
    public void setEffectDescription(String effectDescription) { this.effectDescription = effectDescription; }
    public boolean isOneUsePerBattle() { return oneUsePerBattle; }
    public void setOneUsePerBattle(boolean oneUsePerBattle) { this.oneUsePerBattle = oneUsePerBattle; }
    public boolean isConsumesAction() { return consumesAction; }
    public void setConsumesAction(boolean consumesAction) { this.consumesAction = consumesAction; }
}
