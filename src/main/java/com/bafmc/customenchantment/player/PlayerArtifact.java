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
public class PlayerArtifact extends CEPlayerExpansion {
	private Map<EquipSlot, CEWeaponAbstract> previousArtifactActivateMap = new HashMap<>();
	private boolean disableArtifact;

	@Getter
	@Setter
	@AllArgsConstructor
	public static class ArtifactDiff {
		private Map<EquipSlot, CEWeaponAbstract> artifactMap;
		private Map<EquipSlot, CEWeaponAbstract> notExistsArtifactMap;
		private Map<EquipSlot, Boolean> oldArtifactMap;

		public Map<EquipSlot, CEWeaponAbstract> getOnlyDifferentArtifactMap() {
			Map<EquipSlot, CEWeaponAbstract> onlyDifferentArtifactMap = new HashMap<>();
			for (EquipSlot equipSlot : artifactMap.keySet()) {
				if (!oldArtifactMap.get(equipSlot)) {
					onlyDifferentArtifactMap.put(equipSlot, artifactMap.get(equipSlot));
				}
			}
			return onlyDifferentArtifactMap;
		}

		public List<EquipSlot> getEquipSlotList() {
			return List.copyOf(artifactMap.keySet());
		}

		public CEWeaponAbstract getArtifact(EquipSlot equipSlot) {
			return artifactMap.get(equipSlot);
		}

		public boolean isDifferent(EquipSlot equipSlot) {
			return !oldArtifactMap.get(equipSlot);
		}

		public boolean isDifferent() {
			for (EquipSlot equipSlot : artifactMap.keySet()) {
				if (!oldArtifactMap.get(equipSlot)) {
					return true;
				}
			}
			return false;
		}
	}

	public PlayerArtifact(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
	}

	public void onQuit() {

	}

	public ArtifactDiff getDifferentArtifactMap(Map<EquipSlot, CEWeaponAbstract> currentArtifactActivateMap) {
		Map<EquipSlot, CEWeaponAbstract> artifactMap = new HashMap<>();
		Map<EquipSlot, CEWeaponAbstract> notExistsArtifactMap = new HashMap<>(previousArtifactActivateMap);
		Map<EquipSlot, Boolean> oldArtifactMap = new HashMap<>();

		for (EquipSlot equipSlot : currentArtifactActivateMap.keySet()) {
			CEWeaponAbstract currentArtifact = currentArtifactActivateMap.get(equipSlot);

			boolean find = false;

			for (EquipSlot previousEquipSlot : previousArtifactActivateMap.keySet()) {
				CEWeaponAbstract previousArtifact = previousArtifactActivateMap.get(previousEquipSlot);
				if (previousArtifact.getData().getPattern().equals(currentArtifact.getData().getPattern())) {
					notExistsArtifactMap.remove(previousEquipSlot);
					find = true;
					break;
				}
			}

			if (!find) {
				oldArtifactMap.put(equipSlot, false);
			}else {
				oldArtifactMap.put(equipSlot, true);
			}

			artifactMap.put(equipSlot, currentArtifact);
		}

		return new ArtifactDiff(artifactMap, notExistsArtifactMap, oldArtifactMap);
	}

	public void updateArtifactDiff(ArtifactDiff artifactDiff) {
		clear();

		for (EquipSlot equipSlot : artifactDiff.getArtifactMap().keySet()) {
			CEWeaponAbstract artifact = artifactDiff.getArtifactMap().get(equipSlot);
			previousArtifactActivateMap.put(equipSlot, artifact);
		}
	}

	public void clear() {
		previousArtifactActivateMap.clear();
	}
}
