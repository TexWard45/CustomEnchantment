package com.bafmc.customenchantment.enchant;

import lombok.Setter;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.ILine;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.bukkit.utils.RandomRangeInt;

@Setter
public class CEEnchantSimple implements ILine {
	private String name;
	private int level;
	private RandomRangeInt success;
	private RandomRangeInt destroy;
    private int xp;

	public CEEnchantSimple(String line) {
		this.fromLine(line);
	}

	public CEEnchantSimple(String name, int level) {
		this.name = name;
		this.level = level;
		this.success = new RandomRangeInt(100);
		this.destroy = new RandomRangeInt(0);
	}

	public CEEnchantSimple(String name, int level, RandomRangeInt success, RandomRangeInt destroy) {
		this.name = name;
		this.level = level;
		this.success = success;
		this.destroy = destroy;
	}

	public CEEnchantSimple(String name, int level, int success, int destroy) {
		this.name = name;
		this.level = Integer.valueOf(level);
		this.success = new RandomRangeInt(success);
		this.destroy = new RandomRangeInt(destroy);
	}

    public CEEnchantSimple(String name, int level, int success, int destroy, int xp) {
        this.name = name;
        this.level = Integer.valueOf(level);
        this.success = new RandomRangeInt(success);
        this.destroy = new RandomRangeInt(destroy);
        this.xp = xp;
    }

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public CEEnchant getCEEnchant() {
		return CustomEnchantment.instance().getCeEnchantMap().get(name);
	}

	public RandomRangeInt getSuccess() {
		return success;
	}

	public RandomRangeInt getDestroy() {
		return destroy;
	}

    public int getXp() {
        return xp;
    }

	public String toLine() {
        if (xp > 0) {
            return name + ":" + level + ":" + success.getValue() + ":" + destroy.getValue() + ":" + xp;
        }

		return name + ":" + level + ":" + success.getValue() + ":" + destroy.getValue();
	}

	public void fromLine(String line) {
		Parameter parameter = new Parameter(line);
		this.name = parameter.getString(0);
		this.level = parameter.getInteger(1, 1);
		this.success = new RandomRangeInt(parameter.getInteger(2, 100));
		this.destroy = new RandomRangeInt(parameter.getInteger(3, 0));

        if (parameter.size() >= 5) {
            this.xp = parameter.getInteger(4, 0);
        }
	}

	public String toString() {
		return "CESimple [name=" + name + ", level=" + level + ", success=" + success + ", destroy=" + destroy + ", xp=" + xp + "]";
	}
}
