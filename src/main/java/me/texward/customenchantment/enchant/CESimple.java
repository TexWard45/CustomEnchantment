package me.texward.customenchantment.enchant;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.ILine;
import me.texward.customenchantment.api.Parameter;
import me.texward.texwardlib.util.RandomRangeInt;

public class CESimple implements ILine {
	private String name;
	private int level;
	private RandomRangeInt success;
	private RandomRangeInt destroy;

	public CESimple(String line) {
		this.fromLine(line);
	}

	public CESimple(String name, int level) {
		this.name = name;
		this.level = level;
		this.success = new RandomRangeInt(100);
		this.destroy = new RandomRangeInt(0);
	}

	public CESimple(String name, int level, RandomRangeInt success, RandomRangeInt destroy) {
		this.name = name;
		this.level = level;
		this.success = success;
		this.destroy = destroy;
	}

	public CESimple(String name, int level, int success, int destroy) {
		this.name = name;
		this.level = Integer.valueOf(level);
		this.success = new RandomRangeInt(success);
		this.destroy = new RandomRangeInt(destroy);
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public CEEnchant getCEEnchant() {
		return CustomEnchantment.instance().getCEEnchantMap().get(name);
	}

	public RandomRangeInt getSuccess() {
		return success;
	}

	public RandomRangeInt getDestroy() {
		return destroy;
	}

	public String toLine() {
		return name + ":" + level + ":" + success.getValue() + ":" + destroy.getValue();
	}

	public void fromLine(String line) {
		Parameter parameter = new Parameter(line);
		this.name = parameter.getString(0);
		this.level = parameter.getInteger(1, 1);
		this.success = new RandomRangeInt(parameter.getInteger(2, 100));
		this.destroy = new RandomRangeInt(parameter.getInteger(3, 0));
	}

	public String toString() {
		return "CESimple [name=" + name + ", level=" + level + ", success=" + success + ", destroy=" + destroy + "]";
	}
}
