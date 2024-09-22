package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;

public class AttributeData implements Cloneable {
	public enum Operation {
		ADD_NUMBER(0), MULTIPLY_PERCENTAGE(1), ADD_PERCENTAGE(2), SET_NUMBER(3);

		private int id;

		private Operation(int id) {
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

	private String type;
	private RandomRange amount;
	private Operation operation;
	private Chance chance;

	public AttributeData(String type, double amount, Operation operation) {
		this.type = type != null ? type.toUpperCase() : null;
		this.amount = new RandomRange(amount);
		this.operation = operation;
	}

	public AttributeData(String type, RandomRange amount, Operation operation) {
		this.type = type != null ? type.toUpperCase() : null;
		this.amount = amount;
		this.operation = operation;
	}
	
	public AttributeData(String type, double amount, Operation operation, Chance chance) {
		this.type = type != null ? type.toUpperCase() : null;
		this.amount = new RandomRange(amount);
		this.operation = operation;
		this.chance = chance.clone();
	}
	
	public AttributeData(String type, RandomRange amount, Operation operation, Chance chance) {
		this.type = type != null ? type.toUpperCase() : null;
		this.amount = amount;
		this.operation = operation;
		this.chance = chance.clone();
	}

	public String getType() {
		return type;
	}

	public double getAmount() {
		return amount.getValue();
	}

	public RandomRange getAmountRange() {
		return amount;
	}
	
	public Operation getOperation() {
		return operation;
	}

	public Chance getChance() {
		return chance;
	}

	public boolean hasChance() {
		return chance != null;
	}

	public String toString() {
		return "OptionData [type=" + type + ", amount=" + amount + ", operation=" + operation + "]";
	}

	public AttributeData clone() {
		try {
			return (AttributeData) super.clone();
		} catch (CloneNotSupportedException e) {
			return new AttributeData(type, amount, operation);
		}
	}

}
