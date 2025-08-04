package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.player.CEPlayer;
import lombok.Getter;
import lombok.Setter;

public class EffectData {
	@Setter
    @Getter
    private String taskName;
	@Getter
    private CEPlayer caller;
	private EffectHook effectHook;
	private EffectSettings settings;
	private CEFunctionData data;
	private int tick;
	@Getter
    @Setter
    private boolean remove;
	@Getter
	@Setter
	private boolean running;

	public EffectData(CEPlayer caller, EffectHook effectHook, CEFunctionData data) {
		this.caller = caller;
		this.effectHook = effectHook;
		this.settings = effectHook.getSettings().clone();
		this.data = data;
	}
	
	public void updateAndExecute() {
		this.effectHook.updateAndExecute(data);
		this.running = true;
	}

    public boolean isDifferentEnemyDeadSession() {
        return effectHook.isDifferentEnemyDeadSession(data);
    }

    public boolean isForceEffectOnEnemyDead() {
        return effectHook.isForceEffectOnEnemyDead();
    }

	public void tick() {
		tick++;
	}

	public String getName() {
		return settings.getName();
	}

	public boolean hasDelay() {
		return settings.getDelay() > 0;
	}

	public boolean hasPeriod() {
		return settings.getPeriod() > 0;
	}

	public boolean hasScheduler() {
		return hasDelay() || hasPeriod();
	}

	public boolean canActiveDelay() {
		return tick >= effectHook.getSettings().getDelay();
	}

	public boolean canActivePeriod() {
		return hasPeriod() && tick % settings.getPeriod() == 0;
	}

    public boolean hasTaskName() {
		return taskName != null;
	}

}
