package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class EffectHook implements Cloneable {
	private EffectSettings settings;

	/**
	 * Update target by settings and execute based on data
	 * 
	 * @param data
	 */
	public void updateAndExecute(CEFunctionData data) {
		Target target = getSettings().getTarget();

		boolean removeEnemyEffectWhenDeath = !isForceEffectOnEnemyDead() && !settings.isEffectAfterDead() && isDifferentEnemyDeadSession(data);
		if (removeEnemyEffectWhenDeath) {
			return;
		}

        if (data.isFakeSource() && !settings.isEffectOnFakeSource()) {
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
			executeWithBlacklistCheck(executeData);
		}
	}

	private void updateAndExecuteOther(CEFunctionData data) {
		Target target = getSettings().getTargetOther();

		boolean removeEnemyEffectWhenDeath = !isForceEffectOnEnemyDead() && !settings.isEffectAfterDead() && isDifferentEnemyDeadSession(data);
		if (removeEnemyEffectWhenDeath) {
			return;
		}
		data.setTarget(target);

		executeWithBlacklistCheck(data);
	}

    public boolean isDifferentEnemyDeadSession(CEFunctionData data) {
        Player enemy = data.getEnemyPlayer();
        return enemy != null && getSettings().getTarget() == Target.ENEMY
                && CEAPI.getCEPlayer(enemy).getDeathTime() != data.getEnemyDeathTime();
    }

	public void executeWithBlacklistCheck(CEFunctionData data) {
		if (CustomEnchantment.instance().getMainConfig().getEffectTypeBlacklist().contains(getIdentify())) {
			return;
		}

		execute(data);
	}

    public boolean isForceEffectOnEnemyDead() {
        return false;
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
