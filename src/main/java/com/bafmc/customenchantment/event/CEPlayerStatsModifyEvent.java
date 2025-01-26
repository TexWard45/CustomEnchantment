package com.bafmc.customenchantment.event;

import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.player.CEPlayer;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CEPlayerStatsModifyEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private CEPlayer cePlayer;

	private boolean cancel;

	@Getter
    private CustomAttributeType statsType;

	@Getter
    private ModifyType modifyType;

	@Getter
    private double currentValue;

	@Getter
    private double changeValue;
	
	public CEPlayerStatsModifyEvent(CEPlayer cePlayer, CustomAttributeType type, ModifyType modifyType, double currentValue,
									double changeValue) {
		super(true);
		this.cePlayer = cePlayer;
		this.statsType = type;
		this.modifyType = modifyType;
		this.currentValue = currentValue;
		this.changeValue = changeValue;
	}

	public CEPlayerStatsModifyEvent(CEPlayer cePlayer, CustomAttributeType type, ModifyType modifyType, double currentValue,
									double changeValue, boolean async) {
		super(async);
		this.cePlayer = cePlayer;
		this.statsType = type;
		this.modifyType = modifyType;
		this.currentValue = currentValue;
		this.changeValue = changeValue;
	}

	public CEPlayer getCEPlayer() {
		return this.cePlayer;
	}

    public void setValue(double value) {
		this.changeValue = value;
	}

	public double getFinalValue() {
		switch (this.modifyType) {
		case ADD:
			return getCurrentValue() + getChangeValue();
		case REMOVE:
			return getCurrentValue() - getChangeValue();
		case SET:
			return getChangeValue();
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
