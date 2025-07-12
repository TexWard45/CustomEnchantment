package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PlayerExtraSlot extends CEPlayerExpansion {
	private Map<EquipSlot, CEWeaponAbstract> previousExtraSlotActivateMap = new HashMap<>();
	private boolean disableExtraSlot;

	@Getter
	@Setter
	@AllArgsConstructor
	public static class ExtraSlotDiff {
		private Map<EquipSlot, CEWeaponAbstract> extraSlotMap;
		private Map<EquipSlot, CEWeaponAbstract> notExistsExtraSlotMap;
		private Map<EquipSlot, Boolean> oldExtraSlotMap;

		public Map<EquipSlot, CEWeaponAbstract> getOnlyDifferentArtifactMap() {
			Map<EquipSlot, CEWeaponAbstract> onlyDifferentArtifactMap = new HashMap<>();
			for (EquipSlot equipSlot : extraSlotMap.keySet()) {
				if (!oldExtraSlotMap.get(equipSlot)) {
					onlyDifferentArtifactMap.put(equipSlot, extraSlotMap.get(equipSlot));
				}
			}
			return onlyDifferentArtifactMap;
		}

		public List<EquipSlot> getEquipSlotList() {
			return List.copyOf(extraSlotMap.keySet());
		}

		public CEWeaponAbstract getExtraSlot(EquipSlot equipSlot) {
			return extraSlotMap.get(equipSlot);
		}

		public boolean isDifferent(EquipSlot equipSlot) {
			return !oldExtraSlotMap.get(equipSlot);
		}

		public boolean isDifferent() {
			for (EquipSlot equipSlot : extraSlotMap.keySet()) {
				if (!oldExtraSlotMap.get(equipSlot)) {
					return true;
				}
			}
			return false;
		}
	}

	public PlayerExtraSlot(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
	}

	public void onQuit() {

	}

	public ExtraSlotDiff getDifferentExtraSlotMap(Map<EquipSlot, CEWeaponAbstract> currentExtraSlotActivateMap) {
		Map<EquipSlot, CEWeaponAbstract> extraSlotMap = new HashMap<>();
		Map<EquipSlot, CEWeaponAbstract> notExistsArtifactMap = new HashMap<>(previousExtraSlotActivateMap);
		Map<EquipSlot, Boolean> oldExtraSlotMap = new HashMap<>();

		for (EquipSlot equipSlot : currentExtraSlotActivateMap.keySet()) {
			CEWeaponAbstract currentExtraSlot = currentExtraSlotActivateMap.get(equipSlot);

			boolean find = false;

			for (EquipSlot previousEquipSlot : previousExtraSlotActivateMap.keySet()) {
				CEWeaponAbstract previousExtraSlot = previousExtraSlotActivateMap.get(previousEquipSlot);
				if (previousExtraSlot.getData().getPattern().equals(currentExtraSlot.getData().getPattern())) {
					notExistsArtifactMap.remove(previousEquipSlot);
					find = true;
					break;
				}
			}

			if (!find) {
				oldExtraSlotMap.put(equipSlot, false);
			}else {
				oldExtraSlotMap.put(equipSlot, true);
			}

			extraSlotMap.put(equipSlot, currentExtraSlot);
		}

		return new ExtraSlotDiff(extraSlotMap, notExistsArtifactMap, oldExtraSlotMap);
	}

	public void updateArtifactDiff(ExtraSlotDiff extraSlotDiff) {
		clear();

		for (EquipSlot equipSlot : extraSlotDiff.getExtraSlotMap().keySet()) {
			CEWeaponAbstract artifact = extraSlotDiff.getExtraSlotMap().get(equipSlot);
			previousExtraSlotActivateMap.put(equipSlot, artifact);
		}
	}

	public void clear() {
		previousExtraSlotActivateMap.clear();
	}
}
