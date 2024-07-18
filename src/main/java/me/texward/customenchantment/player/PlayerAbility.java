package me.texward.customenchantment.player;

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
			map.put(type, new CancelManager());
		}
	}

	public void onQuit() {

	}

	public void setCancel(Type type, String unique, boolean cancel) {
		map.get(type).setCancel(unique, cancel);
	}

	public boolean isCancel(Type type) {
		return map.get(type).isCancel();
	}
}
