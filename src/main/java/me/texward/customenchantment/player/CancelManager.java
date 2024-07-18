package me.texward.customenchantment.player;

import java.util.concurrent.ConcurrentHashMap;

public class CancelManager {
	private ConcurrentHashMap<String, Boolean> list = new ConcurrentHashMap<String, Boolean>();
	private int cancelCount;

	public void setCancel(String unique, boolean cancel) {
		Boolean current = this.list.get(unique);
		if (current == null) {
			if (cancel) {
				this.list.put(unique, cancel);
				this.cancelCount++;
			}
			return;
		}
		
		if (current && !cancel) {
			this.list.remove(unique);
			this.cancelCount--;
		}
		
		if (!current && cancel) {
			this.list.put(unique, true);
			this.cancelCount++;
		}
	}

	public boolean isCancel() {
		return cancelCount > 0;
	}
}