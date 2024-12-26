package com.bafmc.customenchantment.menu.equipment;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.menu.MenuAbstract;
import com.bafmc.customenchantment.player.PlayerEquipment;
import com.bafmc.custommenu.menu.CMenuView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class EquipmentMenu extends MenuAbstract {
	public static final String MENU_NAME = "equipment";
	private static HashMap<String, EquipmentMenu> map = new HashMap<String, EquipmentMenu>();

	public static EquipmentMenu putMenu(Player player, CMenuView cMenuView) {
		EquipmentMenu menu = map.get(player.getName());

		if (menu == null) {
			menu = new EquipmentMenu(cMenuView, player);
			map.put(player.getName(), menu);
		}

		return menu;
	}

	public static EquipmentMenu getMenu(Player player) {
		return map.get(player.getName());
	}

	public static EquipmentMenu removeMenu(Player player) {
		return map.remove(player.getName());
	}

	public enum EquipmentAddReason {
		SUCCESS, NOT_SUPPORT_ITEM, ADD_ARTIFACT, MAX_ARTIFACT, DUPLICATE_ARTIFACT, NOTHING, NO_ARTIFACT_SLOT;
	}

	public enum EquipmentConfirmReason {
		SUCCESS, NOTHING;
	}

	public EquipmentMenu(CMenuView menuView, Player player) {
		super(menuView, player);
		this.updateMenu();
	}

	public EquipmentAddReason addItem(ItemStack itemStack, CEItem ceItem) {
		if (ceItem instanceof CEArtifact) {
			PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
			int maxArtifactUseCount = getMaxArtifactUseCount();
			if (maxArtifactUseCount <= 0) {
				return EquipmentAddReason.NO_ARTIFACT_SLOT;
			}

			int emptyIndex = getEmptyArtifactIndex();
			if (emptyIndex != -1) {
				if (checkDuplicateArtifactSlot((CEArtifact) ceItem)) {
					return EquipmentAddReason.DUPLICATE_ARTIFACT;
				}
			} else {
				return EquipmentAddReason.MAX_ARTIFACT;
			}

			playerEquipment.setSlot(EquipSlot.getExtraSlot(emptyIndex), (CEArtifact) ceItem);
			updateArtifactSlots();
			return EquipmentAddReason.ADD_ARTIFACT;
		}
		return EquipmentAddReason.NOT_SUPPORT_ITEM;
	}

	public void updateMenu() {
		updateSlots("helmet", EquipSlot.HELMET.getItemStack(player));
		updateSlots("chestplate", EquipSlot.CHESTPLATE.getItemStack(player));
		updateSlots("leggings", EquipSlot.LEGGINGS.getItemStack(player));
		updateSlots("boots", EquipSlot.BOOTS.getItemStack(player));
		updateSlots("offhand", EquipSlot.OFFHAND.getItemStack(player));
		updateSlots("mainhand", EquipSlot.MAINHAND.getItemStack(player));
		updateArtifactSlots();
	}

	public void updateArtifactSlots() {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		playerEquipment.sortExtraSlot();
		int maxArtifactUseCount = getMaxArtifactUseCount();
		for (int i = 0; i < maxArtifactUseCount; i++) {
			List<Integer> slots = getSlots("artifact");

			if (i < slots.size()) {
				CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(EquipSlot.getExtraSlot(i));
				if (weaponAbstract instanceof CEArtifact) {
					menuView.setTemporaryItem(slots.get(i), weaponAbstract.getDefaultItemStack());
				}else {
					menuView.removeTemporaryItem(slots.get(i));
				}
			}
		}
	}

	public int getMaxArtifactUseCount() {
		return CustomEnchantment.instance().getMainConfig().getMaxArtifactUseCount();
	}

	public void returnItem(int slot) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		List<Integer> slots = getSlots("artifact");

		int index = -1;
		for (int i = 0; i < slots.size(); i++) {
			if (slots.get(i) == slot) {
				index = i;
				break;
			}
		}

		if (index == -1) {
			return;
		}

        CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(EquipSlot.getExtraSlot(index));
        if (weaponAbstract != null) {
			playerEquipment.setSlot(EquipSlot.getExtraSlot(index), null);
            player.getInventory().addItem(weaponAbstract.getDefaultItemStack());
        }
        updateArtifactSlots();
    }

	public void returnItems() {
	}

	public int getEmptyArtifactIndex() {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		playerEquipment.sortExtraSlot();

		int maxArtifactUseCount = getMaxArtifactUseCount();
		for (int i = 0; i < maxArtifactUseCount; i++) {
			if (playerEquipment.getSlot(EquipSlot.getExtraSlot(i)) == null) {
				return i;
			}
		}
		return -1;
	}

	public boolean checkDuplicateArtifactSlot(CEArtifact ceArtifact) {
		PlayerEquipment playerEquipment = CEAPI.getCEPlayer(player).getEquipment();
		int maxArtifactUseCount = getMaxArtifactUseCount();
		for (int i = 0; i < maxArtifactUseCount; i++) {
			CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(EquipSlot.getExtraSlot(i));
			if (weaponAbstract instanceof CEArtifact) {
				CEArtifact artifact = (CEArtifact) weaponAbstract;
				if (artifact.getData().getPattern().equals(ceArtifact.getData().getPattern())) {
					return true;
				}
			}
		}
		return false;
	}

	public EquipmentConfirmReason confirmEquipment() {
		return EquipmentConfirmReason.SUCCESS;
	}
}
