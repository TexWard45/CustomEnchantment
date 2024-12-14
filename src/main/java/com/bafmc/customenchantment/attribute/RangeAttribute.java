package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;
import lombok.Getter;

public class RangeAttribute extends NMSAttribute implements Cloneable {
	private RandomRange amount;
	@Getter
    private Chance chance;

	public RangeAttribute(NMSAttributeType type, double amount, NMSAttributeOperation operation) {
        super();
		this.setAttributeType(type);
		this.setAmount(amount);
		this.setOperation(operation);
	}

	public RangeAttribute(NMSAttributeType type, RandomRange amount, NMSAttributeOperation operation) {
		super();
		this.setAttributeType(type);
		this.setOperation(operation);
		this.amount = amount;
	}
	
	public RangeAttribute(NMSAttributeType type, RandomRange amount, NMSAttributeOperation operation, Chance chance) {
		super();
		this.setAttributeType(type);
		this.setOperation(operation);
		this.amount = amount;
		this.chance = chance.clone();
	}

    public double getAmount() {
		if (amount != null) {
			return amount.getValue();
		}else {
			return super.getAmount();
		}
	}

    public boolean hasChance() {
		return chance != null;
	}

	public RangeAttribute clone() {
		RangeAttribute rangeAttribute = (RangeAttribute) super.clone();
		rangeAttribute.amount = amount.clone();
		rangeAttribute.chance = chance.clone();
		return rangeAttribute;
	}
}
