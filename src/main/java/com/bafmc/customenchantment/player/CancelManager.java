package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.attribute.CustomAttributeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CancelManager {
	private CEPlayerExpansion cePlayerExpansion;
	private ConcurrentHashMap<String, CancelData> list = new ConcurrentHashMap<>();

	public CancelManager(CEPlayerExpansion cePlayerExpansion) {
		this.cePlayerExpansion = cePlayerExpansion;
	}

	@Getter
	@AllArgsConstructor
	public static class CancelData {
		private boolean cancel;
		private long endTime;
	}

	public void setCancel(String unique, boolean cancel, long duration) {
		if (cancel && duration > 0) {
			double magicResistance = cePlayerExpansion.getCEPlayer().getCustomAttribute().getValue(CustomAttributeType.MAGIC_RESISTANCE);
			long newDuration = (long) (duration * (1 - magicResistance / 100));
			if (newDuration <= 0) {
				return;
			}

			list.put(unique, new CancelData(cancel, System.currentTimeMillis() + newDuration));
		} else {
			list.remove(unique);
		}
	}

	/*
	 * ratio: 0.0 ~ 1.0 - Support for reducing the duration of the effect
	 */
	public boolean isCancel() {
		long currentTime = System.currentTimeMillis();
		List<String> keyList = List.copyOf(list.keySet());
		for (String key : keyList) {
			CancelData cancelData = list.get(key);
			if (cancelData == null) {
				list.remove(key);
				continue;
			}

			if (cancelData.getEndTime() > 0 && cancelData.getEndTime() < currentTime) {
				list.remove(key);
				continue;
			}

			if (cancelData.isCancel()) {
				return true;
			}
		}

		return false;
	}
}