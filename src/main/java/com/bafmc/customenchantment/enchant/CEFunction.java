package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.EquipSlot;

public class CEFunction {
	private String name;
	private CEType ceType;
	private Chance chance;
	private Cooldown cooldown;
	private List<EquipSlot> chanceSlot;
	private List<EquipSlot> cooldownSlot;
	private List<EquipSlot> activeSlot;
	private TargetFilter targetFilter;
	private Condition targetCondition;
	private Option targetOption;
	private Effect targetEffect;
	private Condition condition;
	private Option option;
	private Effect effect;
	private boolean effectNow;
	private boolean trueConditionBreak;
	private boolean falseConditionBreak;
	private boolean timeoutCooldownBreak;
	private boolean inCooldownBreak;
	private boolean trueChanceBreak;
	private boolean falseChanceBreak;

	public CEFunction(String name, CEType ceType, Chance chance, Cooldown cooldown, List<EquipSlot> chanceSlot,
			List<EquipSlot> cooldownSlot, List<EquipSlot> activeSlot, TargetFilter targetFilter, Condition targetCondition, Option targetOption,
			Effect targetEffect, Condition condition, Option option, Effect effect, boolean effectNow,
			boolean trueChanceBreak, boolean falseChanceBreak, boolean timeoutCooldownBreak, boolean inCooldownBreak,
			boolean trueConditionBreak, boolean falseConditionBreak) {
		this.name = name;
		this.ceType = ceType;
		this.chance = chance;
		this.cooldown = cooldown;
		this.chanceSlot = chanceSlot;
		this.cooldownSlot = cooldownSlot;
		this.activeSlot = activeSlot;
		this.targetFilter = targetFilter;
		this.targetCondition = targetCondition;
		this.targetOption = targetOption;
		this.targetEffect = targetEffect;
		this.condition = condition;
		this.option = option;
		this.effect = effect;
		this.effectNow = effectNow;
		this.trueChanceBreak = trueChanceBreak;
		this.falseChanceBreak = falseChanceBreak;
		this.timeoutCooldownBreak = timeoutCooldownBreak;
		this.inCooldownBreak = inCooldownBreak;
		this.trueConditionBreak = trueConditionBreak;
		this.falseConditionBreak = falseConditionBreak;
	}

	public String getName() {
		return name;
	}

	public CEType getCEType() {
		return ceType;
	}

	public Chance getChance() {
		return chance;
	}

	public Cooldown getCooldown() {
		return cooldown;
	}

	public List<EquipSlot> getChanceSlot() {
		return new ArrayList<EquipSlot>(chanceSlot);
	}

	public List<EquipSlot> getCooldownSlot() {
		return new ArrayList<EquipSlot>(cooldownSlot);
	}
	
	public List<EquipSlot> getActiveSlot() {
		return new ArrayList<EquipSlot>(activeSlot);
	}

	public TargetFilter getTargetFilter() {
		return targetFilter;
	}

	public Option getTargetOption() {
		return targetOption;
	}

	public Condition getTargetCondition() {
		return targetCondition;
	}

	public Effect getTargetEffect() {
		return targetEffect;
	}

	public Condition getCondition() {
		return condition;
	}

	public Option getOption() {
		return option;
	}

	public Effect getEffect() {
		return effect;
	}

	public boolean isEffectNow() {
		return effectNow;
	}

	public boolean isTrueChanceBreak() {
		return trueChanceBreak;
	}

	public boolean isFalseChanceBreak() {
		return falseChanceBreak;
	}

	public boolean isTimeoutCooldownBreak() {
		return timeoutCooldownBreak;
	}

	public boolean isInCooldownBreak() {
		return inCooldownBreak;
	}

	public boolean isTrueConditionBreak() {
		return trueConditionBreak;
	}

	public boolean isFalseConditionBreak() {
		return falseConditionBreak;
	}

}