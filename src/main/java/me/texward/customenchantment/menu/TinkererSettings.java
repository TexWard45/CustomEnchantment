package me.texward.customenchantment.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.texward.customenchantment.item.CEItem;

public class TinkererSettings {
	private ConcurrentHashMap<String, TinkererReward> map = new ConcurrentHashMap<String, TinkererReward>();
	private List<Integer> tinkerSlots = new ArrayList<Integer>();
	private List<Integer> rewardSlots = new ArrayList<Integer>();
	private int size;

	public TinkererSettings(ConcurrentHashMap<String, TinkererReward> map, List<Integer> tinkerSlots,
			List<Integer> rewardSlots) {
		this.map = map;
		this.tinkerSlots = tinkerSlots;
		this.rewardSlots = rewardSlots;
		this.size = tinkerSlots.size();
	}

	public TinkererReward getReward(CEItem ceItem) {
		String type = TinkererTypeRegister.getType(ceItem);
		return type != null ? map.get(type) : null;
	}

	/**
	 * Get reward slot
	 * 
	 * @param tinkerSlot slot of tinker
	 * @return slot of reward
	 */
	public int getOppositeTinkerSlot(int tinkerSlot) {
		int index = getTinkerIndex(tinkerSlot);

		return index == -1 ? -1 : rewardSlots.get(index);
	}

	/**
	 * Get tinker slot
	 * 
	 * @param rewardSlot slot of tinker
	 * @return slot of reward
	 */
	public int getOppositeRewardSlot(int rewardSlot) {
		int index = getRewardIndex(rewardSlot);

		return index == -1 ? -1 : tinkerSlots.get(index);
	}

	public int getTinkerIndex(int tinkerSlot) {
		for (int i = 0; i < tinkerSlots.size(); i++) {
			if (tinkerSlots.get(i) == tinkerSlot) {
				return i;
			}
		}
		return -1;
	}

	public int getRewardIndex(int rewardSlot) {
		for (int i = 0; i < rewardSlots.size(); i++) {
			if (rewardSlots.get(i) == rewardSlot) {
				return i;
			}
		}
		return -1;
	}

	public int getTinkerSlot(int index) {
		return tinkerSlots.get(index);
	}

	public int getRewardSlot(int index) {
		return rewardSlots.get(index);
	}

	public int getSize() {
		return size;
	}
}
