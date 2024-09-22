package com.bafmc.customenchantment.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.scheduler.BukkitRunnable;

import com.bafmc.customenchantment.enchant.EffectData;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectExecuteTask extends BukkitRunnable {
	private int maxProcessPerTick = 500;
	private ConcurrentHashMap<String, EffectData> effectSchedulerMap = new ConcurrentHashMap<String, EffectData>();
	private List<EffectData> list = new ArrayList<EffectData>();
	private boolean async;

	public EffectExecuteTask(boolean async) {
		this.async = async;
	}

	public void run() {
		if (list.isEmpty()) {
			return;
		}

		for (int i = 0, j = 0; j < maxProcessPerTick && i < list.size(); j++) {
			EffectData effectData = list.get(i);
			
			if (effectData == null) {
				list.remove(i);
				continue;
			}

			if (!isLastEffectData(effectData)) {
				list.remove(i);
				continue;
			}

			boolean hasDelay = effectData.hasDelay();
			boolean hasPeriod = effectData.hasPeriod();
			boolean instant = !hasDelay && !hasPeriod;
			boolean onlyDelay = hasDelay && !hasPeriod;
			boolean onlyPeriod = !hasDelay && hasPeriod;
			boolean delayAndPeriod = hasDelay && hasPeriod;
			boolean canActiveDelay = effectData.canActiveDelay();
			boolean canActivePeriod = effectData.canActivePeriod();
			boolean execute = instant || (onlyDelay && canActiveDelay) || (onlyPeriod && canActivePeriod)
					|| (delayAndPeriod && canActiveDelay && canActivePeriod);
            boolean forceExecute = false;

            if (effectData.isForceEffectOnEnemyDead() && effectData.isDifferentEnemyDeadSession()) {
                try {
                    effectData.updateAndExecute();
                    forceExecute = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    list.remove(i);
                    continue;
                }
            }else if (execute && !effectData.isRemove()) {
				try {
					effectData.updateAndExecute();
				} catch (Exception e) {
					e.printStackTrace();
					list.remove(i);
					continue;
				}
			}

			boolean executeOnce = forceExecute || instant || (onlyDelay && canActiveDelay);
			if (executeOnce) {
				list.remove(i);
			} else {
				effectData.tick();
				i++;
			}
		}
	}

	public boolean isLastEffectData(EffectData effectData) {
		if (!effectData.hasTaskName()) {
			return true;
		}

		String taskName = effectData.getTaskName();
		EffectData lastEffectData = effectSchedulerMap.get(taskName);
		return effectData == lastEffectData;
	}

	public void addEffectDataList(List<EffectData> list) {
		for (EffectData effectData : list) {
			if (effectData.hasScheduler()) {
				String taskName = getEffectSchedulerName(effectData);
				effectData.setTaskName(taskName);
				effectSchedulerMap.put(taskName, effectData);
			}
			this.list.add(effectData);
		}
	}

	public void removeEffectData(CEPlayer caller, String name) {
		if (caller == null) {
			return;
		}

		removeEffectData(caller.getPlayer().getName(), name);
	}

	public void removeEffectData(String prefix, String name) {
		if (prefix != null) {
			removeEffectData(getEffectSchedulerName(prefix, name));
		} else {
			removeEffectData(name);
		}
	}

	public void removeEffectData(String name) {
		EffectData data = effectSchedulerMap.get(name);
		if (data != null) {
			data.setRemove(true);
			effectSchedulerMap.remove(name);
		}
	}

	public EffectData getEffectData(String name) {
		return effectSchedulerMap.get(name);
	}

	public String getEffectSchedulerName(EffectData effectData) {
		return getEffectSchedulerName(effectData.getCaller().getPlayer().getName(), effectData.getName());
	}

	public String getEffectSchedulerName(String prefix, String name) {
		if (name != null) {
			if (prefix != null) {
				return prefix + "-" + name;
			} else {
				return name;
			}
		} else {
			return String.valueOf(System.nanoTime());
		}
	}

	public boolean isAsync() {
		return async;
	}
}
