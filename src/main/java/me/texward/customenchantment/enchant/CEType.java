package me.texward.customenchantment.enchant;

import java.util.concurrent.ConcurrentHashMap;

public class CEType {
	private static final ConcurrentHashMap<String, CEType> map = new ConcurrentHashMap<String, CEType>();
	public static final CEType AUTO = new CEType("AUTO").register();
	public static final CEType ATTACK = new CEType("ATTACK").register();
	public static final CEType FINAL_ATTACK = new CEType("FINAL_ATTACK").register();
	public static final CEType DEFENSE = new CEType("DEFENSE").register();
	public static final CEType UNKNOWN_DEFENSE = new CEType("UNKNOWN_DEFENSE").register();
	public static final CEType HURT = new CEType("HURT").register();
	public static final CEType ARROW_HIT = new CEType("ARROW_HIT").register();
	public static final CEType ARROW_DEFENSE = new CEType("ARROW_DEFENSE").register();
	public static final CEType BOW_SHOOT = new CEType("BOW_SHOOT").register();
	public static final CEType MINING = new CEType("MINING").register();
	public static final CEType HOLD = new CEType("HOLD").register();
	public static final CEType CHANGE_HAND = new CEType("CHANGE_HAND").register();
	public static final CEType KILL_PLAYER = new CEType("KILL_PLAYER").register();
	public static final CEType DEATH = new CEType("DEATH").register();
	public static final CEType ARMOR_EQUIP = new CEType("ARMOR_EQUIP").register();
	public static final CEType ARMOR_UNDRESS = new CEType("ARMOR_UNDRESS").register();
	public static final CEType QUIT = new CEType("QUIT").register();
	public static final CEType JOIN = new CEType("JOIN").register();
	public static final CEType MOVE = new CEType("MOVE").register();
	public static final CEType STATS_CHANGE = new CEType("STATS_CHANGE").register();
	public static final CEType ITEM_CONSUME = new CEType("ITEM_CONSUME").register();
	private String type;

	public CEType(String type) {
		this.type = type.toUpperCase();
	}

	public String getType() {
		return type;
	}

	public CEType register() {
		if (map.containsKey(this.type)) {
			throw new IllegalArgumentException("Already exist this type!");
		}
		map.put(this.type, this);
		return this;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CEType) {
			return ((CEType) obj).getType().equals(getType());
		}
		return false;
	}

	public static CEType valueOf(String name) {
		return map.get(name);
	}

	public static CEType[] values() {
		return map.values().toArray(new CEType[map.size()]);
	}

	public String toString() {
		return type;
	}
}