package me.texward.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import me.texward.customenchantment.player.CEPlayer;

public class EffectTaskSeparate {
	private List<EffectData> effectList = new ArrayList<EffectData>();
	private List<EffectData> effectAsyncList = new ArrayList<EffectData>();

	public EffectTaskSeparate() {
	}

	public EffectTaskSeparate(CEPlayer caller, Effect effect, CEFunctionData data) {
		this.add(caller, effect, data);
	}

	public void add(CEPlayer caller, Effect effect, CEFunctionData data) {
		for (EffectHook effectHook : effect.getEffectHooks()) {
			if (effectHook.isAsync()) {
				effectAsyncList.add(new EffectData(caller, effectHook, data));
			} else {
				effectList.add(new EffectData(caller, effectHook, data));
			}
		}
	}

	public List<EffectData> getEffectList() {
		return effectList;
	}

	public List<EffectData> getEffectAsyncList() {
		return effectAsyncList;
	}
}
