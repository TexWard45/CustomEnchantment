package me.texward.customenchantment.enchant;

import java.util.List;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;

public abstract class EffectHook implements Cloneable {
	private EffectSettings settings;

	/**
	 * Update target by settings and execute based on data
	 * 
	 * @param data
	 */
	public void updateAndExecute(CEFunctionData data) {
		Target target = getSettings().getTarget();

		Player enemy = data.getEnemyPlayer();

		boolean removeEnemyEffectWhenDeath = enemy != null && !settings.isEffectAfterDead() && target == Target.ENEMY
				&& CEAPI.getCEPlayer(enemy).getDeathTime() != data.getEnemyDeathTime();
		if (removeEnemyEffectWhenDeath) {
			return;
		}

		CEFunctionData executeData = data;
		if (executeData.getTarget() != target) {
			executeData = data.clone();
			executeData.setTarget(target);
		}

		TargetFilter targetFilter = getSettings().getTargetFilter();
		if (targetFilter.isEnable()) {
			List<Player> targets = targetFilter.getTargetsByPlayer(executeData.getPlayer(),
					executeData.getEnemyPlayer());

			for (Player otherTarget : targets) {
				CEFunctionData otherData = data.clone().setEnemyPlayer(otherTarget);
				updateAndExecuteOther(otherData);
			}
		} else {
			execute(executeData);
		}
	}

	private void updateAndExecuteOther(CEFunctionData data) {
		Target target = getSettings().getTargetOther();

		Player enemy = data.getEnemyPlayer();

		boolean removeEnemyEffectWhenDeath = enemy != null && !settings.isEffectAfterDead() && target == Target.ENEMY
				&& CEAPI.getCEPlayer(enemy).getDeathTime() != data.getEnemyDeathTime();
		if (removeEnemyEffectWhenDeath) {
			return;
		}
		data.setTarget(target);

		execute(data);
	}

	public abstract String getIdentify();

	public abstract void setup(String[] args);

	/**
	 * Execute based on data
	 * 
	 * @param data
	 */
	public abstract void execute(CEFunctionData data);

	public boolean register() {
		return Effect.register(this);
	}

	public EffectHook clone() {
		try {
			return (EffectHook) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public EffectSettings getSettings() {
		return settings;
	}

	public void setSettings(EffectSettings settings) {
		this.settings = settings;
	}

	public boolean isAsync() {
		return true;
	}

	public String get(String[] args, int start) {
		String message = "";
		for (int i = start; i < args.length; i++) {
			if (i == start) {
				message = args[i];
			} else {
				message += ":" + args[i];
			}
		}
		return message;
	}
}
