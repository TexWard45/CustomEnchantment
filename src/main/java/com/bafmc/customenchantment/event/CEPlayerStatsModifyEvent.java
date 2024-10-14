package com.bafmc.customenchantment.event;

import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CEPlayerStatsModifyEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private CEPlayer cePlayer;

	private boolean cancel;

	private CustomAttributeType type;

	private ModifyType modifyType;

	private double defaultValue;

	private double currentValue;
	
	public CEPlayerStatsModifyEvent(CEPlayer cePlayer, CustomAttributeType type, ModifyType modifyType, double defaultValue,
									double changeValue) {
		super(true);
		this.cePlayer = cePlayer;
		this.type = type;
		this.modifyType = modifyType;
		this.defaultValue = defaultValue;
		this.currentValue = changeValue;
	}

	public CEPlayerStatsModifyEvent(CEPlayer cePlayer, CustomAttributeType type, ModifyType modifyType, double defaultValue,
									double changeValue, boolean async) {
		super(async);
		this.cePlayer = cePlayer;
		this.type = type;
		this.modifyType = modifyType;
		this.defaultValue = defaultValue;
		this.currentValue = changeValue;
	}

	public CEPlayer getCEPlayer() {
		return this.cePlayer;
	}

	public CustomAttributeType getStatsType() {
		return this.type;
	}

	public ModifyType getModifyType() {
		return this.modifyType;
	}

	public double getDefaultValue() {
		return this.defaultValue;
	}

	public double getCurrentValue() {
		return this.currentValue;
	}

	public void setValue(double value) {
		this.currentValue = value;
	}

	public double getFinalValue() {
		switch (this.modifyType) {
		case ADD:
			return getDefaultValue() + getCurrentValue();
		case REMOVE:
			return getDefaultValue() - getCurrentValue();
		case SET:
			return getCurrentValue();
		}
		return 0d;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() {
		return this.cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
