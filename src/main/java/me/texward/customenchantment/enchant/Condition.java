package me.texward.customenchantment.enchant;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

public class Condition {
	private static HashMap<String, ConditionHook> hookMap;
	private List<ConditionOR> conditions;

	static {
		hookMap = new HashMap<String, ConditionHook>();
	}

	public Condition(List<ConditionOR> conditions) {
		this.conditions = conditions;
	}

	public boolean check(Player player) {
		return check(new CEFunctionData(player));
	}

	/**
	 * Check if all conditions are passed
	 * 
	 * @param conditionOR
	 * @param data
	 * @param map
	 * @return true if all are matched, false if not
	 */
	public boolean check(CEFunctionData data) {
		if (data == null) {
			return false;
		}

		for (ConditionOR conditionOR : conditions) {
			boolean check = check(conditionOR, data);

			if (!check) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check if any condition is passed
	 * 
	 * @param conditionOR
	 * @param data
	 * @param map
	 * @return true if any is matched, false if not
	 */
	private boolean check(ConditionOR conditionOR, CEFunctionData data) {
		for (ConditionHook conditionHook : conditionOR.getConditionHooks()) {
			try {
				boolean matchCondition = conditionHook.updateAndMatch(data);
				if (matchCondition) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return conditionOR.isEmpty();
	}

	public static boolean register(ConditionHook conditionHook) {
		if (conditionHook == null) {
			return false;
		}
		if (hookMap.containsKey(conditionHook.getIdentify().toLowerCase())) {
			return false;
		}
		hookMap.put(conditionHook.getIdentify().toLowerCase(), conditionHook);
		return true;
	}

	public static void unregister(ConditionHook conditionHook) {
		if (conditionHook == null || conditionHook.getIdentify() == null) {
			return;
		}
		hookMap.remove(conditionHook.getIdentify().toLowerCase());
	}

	public static boolean isRegister(String identify) {
		if (identify == null) {
			return false;
		}

		return hookMap.containsKey(identify.toLowerCase());
	}

	public static boolean isRegister(ConditionHook conditionHook) {
		return conditionHook == null ? false : hookMap.containsKey(conditionHook.getIdentify().toLowerCase());
	}

	public static ConditionHook get(String identify, String[] args) {
		identify = identify.toLowerCase();
		
		if (isRegister(identify)) {
			ConditionHook hook = hookMap.get(identify).clone();
			hook.setup(args);
			return hook;
		}
		return null;
	}
}
