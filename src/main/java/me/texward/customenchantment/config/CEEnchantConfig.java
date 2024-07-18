package me.texward.customenchantment.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.CustomEnchantmentDebug;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.MaterialList;
import me.texward.customenchantment.attribute.AttributeData;
import me.texward.customenchantment.attribute.AttributeData.Operation;
import me.texward.customenchantment.enchant.CEDisplay;
import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CEFunction;
import me.texward.customenchantment.enchant.CEGroup;
import me.texward.customenchantment.enchant.CELevel;
import me.texward.customenchantment.enchant.CEType;
import me.texward.customenchantment.enchant.Condition;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.customenchantment.enchant.ConditionOR;
import me.texward.customenchantment.enchant.ConditionSettings;
import me.texward.customenchantment.enchant.Cooldown;
import me.texward.customenchantment.enchant.Effect;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.enchant.EffectSettings;
import me.texward.customenchantment.enchant.Option;
import me.texward.customenchantment.enchant.OptionType;
import me.texward.customenchantment.enchant.Target;
import me.texward.customenchantment.enchant.TargetFilter;
import me.texward.texwardlib.configuration.AbstractConfig;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;
import me.texward.texwardlib.util.Chance;
import me.texward.texwardlib.util.EnumUtils;
import me.texward.texwardlib.util.EquipSlot;
import me.texward.texwardlib.util.SparseMap;
import me.texward.texwardlib.util.StringUtils;

public class CEEnchantConfig extends AbstractConfig {

	protected void loadConfig() {
		Set<String> enchantKeys = config.getKeys(false);

		for (String key : enchantKeys) {
			CEEnchant ceEnchant = loadCEEnchant(key, config.getAdvancedConfigurationSection(key));
			CustomEnchantment.instance().getCEEnchantMap().put(ceEnchant.getName(), ceEnchant);
		}
	}

	public CEEnchant loadCEEnchant(String key, AdvancedConfigurationSection config) {
		String name = key;
		String groupName = config.getString("group");
		int maxLevel = config.getInt("max-level");
		int valuable = config.getInt("valuable");
		int enchantPoint = config.getInt("enchant-point");
        String set = config.getString("set-example");
		CEGroup ceGroup = CEAPI.getCEGroup(groupName);
		valuable = ceGroup.getValuable();
		CEDisplay ceDisplay = new CEDisplay(config.getStringColor("display"),
				config.getStringColor("custom-display-lore"),
				config.getBoolean("disable-enchant-lore", ceGroup.isDisableEnchantLore()),
				config.getStringColorList("description"), config.getStringColorList("detail-description"),
				config.getStringColorList("applies-description"));
		ceDisplay.setDescriptionMap(loadDescriptionMap(config.getAdvancedConfigurationSection("descriptions")));

		SparseMap<CELevel> ceLevelMap = loadCELevels(config.getAdvancedConfigurationSection("levels"));
		MaterialList appliesMaterialList = MaterialList.getMaterialList(config.getStringList("applies"));

		return new CEEnchant(name, groupName, maxLevel, valuable, enchantPoint, ceDisplay, ceLevelMap,
				appliesMaterialList);
	}

	public HashMap<Integer, List<String>> loadDescriptionMap(AdvancedConfigurationSection config) {
		HashMap<Integer, List<String>> map = new HashMap<Integer, List<String>>();

		for (String key : config.getKeys(false)) {
			map.put(Integer.valueOf(key), config.getStringList(key));
		}

		return map;
	}

	public SparseMap<CELevel> loadCELevels(AdvancedConfigurationSection config) {
		SparseMap<CELevel> map = new SparseMap<CELevel>();

		for (String levelKey : config.getKeys(false)) {
			try {
				map.put(Integer.valueOf(levelKey), loadCELevel(config.getAdvancedConfigurationSection(levelKey)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return map;
	}

	public CELevel loadCELevel(AdvancedConfigurationSection config) {
		LinkedHashMap<String, CEFunction> functionMap = loadFunctionMap(config);

		return new CELevel(functionMap);
	}

	public LinkedHashMap<String, CEFunction> loadFunctionMap(AdvancedConfigurationSection config) {
		LinkedHashMap<String, CEFunction> map = new LinkedHashMap<String, CEFunction>();
		Set<String> functionKeys = config.getKeys(false);

		for (String key : functionKeys) {
			map.put(key, loadCEFunction(key, config.getAdvancedConfigurationSection(key)));
		}

		return map;
	}

	public CEFunction loadCEFunction(String key, AdvancedConfigurationSection config) {
		String name = key;
		CEType ceType = CEType.valueOf(config.getString("type"));
		Chance chance = new Chance(config.getDouble("chance"));
		Cooldown cooldown = new Cooldown(config.getLong("cooldown"));
		List<EquipSlot> chanceSlot = EnumUtils.getEnumListByStringList(EquipSlot.class,
				config.getStringList("chance-slot"));
		List<EquipSlot> cooldownSlot = EnumUtils.getEnumListByStringList(EquipSlot.class,
				config.getStringList("cooldown-slot"));
		List<EquipSlot> activeSlot = EnumUtils.getEnumListByStringList(EquipSlot.class,
				config.getStringList("active-slot"));
		TargetFilter targetFilter = loadTargetFilter(config.getAdvancedConfigurationSection("target-filter"));
		Condition targetCondition = loadCondition(config.getStringList("target-conditions"));
		Option targetOption = loadOption(config.getStringList("target-options"));
		Effect targetEffect = loadEffect(config.getStringList("target-effects"));
		Option option = loadOption(config.getStringList("options"));
		Condition condition = loadCondition(config.getStringList("conditions"));
		Effect effect = loadEffect(config.getStringList("effects"));
		boolean effectNow = config.getBoolean("effect-now");
		boolean trueChanceBreak = config.getBoolean("true-chance-break");
		boolean falseChanceBreak = config.getBoolean("false-chance-break");
		boolean timeoutCooldownBreak = config.getBoolean("timeout-cooldown-break");
		boolean inCooldownBreak = config.getBoolean("in-cooldown-break");
		boolean trueConditionBreak = config.getBoolean("true-condition-break");
		boolean falseConditionBreak = config.getBoolean("false-condition-break");

		return new CEFunction(name, ceType, chance, cooldown, chanceSlot, cooldownSlot, activeSlot, targetFilter,
				targetCondition, targetOption, targetEffect, condition, option, effect, effectNow, trueChanceBreak,
				falseChanceBreak, timeoutCooldownBreak, inCooldownBreak, trueConditionBreak, falseConditionBreak);
	}

	public TargetFilter loadTargetFilter(AdvancedConfigurationSection config) {
		Target target = Target.PLAYER;
		boolean enable = false;
		double minDistance = 0;
		double maxDistance = Double.MAX_VALUE;
		int minTarget = 0;
		int maxTarget = Integer.MAX_VALUE;
		boolean exceptPlayer = false;
		boolean exceptEnemy = false;

		if (config != null) {
			enable = config.getBoolean("enable", true);
			target = Target.valueOf(config.getString("target", "PLAYER"));
			minDistance = config.getDouble("min-distance", 0);
			maxDistance = config.getDouble("max-distance", Double.MAX_VALUE);
			minTarget = config.getInt("min-target", 0);
			maxTarget = config.getInt("max-target", Integer.MAX_VALUE);
			exceptPlayer = config.getBoolean("except-player", true);
			exceptEnemy = config.getBoolean("except-enemy", false);
		} else {
			enable = false;
		}

		return new TargetFilter(enable, target, exceptPlayer, exceptEnemy, minDistance, maxDistance, minTarget,
				maxTarget);
	}

	public Condition loadCondition(List<String> lines) {
		List<ConditionOR> conditions = new ArrayList<ConditionOR>();

		for (String line : lines) {
			if (line.isEmpty()) {
				continue;
			}

			try {
				conditions.add(loadConditionOR(line));
			} catch (Exception e) {
				CustomEnchantmentDebug.warn("Error option at line: " + line);
				e.printStackTrace();
				continue;
			}
		}

		return new Condition(conditions);
	}

	public ConditionOR loadConditionOR(String line) {
		line = line.replace("\\ || ", "_OR_");

		List<String> conditions = StringUtils.split(line, " || ", 0);

		List<ConditionHook> list = new ArrayList<ConditionHook>();

		for (String condition : conditions) {
			condition = condition.replace("_OR_", " || ");
			list.add(loadConditionHook(condition));
		}

		return new ConditionOR(list);
	}

	public ConditionHook loadConditionHook(String line) {
		line = line.replace("\\ | ", "_PIPE_");

		List<String> conditions = StringUtils.split(line, " | ", 0);
		ListIterator<String> conditionsIterator = conditions.listIterator();

		while (conditionsIterator.hasNext()) {
			conditionsIterator.set(conditionsIterator.next().replace("_PIPE_", " | "));
		}

		List<String> conditionSettings = null;
		List<String> conditionHooks = null;

		// Settings
		if (conditions.size() > 1) {
			conditionSettings = StringUtils.split(conditions.get(0), ",", 0);
			conditionHooks = StringUtils.split(conditions.get(1), ":", 0);
		}
		// No settings
		else {
			conditionHooks = StringUtils.split(conditions.get(0), ":", 0);
		}

		// Setup condition hook
		String identify = conditionHooks.get(0);
		List<String> argsList = conditionHooks.subList(1, conditionHooks.size());
		String[] args = argsList.toArray(new String[argsList.size()]);

		// Setup settings
		ConditionSettings settings = loadConditionSettings(conditionSettings);

		ConditionHook conditionHook = Condition.get(identify, args);
		conditionHook.setSettings(settings);
		return conditionHook;
	}

	public ConditionSettings loadConditionSettings(List<String> list) {
		ConditionSettings settings = new ConditionSettings();
		if (list == null) {
			return settings;
		}

		for (String s : list) {
			// Shortcut case
			if (s.equals("ENEMY")) {
				settings.setTarget(Target.ENEMY);
				continue;
			}

			List<String> parameters = StringUtils.split(s, "=", 0);

			String key = parameters.get(0);
			String value = parameters.get(1);

			switch (key) {
			case "TARGET":
				settings.setTarget(Target.valueOf(value));
				break;
			case "NEGATIVE":
				settings.setNegative(Boolean.valueOf(value));
				break;
			}
		}

		return settings;
	}

	public Option loadOption(List<String> lines) {
		List<AttributeData> list = new ArrayList<AttributeData>();

		for (String line : lines) {
			if (line.isEmpty()) {
				continue;
			}

			try {
				list.add(loadOptionData(line));
			} catch (Exception e) {
				CustomEnchantmentDebug.warn("Error option at line: " + line);
				e.printStackTrace();
				continue;
			}
		}

		return new Option(list);
	}

	public AttributeData loadOptionData(String line) {
		List<String> parameters = StringUtils.split(line, ":", 0);

		String type = parameters.get(0);

		double amount = Double.valueOf(parameters.get(1));
		if (type.equals(OptionType.DEFENSE)) {
			amount = -amount;
		}
		Operation operation = null;
		try {
			operation = Operation.fromId(Integer.valueOf(parameters.get(2)));
		} catch (Exception e) {
			operation = Operation.valueOf(parameters.get(2));
		}

		return new AttributeData(type, amount, operation);
	}

	public Effect loadEffect(List<String> lines) {
		List<EffectHook> effects = new ArrayList<EffectHook>();

		for (String line : lines) {
			if (line.isEmpty()) {
				continue;
			}

			try {
				effects.add(loadEffectHook(line));
			} catch (Exception e) {
				CustomEnchantmentDebug.warn("Error effect at line: " + line);
				e.printStackTrace();
				continue;
			}
		}

		return new Effect(effects);
	}

	public EffectHook loadEffectHook(String line) {
		line = line.replace("\\ | ", "_PIPE_");

		List<String> effects = StringUtils.split(line, " | ", 0);
		ListIterator<String> effectsIterator = effects.listIterator();

		while (effectsIterator.hasNext()) {
			effectsIterator.set(effectsIterator.next().replace("_PIPE_", " | "));
		}

		List<String> effectSettings = null;
		List<String> effectHooks = null;

		// Settings
		if (effects.size() > 1) {
			effectSettings = StringUtils.split(effects.get(0), ",", 0);
			effectHooks = StringUtils.split(effects.get(1), ":", 0);
		}
		// No settings
		else {
			effectHooks = StringUtils.split(effects.get(0), ":", 0);
		}

		// Setup effect hook
		String identify = effectHooks.get(0);
		List<String> argsList = effectHooks.subList(1, effectHooks.size());
		String[] args = argsList.toArray(new String[argsList.size()]);

		// Setup settings
		EffectSettings settings = loadEffectSettings(effectSettings);

		EffectHook effectHook = Effect.get(identify, args);
		effectHook.setSettings(settings);
		return effectHook;
	}

	public EffectSettings loadEffectSettings(List<String> list) {
		EffectSettings settings = new EffectSettings();
		Target target = Target.PLAYER;
		boolean targetEnable = false;
		double targetMinDistance = 0;
		double targetMaxDistance = Double.MAX_VALUE;
		int targetMinTarget = 0;
		int targetMaxTarget = Integer.MAX_VALUE;
		boolean targetExceptPlayer = true;
		boolean targetExceptEnemy = false;
		if (list == null) {
			TargetFilter targetFilter = new TargetFilter(targetEnable, target, targetExceptPlayer, targetExceptEnemy,
					targetMinDistance, targetMaxDistance, targetMinTarget, targetMaxTarget);
			settings.setTargetFilter(targetFilter);
			return settings;
		}

		for (String s : list) {
			// Shortcut case
			if (s.equals("ENEMY")) {
				settings.setTarget(Target.ENEMY);
				continue;
			}

			List<String> parameters = StringUtils.split(s, "=", 0);

			String key = parameters.get(0);
			String value = parameters.get(1);

			switch (key) {
			case "TARGET":
				settings.setTarget(Target.valueOf(value));
				break;
			case "DELAY":
				settings.setDelay(Long.valueOf(value));
				break;
			case "PERIOD":
				settings.setPeriod(Long.valueOf(value));
				break;
			case "NAME":
				settings.setName(value);
				break;
			case "EFFECT_AFTER_DEAD":
				settings.setEffectAfterDead(Boolean.valueOf(value));
				break;
			case "MIN_DISTANCE":
				targetEnable = true;
				targetMinDistance = Double.valueOf(value);
				break;
			case "MAX_DISTANCE":
				targetEnable = true;
				targetMaxDistance = Double.valueOf(value);
				break;
			case "MIN_TARGET":
				targetEnable = true;
				targetMinTarget = Integer.valueOf(value);
				break;
			case "MAX_TARGET":
				targetEnable = true;
				targetMaxTarget = Integer.valueOf(value);
				break;
			case "EXCEPT_PLAYER":
				targetEnable = true;
				targetExceptPlayer = Boolean.valueOf(value);
				break;
			case "EXCEPT_ENEMY":
				targetEnable = true;
				targetExceptEnemy = Boolean.valueOf(value);
				break;
			case "TARGET_OTHER":
				targetEnable = true;
				settings.setTargetOther(Target.valueOf(value));
				break;
			}
		}
		TargetFilter targetFilter = new TargetFilter(targetEnable, target, targetExceptPlayer, targetExceptEnemy,
				targetMinDistance, targetMaxDistance, targetMinTarget, targetMaxTarget);
		settings.setTargetFilter(targetFilter);

		return settings;
	}
}
