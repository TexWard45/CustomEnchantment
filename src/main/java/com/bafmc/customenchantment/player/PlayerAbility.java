package com.bafmc.customenchantment.player;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerAbility extends CEPlayerExpansion {
	public enum Type {
		ATTACK, MOVE, JUMP, LOOK;
	}

	private ConcurrentHashMap<Type, CancelManager> map = new ConcurrentHashMap<Type, CancelManager>();

	public PlayerAbility(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
		for (Type type : Type.values()) {
			map.put(type, new CancelManager(this));
		}
	}

	public void onQuit() {

	}

	public void setCancel(Type type, String unique, boolean cancel, long duration) {
        CancelManager cancelManager = map.get(type);
        if (cancelManager == null) {
            return;
        }

        cancelManager.setCancel(unique, cancel, duration);
	}

	public boolean isCancel(Type type) {
        CancelManager cancelManager = map.get(type);
        if (cancelManager == null) {
            return false;
        }

        return cancelManager.isCancel();
	}
}
