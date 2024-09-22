package com.bafmc.customenchantment.enchant;

import java.util.HashMap;
import java.util.List;

public class Effect {
	private static HashMap<String, EffectHook> hookMap = new HashMap<String, EffectHook>();
	private List<EffectHook> effectHooks;

	public Effect(List<EffectHook> effects) {
		this.effectHooks = effects;
	}

	public void execute(CEFunctionData data) {
		for (EffectHook effectHook : effectHooks) {
			try {
				effectHook.updateAndExecute(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<EffectHook> getEffectHooks() {
		return effectHooks;
	}

	public static boolean register(EffectHook executeHook) {
		if (executeHook == null) {
			return false;
		}
		if (hookMap.containsKey(executeHook.getIdentify())) {
			return false;
		}
		hookMap.put(executeHook.getIdentify().toLowerCase(), executeHook);
		return true;
	}

	public static void unregister(EffectHook executeHook) {
		if (executeHook == null || executeHook.getIdentify() == null) {
			return;
		}
		hookMap.remove(executeHook.getIdentify().toLowerCase());
	}

	public static boolean isRegister(String identify) {
		if (identify == null) {
			return false;
		}
		return hookMap.containsKey(identify.toLowerCase());
	}

	public static boolean isRegister(EffectHook executeHook) {
		return executeHook == null ? false : hookMap.containsKey(executeHook.getIdentify().toLowerCase());
	}

	public static EffectHook get(String identify, String[] args) {
		identify = identify.toLowerCase();

		if (isRegister(identify)) {
			EffectHook hook = hookMap.get(identify).clone();
			hook.setup(args);
			return hook;
		}
		return null;
	}
}
