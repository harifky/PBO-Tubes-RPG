package com.elemental.model;

public class BattleAction {
    private Character actor;
    private Character target;
    private ActionType actionType;
    private Skill skill;
    private Item item;

    public BattleAction(Character actor, ActionType actionType) {
        this.actor = actor;
        this.actionType = actionType;
    }

    // Getters and Setters
    public Character getActor() {
        return actor;
    }

    public void setActor(Character actor) {
        this.actor = actor;
    }

    public Character getTarget() {
        return target;
    }

    public void setTarget(Character target) {
        this.target = target;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
