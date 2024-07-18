package me.texward.customenchantment.player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CEFunction;
import me.texward.customenchantment.enchant.Cooldown;
import me.texward.texwardlib.util.EquipSlot;

public class PlayerCECooldown extends CEPlayerExpansion {

	private ConcurrentHashMap<EquipSlot, CECooldown> map = new ConcurrentHashMap<EquipSlot, CECooldown>();

	public PlayerCECooldown(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {

	}

	public void onQuit() {

	}

	public void put(List<EquipSlot> equipSlots, CEEnchant ce, CEFunction ceFunction) {
		for (EquipSlot equipSlot : equipSlots) {
			put(equipSlot, ce.getName(), ceFunction.getName(), ceFunction.getCooldown());
		}
	}

	public void put(List<EquipSlot> equipSlots, String ceName, String functionName, Cooldown cooldown) {
		for (EquipSlot equipSlot : equipSlots) {
			put(equipSlot, ceName, functionName, cooldown);
		}
	}

	public void put(EquipSlot[] equipSlots, CEEnchant ce, CEFunction ceFunction) {
		for (EquipSlot equipSlot : equipSlots) {
			put(equipSlot, ce.getName(), ceFunction.getName(), ceFunction.getCooldown());
		}
	}

	public void put(EquipSlot[] equipSlots, String ceName, String functionName, Cooldown cooldown) {
		for (EquipSlot equipSlot : equipSlots) {
			put(equipSlot, ceName, functionName, cooldown);
		}
	}

	public void put(EquipSlot equipSlot, CEEnchant ce, CEFunction ceFunction) {
		put(equipSlot, ce.getName(), ceFunction.getName(), ceFunction.getCooldown());
	}

	public void put(EquipSlot equipSlot, String ceName, String functionName, Cooldown cooldown) {
		ceName = ceName != null ? ceName : "UNKNOWN";
		functionName = functionName != null ? functionName : "UNKNOWN";
		cooldown = cooldown != null ? cooldown.clone() : new Cooldown(0);

		CECooldown ceCooldown = map.get(equipSlot);

		if (ceCooldown == null) {
			ceCooldown = new CECooldown();
			map.put(equipSlot, ceCooldown);
		}

		ceCooldown.put(ceName, functionName, cooldown);
	}

	public boolean isCooldownTimeout(EquipSlot equipSlot, CEEnchant ce, CEFunction ceFunction) {
		return isCooldownTimeout(equipSlot, ce.getName(), ceFunction.getName());
	}

	public boolean isCooldownTimeout(EquipSlot equipSlot, String ceName, String functionName) {
		ceName = ceName != null ? ceName : "UNKNOWN";
		functionName = functionName != null ? functionName : "UNKNOWN";

		if (map.containsKey(EquipSlot.ALL) && !map.get(EquipSlot.ALL).isCooldownTimeout(ceName, functionName)) {
			return false;
		}

		if (equipSlot.isHand() && map.containsKey(EquipSlot.HAND)
				&& !map.get(EquipSlot.HAND).isCooldownTimeout(ceName, functionName)) {
			return false;
		}

		if (equipSlot.isArmor() && map.containsKey(EquipSlot.ARMOR)
				&& !map.get(EquipSlot.ARMOR).isCooldownTimeout(ceName, functionName)) {
			return false;
		}

		return map.containsKey(equipSlot) ? map.get(equipSlot).isCooldownTimeout(ceName, functionName) : true;
	}
}

class CECooldown {
	private ConcurrentHashMap<String, CEFunctionCooldown> map = new ConcurrentHashMap<String, CEFunctionCooldown>();

	public void put(String name, String functionName, Cooldown cooldown) {
		CEFunctionCooldown functionCooldown = map.get(name);

		if (functionCooldown == null) {
			functionCooldown = new CEFunctionCooldown();
			map.put(name, functionCooldown);
		}

		functionCooldown.put(functionName, cooldown);
	}

	public boolean isCooldownTimeout(String name, String functionName) {
		return map.containsKey(name) ? map.get(name).isCooldownTimeout(functionName) : true;
	}
}

class CEFunctionCooldown {
	private ConcurrentHashMap<String, Cooldown> map = new ConcurrentHashMap<String, Cooldown>();

	public void put(String name, Cooldown cooldown) {
		cooldown.start();
		map.put(name, cooldown);
	}

	public boolean isCooldownTimeout(String name) {
		return map.containsKey(name) ? !map.get(name).isInCooldown() : true;
	}
}
