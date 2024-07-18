package me.texward.customenchantment.enchant;

import me.texward.customenchantment.player.CEPlayer;

public class EffectData {
	private String taskName;
	private CEPlayer caller;
	private EffectHook effectHook;
	private EffectSettings settings;
	private CEFunctionData data;
	private int tick;
	private boolean remove;

	public EffectData(CEPlayer caller, EffectHook effectHook, CEFunctionData data) {
		this.caller = caller;
		this.effectHook = effectHook;
		this.settings = effectHook.getSettings().clone();
		this.data = data;
	}
	
	public void updateAndExecute() {
		effectHook.updateAndExecute(data);
	}

	public void execute() {
		effectHook.execute(data);
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

	public CEPlayer getCaller() {
		return caller;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public String getTaskName() {
		return taskName;
	}
	
	public boolean hasTaskName() {
		return taskName != null;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
