package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bafmc.customenchantment.command.CommandDebugCE;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.EquipSlot;

public class CECaller {
	private HashMap<EquipSlot, List<String>> slotEnchantsChanceMap = new HashMap<EquipSlot, List<String>>();
	private CEType ceType;
	private CEPlayer caller;
	private CEFunctionData data;
	private List<CESimple> ceSimpleList;
	private List<EquipSlot> equipSlotList;
	private boolean byPassCooldown;
	private CECallerResult result;
	private EquipSlot equipSlot;
	private EquipSlot activeEquipSlot;
	private CESimple ceSimple;
	private CEEnchant ceEnchant;
	private CEFunction ceFunction;
	private Map<EquipSlot, CEWeaponAbstract> weaponMap;
	private CEWeaponAbstract weaponAbstract;
	private boolean executerLater = true;
	private boolean primary;

	private CECaller() {
	}

	public static CECaller instance() {
		return new CECaller();
	}

	/**
	 * Reset caller result.
	 */
	public CECaller resetResult() {
		this.result = CECallerResult.instance();
		return this;
	}

	/**
	 * You must assign ceType, equipSlotList, data to call this method.
	 * 
	 */
	public CECaller call() {
		if (equipSlotList != null) {
			for (EquipSlot equipSlot : equipSlotList) {
				setEquipSlot(equipSlot).setWeaponAbstract(weaponMap.get(equipSlot)).callEquipSlot();
			}
		} else {
			callEquipSlot();
		}
		return this;
	}

	/**
	 * You must assign ceType, equipSlot, data, ceSimpleList to call this method.
	 * 
	 */
	public CECaller callEquipSlot() {
		if (ceSimpleList != null) {
			for (CESimple ceSimple : ceSimpleList) {
				setCESimple(ceSimple).callCE();
			}
		} else {
			callCE();
		}
		return this;
	}

	/**
	 * You must assign ceType, equipSlot, data, ceSimple to call this method.
	 * 
	 */
	public CECaller callCE() {
		CEEnchant ce = ceSimple.getCEEnchant();
		if (ce == null) {
			return this;
		}

		CELevel ceLevel = ce.getCELevel(ceSimple.getLevel());
		if (ceLevel == null) {
			return this;
		}

		setCEEnchant(ce);

		List<CEFunction> functionList = ceLevel.getFunctionList();

		for (CEFunction function : functionList) {
			try {
				if (setCEFunction(function).callCEFunction().isBreak()) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this;
	}

	/**
	 * You must assign ceType, equipSlot, data, ce, ceFunction to call this method.
	 * 
	 */
	public StepAction callCEFunction() {
		StepAction stepAction = StepAction.CONTINUE;

		// Check type
		if (ceFunction.getCEType() != ceType) {
			return stepAction;
		}

		// Check active equip slot
		if (!isPrimary() && !ceFunction.getActiveSlot().contains(activeEquipSlot)) {
			return StepAction.CONTINUE;
		}

		// Check ce is locked
		if (hasCaller() && caller.getCEManager().isCancelSlot(equipSlot)) {
			return StepAction.CONTINUE;
		}

		// Check ce has already checked chance
		List<EquipSlot> chanceSlot = ceFunction.getChanceSlot();
		if (!chanceSlot.isEmpty()) {
			for (EquipSlot equipSlot : chanceSlot) {
				List<String> enchants = getEnchantChanceList(equipSlot);

				if (enchants.contains(ceEnchant.getName())) {
					return StepAction.CONTINUE;
				}
			}
		}

        boolean chance = ceFunction.getChance().work();

        if (chanceSlot.isEmpty()) {
            putEnchantChance(equipSlot, ceEnchant.getName());
        } else {
            putEnchantChance(chanceSlot, ceEnchant.getName());
        }

		// Check chance
		if (!caller.isAdminMode() && !chance) {
            return StepAction.valueOf(ceFunction.isFalseChanceBreak());
		}
		stepAction = StepAction.valueOf(ceFunction.isTrueChanceBreak());

		// Check cooldown
		if (hasCaller() && !isByPassCooldown() && !caller.getCECooldown().isCooldownTimeout(equipSlot, ceEnchant, ceFunction)) {
			return StepAction.valueOf(ceFunction.isInCooldownBreak());
		}

		stepAction = StepAction.valueOf(ceFunction.isTimeoutCooldownBreak());

		if (data == null) {
			return StepAction.BREAK;
		}

		CEFunctionData data = this.data.clone();
		data.setEquipSlot(equipSlot);
		data.setWeaponAbstract(weaponAbstract);
		data.setActiveEquipSlot(activeEquipSlot);

		// Check condition
		if (!ceFunction.getCondition().check(data)) {
			return StepAction.valueOf(ceFunction.isFalseConditionBreak(), stepAction);
		}

		if (CommandDebugCE.getTogglePlayers().contains(caller.getPlayer().getName())) {
			System.out.println(caller.getPlayer().getName() + " is calling " + ceEnchant.getName() + " level " + ceSimple.getLevel() + " function " + ceFunction.getName() + " slot " + equipSlot);
		}

		// Execute effect
		Effect effect = ceFunction.getEffect();
		executeEffect(effect, data);

		// Add option to list
		Option option = ceFunction.getOption();
		getResult().getOptionDataList().addAll(option.getOptionDataList());

		// Execute other target effect
		TargetFilter targetFilter = ceFunction.getTargetFilter();
		Condition targetCondition = ceFunction.getTargetCondition();
		Option targetOption = ceFunction.getTargetOption();
		Effect targetEffect = ceFunction.getTargetEffect();
		CECallerResult result = executeOtherEffect(targetCondition, targetOption, targetEffect, targetFilter);
		getResult().getOptionDataList().addAll(result.getOptionDataList());

		// Set cooldown to caller
		if (hasCaller() && !isByPassCooldown()) {
			if (ceFunction.getCooldownSlot().isEmpty()) {
				getCaller().getCECooldown().put(equipSlot, ceEnchant, ceFunction);
			} else {
				getCaller().getCECooldown().put(ceFunction.getCooldownSlot(), ceEnchant, ceFunction);
			}
		}

		return StepAction.valueOf(ceFunction.isTrueConditionBreak(), stepAction);
	}

	public void executeEffect(Effect effect, CEFunctionData data) {
		if (isExecuterLater() && !ceFunction.isEffectNow()) {
			EffectTaskSeparate effectSeparate = new EffectTaskSeparate(caller, effect, data);
			CustomEnchantment.instance().addEffectTask(effectSeparate);
		} else {
			effect.execute(data);
		}
	}

	public CECallerResult executeOtherEffect(Condition condition, Option option, Effect effect,
			TargetFilter targetFilter) {
		CECallerResult result = CECallerResult.instance();
		if (targetFilter.isEnable()) {

			List<Player> targets = targetFilter.getTargetsByPlayer(data.getPlayer(), data.getEnemyPlayer());

			for (Player target : targets) {
				CEFunctionData targetData = data.clone().setEnemyPlayer(target);

				if (condition.check(targetData)) {
					result.getOptionDataList().addAll(option.getOptionDataList());
					executeEffect(effect, targetData);
				}
			}
		}

		return result;
	}

	public void putEnchantChance(List<EquipSlot> equipSlots, String name) {
		for (EquipSlot slot : equipSlots) {
			putEnchantChance(slot, name);
		}
	}

	public void putEnchantChance(EquipSlot equipSlot, String name) {
		List<String> list = getEnchantChanceList(equipSlot);

		if (!list.contains(name)) {
			list.add(name);
		}
	}

	public List<String> getEnchantChanceList(EquipSlot equipSlot) {
		List<String> list = slotEnchantsChanceMap.get(equipSlot);

		if (list == null) {
			list = new ArrayList<String>();
			slotEnchantsChanceMap.put(equipSlot, list);
		}

		return list;
	}

	public HashMap<EquipSlot, List<String>> getSlotEnchantsChanceMap() {
		return slotEnchantsChanceMap;
	}

	public CECaller setSlotEnchantsChanceMap(HashMap<EquipSlot, List<String>> slotEnchantsChanceMap) {
		this.slotEnchantsChanceMap = slotEnchantsChanceMap;
		return this;
	}

	public CEType getCEType() {
		return ceType;
	}

	public CECaller setCEType(CEType ceType) {
		this.ceType = ceType;
		return this;
	}

	public CEPlayer getCaller() {
		return caller;
	}

	public boolean hasCaller() {
		return getCaller() != null;
	}

	public CECaller setCaller(CEPlayer caller) {
		this.caller = caller;
		return this;
	}

	public CECaller setCaller(Player caller) {
		this.caller = CEAPI.getCEPlayer(caller);
		return this;
	}

	public CECaller setWeaponAbstract(CEWeaponAbstract weaponAbstract) {
		this.weaponAbstract = weaponAbstract;
		return this;
	}

	public CEWeaponAbstract getWeaponAbstract() {
		return this.weaponAbstract;
	}

	public Map<EquipSlot, CEWeaponAbstract> getWeaponMap() {
		return weaponMap;
	}

	public CECaller setWeaponMap(Map<EquipSlot, CEWeaponAbstract> weaponMap) {
		this.weaponMap = weaponMap;
		return this;
	}

	public CEFunctionData getData() {
		return data;
	}

	public CECaller setData(CEFunctionData data) {
		this.data = data;
		return this;
	}

	public CECaller setData(Player player) {
		this.data = new CEFunctionData(player);
		return this;
	}

	public List<CESimple> getCESimpleList() {
		return ceSimpleList;
	}

	public CECaller setCESimpleList(CEWeaponAbstract ceItem) {
		if (ceItem == null) {
			this.ceSimpleList = new ArrayList<CESimple>();
		} else {
			this.ceSimpleList = ceItem.getWeaponEnchant().getCESimpleList();
		}
		return this;
	}

	public CECaller setCESimpleList(List<CESimple> ceSimpleList) {
		this.ceSimpleList = ceSimpleList;
		return this;
	}

	public List<EquipSlot> getEquipSlotList() {
		return equipSlotList;
	}

	public CECaller setEquipSlotList(EquipSlot[] equipSlotArr) {
		this.equipSlotList = Arrays.asList(equipSlotArr);
		return this;
	}

	public CECaller setEquipSlotList(List<EquipSlot> equipSlotList) {
		this.equipSlotList = equipSlotList;
		return this;
	}

	public boolean isByPassCooldown() {
		return byPassCooldown;
	}

	public CECaller setByPassCooldown(boolean byPassCooldown) {
		this.byPassCooldown = byPassCooldown;
		return this;
	}

	public CECallerResult getResult() {
		return result != null ? result : (result = CECallerResult.instance());
	}

	public EquipSlot getEquipSlot() {
		return equipSlot;
	}

	public CECaller setEquipSlot(EquipSlot equipSlot) {
		this.equipSlot = equipSlot;
		return this;
	}

	public EquipSlot getActiveEquipSlot() {
		return activeEquipSlot;
	}

	public CECaller setActiveEquipSlot(EquipSlot activeEquipSlot) {
		this.activeEquipSlot = activeEquipSlot;
		return this;
	}
	
	public boolean isPrimary() {
		return primary;
	}

	public CECaller setPrimary(boolean primary) {
		this.primary = primary;
		return this;
	}

	public CESimple getCESimple() {
		return ceSimple;
	}

	public CECaller setCESimple(CESimple ceSimple) {
		this.ceSimple = ceSimple;
		return this;
	}

	public CEEnchant getCE() {
		return ceEnchant;
	}

	public CECaller setCEEnchant(CEEnchant ceEnchant) {
		this.ceEnchant = ceEnchant;
		return this;
	}

	public CEFunction getCEFunction() {
		return ceFunction;
	}

	public CECaller setCEFunction(CEFunction ceFunction) {
		this.ceFunction = ceFunction;
		return this;
	}

	public boolean isExecuterLater() {
		return executerLater;
	}

	public CECaller setExecuterLater(boolean executerLater) {
		this.executerLater = executerLater;
		return this;
	}
}
