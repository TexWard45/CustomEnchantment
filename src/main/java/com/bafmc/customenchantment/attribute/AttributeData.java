package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;
import lombok.Getter;

public class AttributeData implements Cloneable {
	public enum Operation {
		ADD_NUMBER(0), MULTIPLY_PERCENTAGE(1), ADD_PERCENTAGE(2), SET_NUMBER(3);

		private int id;

		Operation(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Operation fromId(int id) {
			for (Operation op : values()) {
				if (op.getId() == id) {
					return op;
				}
			}
			throw new IllegalArgumentException("Corrupt operation ID " + id + " detected.");
		}
	}

	@Getter
    private CustomAttributeType type;
	private RandomRange amount;
	@Getter
    private Operation operation;
	@Getter
    private Chance chance;

	public AttributeData(CustomAttributeType type, double amount, Operation operation) {
		this.type = type;
		this.amount = new RandomRange(amount);
		this.operation = operation;
	}

	public AttributeData(CustomAttributeType type, RandomRange amount, Operation operation) {
		this.type = type;
		this.amount = amount;
		this.operation = operation;
	}
	
	public AttributeData(CustomAttributeType type, double amount, Operation operation, Chance chance) {
		this.type = type;
		this.amount = new RandomRange(amount);
		this.operation = operation;
		this.chance = chance.clone();
	}
	
	public AttributeData(CustomAttributeType type, RandomRange amount, Operation operation, Chance chance) {
		this.type = type;
		this.amount = amount;
		this.operation = operation;
		this.chance = chance.clone();
	}

    public double getAmount() {
		return amount.getValue();
	}

    public boolean hasChance() {
		return chance != null;
	}

	public String toString() {
		return "OptionData [type=" + type.getType() + ", amount=" + amount + ", operation=" + operation + "]";
	}

	public AttributeData clone() {
		try {
			return (AttributeData) super.clone();
		} catch (CloneNotSupportedException e) {
			return new AttributeData(type, amount, operation);
		}
	}

}
