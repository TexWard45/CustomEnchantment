package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeSlot;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.attribute.RangeAttribute;
import com.bafmc.customenchantment.item.gem.CEGemSimple;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerGem extends CEPlayerExpansion {
	private Map<CEWeaponAbstract, WeaponAttribute> gemAttributeMap = new HashMap<>();

	public static class WeaponAttribute {
		private CEWeaponAbstract ceWeaponAbstract;
		private List<GemAttribute> gemAttributes = new ArrayList<>();;

		public WeaponAttribute(CEWeaponAbstract ceWeaponAbstract) {
			this.ceWeaponAbstract = ceWeaponAbstract;
			this.init();
		}

		private void init() {
			List<CEGemSimple> gemSimples = ceWeaponAbstract.getWeaponGem().getCEGemSimpleList();
			for (CEGemSimple gemSimple : gemSimples) {
				List<NMSAttribute> nmsAttributes = gemSimple.getCEGemData().getNMSAttributes();

				for (NMSAttribute nmsAttribute : nmsAttributes) {
					this.gemAttributes.add(new GemAttribute(nmsAttribute, NMSAttributeSlot.getByName(nmsAttribute.getSlot())));
				}
			}
		}

		public boolean isAllDeactivation() {
			for (GemAttribute gemAttribute : gemAttributes) {
				if (!gemAttribute.activeSlots.isEmpty()) {
					return false;
				}
			}

			return true;
		}

		public void activeSlot(CEPlayer cePlayer, EquipSlot slot) {
			for (GemAttribute gemAttribute : gemAttributes) {
				gemAttribute.activeSlot(cePlayer, slot);
			}
		}

		public void deactiveSlot(CEPlayer cePlayer, EquipSlot slot) {
			for (GemAttribute gemAttribute : gemAttributes) {
				gemAttribute.deactiveSlot(cePlayer, slot);
			}
		}

		public void deactiveSlot(CEPlayer cePlayer) {
			for (GemAttribute gemAttribute : gemAttributes) {
				gemAttribute.deactiveSlot(cePlayer);
			}
		}
	}

	public static class GemAttribute {
		private NMSAttribute nmsAttribute;
		private NMSAttributeSlot nmsSlot;
		private List<EquipSlot> activeSlots = new ArrayList<>();
		private String id;

		public GemAttribute(NMSAttribute nmsAttribute, NMSAttributeSlot nmsSlot) {
			this.nmsAttribute = nmsAttribute;
			this.nmsSlot = nmsSlot;
			this.generateId();
		}

		private void generateId() {
			this.id = nmsAttribute.getName() + "-" + System.nanoTime();
		}

		public void deactiveSlot(CEPlayer cePlayer) {
			List<EquipSlot> activeSlots = new ArrayList<>(this.activeSlots);
			for (EquipSlot slot : activeSlots) {
				deactiveSlot(cePlayer, slot);
			}
		}

		public void deactiveSlot(CEPlayer cePlayer, EquipSlot slot) {
			if (!isSuitableSlot(slot, nmsSlot)) {
				return;
			}

			// If activeSlots is empty, there is nothing to do
			if (activeSlots.isEmpty()) {
				return;
			}else {
				activeSlots.remove(slot);
			}

			// If activeSlots is not empty, other slots are still active
			if (!activeSlots.isEmpty()) {
				return;
			}

			if (nmsAttribute.getAttributeType().isVanilla()) {
				String name = id;
				Attribute attribute = nmsAttribute.getAttributeType().getBukkitAttribute();

				cePlayer.getVanillaAttribute().removeAttribute(attribute, name);
			}else {
				cePlayer.getCustomAttribute().removeCustomAttribute(this.id);
			}

			activeSlots.remove(slot);
		}

		public void activeSlot(CEPlayer cePlayer, EquipSlot slot) {
			if (!isSuitableSlot(slot, nmsSlot)) {
				return;
			}

			// If activeSlots already contains slot, other slots are active before
			if (activeSlots.contains(slot)) {
				return;
			}else {
				activeSlots.add(slot);
			}

			// If activeSlots contains more than 1 slot, there is nothing to do because it is already active
			if (activeSlots.size() > 1) {
				return;
			}

			if (nmsAttribute.getAttributeType().isVanilla()) {
				String name = id;
				Attribute attribute = nmsAttribute.getAttributeType().getBukkitAttribute();
				double amount = nmsAttribute.getAmount();
				AttributeModifier.Operation operation = nmsAttribute.getOperation().getBukkitOperation();
				cePlayer.getVanillaAttribute().addAttribute(attribute, name, amount, operation);
			}else {
				NMSAttributeType nmsAttributeType = nmsAttribute.getAttributeType();
				double amount = nmsAttribute.getAmount();
				NMSAttributeOperation nmsAttributeOperation = nmsAttribute.getOperation();
				RangeAttribute rangeAttribute = new RangeAttribute(nmsAttributeType, amount, nmsAttributeOperation);
				cePlayer.getCustomAttribute().addCustomAttribute(this.id, rangeAttribute);
			}
		}
	}

	public PlayerGem(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
		deactivateAll();
	}

	public void onQuit() {
		deactivateAll();
	}

	public void handleAttributeActivation(EquipSlot slot, CEWeaponAbstract ceWeaponAbstract) {
		WeaponAttribute weaponGemAttribute = gemAttributeMap.get(ceWeaponAbstract);
		if (weaponGemAttribute == null) {
			weaponGemAttribute = new WeaponAttribute(ceWeaponAbstract);
			gemAttributeMap.put(ceWeaponAbstract, weaponGemAttribute);
		}

		weaponGemAttribute.activeSlot(cePlayer, slot);
	}

	public void handleAttributeDeactivation(EquipSlot slot, CEWeaponAbstract ceWeaponAbstract) {
		WeaponAttribute weaponGemAttribute = gemAttributeMap.get(ceWeaponAbstract);
		if (weaponGemAttribute == null) {
			return;
		}

		weaponGemAttribute.deactiveSlot(cePlayer, slot);

		if (weaponGemAttribute.isAllDeactivation()) {
			gemAttributeMap.remove(ceWeaponAbstract);
		}
	}

	public void deactivateAll() {
		for (WeaponAttribute weaponAttribute : gemAttributeMap.values()) {
			weaponAttribute.deactiveSlot(cePlayer);
		}

		gemAttributeMap.clear();
	}

	public static boolean isSuitableSlot(EquipSlot equipSlot, NMSAttributeSlot nmsSlot) {
		if (nmsSlot == null || nmsSlot == NMSAttributeSlot.ALL) {
			return true;
		}

		if (nmsSlot == NMSAttributeSlot.HAND) {
			return equipSlot == EquipSlot.MAINHAND;
		}

		if (nmsSlot == NMSAttributeSlot.OFFHAND) {
			return equipSlot == EquipSlot.OFFHAND;
		}

		if (nmsSlot == NMSAttributeSlot.FEET) {
			return equipSlot == EquipSlot.BOOTS;
		}

		if (nmsSlot == NMSAttributeSlot.LEGS) {
			return equipSlot == EquipSlot.LEGGINGS;
		}

		if (nmsSlot == NMSAttributeSlot.CHEST) {
			return equipSlot == EquipSlot.CHESTPLATE;
		}

		if (nmsSlot == NMSAttributeSlot.HEAD) {
			return equipSlot == EquipSlot.HELMET;
		}

		if (nmsSlot == NMSAttributeSlot.ARMOR) {
			return equipSlot.isArmor();
		}

		if (nmsSlot == NMSAttributeSlot.WEAPON) {
			return equipSlot.isHand();
		}

		if (nmsSlot == NMSAttributeSlot.HOTBAR) {
			return equipSlot.isHotbar();
		}

		return false;
	}
}
