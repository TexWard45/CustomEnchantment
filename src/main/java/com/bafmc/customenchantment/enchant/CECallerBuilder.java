package com.bafmc.customenchantment.enchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bafmc.customenchantment.CustomEnchantment;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.bukkit.utils.EquipSlot;

public class CECallerBuilder {
	private Player player;
	private CEType ceType;
	private EquipSlot activeEquipSlot;
	private CEFunctionData ceFunctionData;
	private Map<EquipSlot, CEWeaponAbstract> weaponMap;
	private boolean executeLater = true;
	private boolean checkWorld = true;
    private boolean bypassCooldown = false;
	
	private CECallerBuilder() {
	}
	
	public static CECallerBuilder build() {
		return new CECallerBuilder();
	}
	
	public static CECallerBuilder build(Player player) {
		return build().setPlayer(player).setCEFunctionData(new CEFunctionData(player));
	}

	public CECallerList call() {
		if (checkWorld && CustomEnchantment.instance().getMainConfig().isEnchantDisableLocation(player.getLocation())) {
			return new CECallerList();
		}

		CECallerList callerList = new CECallerList();

		HashMap<EquipSlot, List<String>> slotEnchantChanceMap = new HashMap<EquipSlot, List<String>>();

		Map<EquipSlot, CEWeaponAbstract> weaponMap = getWeaponMap();

		CEFunctionData data = getCEFunctionData();
		
		for (EquipSlot slot : weaponMap.keySet()) {
			if (!slot.isSpecific()) {
				continue;
			}
			
			boolean primary = activeEquipSlot == null || activeEquipSlot == slot;

			CECaller caller = CECaller.instance()
					.setSlotEnchantsChanceMap(slotEnchantChanceMap)
					.setCaller(player)
					.setData(data)
					.setPrimary(primary)
					.setEquipSlot(slot)
					.setActiveEquipSlot(activeEquipSlot)
					.setWeaponMap(weaponMap)
					.setCESimpleList(weaponMap.get(slot))
					.setCEType(ceType)
                    .setByPassCooldown(bypassCooldown)
					.setExecuterLater(executeLater).call();
			slotEnchantChanceMap = caller.getSlotEnchantsChanceMap();
			callerList.add(caller);
		}
		return callerList;
	}

	public CECallerBuilder setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public CECallerBuilder setCEType(CEType ceType) {
		this.ceType = ceType;
		return this;
	}

	public CECallerBuilder setActiveEquipSlot(EquipSlot activeEquipSlot) {
		this.activeEquipSlot = activeEquipSlot;
		return this;
	}

	public CECallerBuilder setCEFunctionData(CEFunctionData ceFunctionData) {
		this.ceFunctionData = ceFunctionData;
		return this;
	}

	public CECallerBuilder setWeaponMap(Map<EquipSlot, CEWeaponAbstract> weaponMap) {
		this.weaponMap = weaponMap;
		return this;
	}

	public CECallerBuilder setExecuteLater(boolean executeLater) {
		this.executeLater = executeLater;
		return this;
	}

	public CECallerBuilder setCheckWorld(boolean checkWorld) {
		this.checkWorld = checkWorld;
		return this;
	}

    public CECallerBuilder setByPassCooldown(boolean bypassCooldown) {
        this.bypassCooldown = bypassCooldown;
        return this;
    }

	public Player getPlayer() {
		return player;
	}

	public CEType getCeType() {
		return ceType;
	}

	public EquipSlot getActiveEquipSlot() {
		return activeEquipSlot;
	}

	public CEFunctionData getCEFunctionData() {
		return ceFunctionData != null ? ceFunctionData : new CEFunctionData(player);
	}

	public Map<EquipSlot, CEWeaponAbstract> getWeaponMap() {
		return weaponMap != null ? weaponMap : CEAPI.getCEWeaponMap(player);
	}

	public boolean isExecuteLater() {
		return executeLater;
	}

	public boolean isCheckWorld() {
		return checkWorld;
	}
}
