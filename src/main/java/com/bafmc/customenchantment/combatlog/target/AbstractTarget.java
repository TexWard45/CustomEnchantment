package com.bafmc.customenchantment.combatlog.target;

public abstract class AbstractTarget<T> {
    private T target;
    public AbstractTarget(T target) {
        this.target = target;
    }

    public abstract String getType();

    public abstract String getName();

    public T getTarget() {
        return target;
    }
}
