package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.IConfigurationLoader;
import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.Path;
import com.bafmc.bukkit.config.annotation.ValueType;
import com.bafmc.customenchantment.api.EntityTypeList;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.config.data.ExtraSlotSettingsData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.sigil.CESigil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MainConfig implements IConfigurationLoader {
	@Path("material-group")
	@ValueType(List.class)
	private Map<String, List<String>> materialGroupStringList = new LinkedHashMap<>();
	@Path("entity-group")
	@ValueType(List.class)
	private Map<String, List<String>> entityGroupStringList = new LinkedHashMap<>();
	@Path("ce-item-material-whitelist")
	private List<String> ceItemMaterialWhitelist = new ArrayList<>();
	@Path("move-event-period")
	@Getter
	private long moveEventPeriod = 1000;
	@Path("sneak-event-period")
	@Getter
	private long sneakEventPeriod = 250;
	@Path("enchant-disable-worlds")
	@Getter
	private List<String> enchantDisableWorlds = new ArrayList<>();
	@Path("max-extra-slot-use-count")
	@Getter
	private int maxExtraSlotUseCount = 3;
	@Path("combat-time")
	@Getter
	private int combatTime = 10000;
	@Path("unbreakable-armor.enable")
	@Getter
	private boolean unbreakableArmorEnable = true;
	@Path("unbreakable-armor.player-per-tick")
	@Getter
	private int unbreakableArmorPlayerPerTick = 1;
	@Path("unbreakable-armor.tick-interval")
	@Getter
	private int unbreakableArmorTickInterval = 20;
	@Path("effect-type-blacklist")
	@Getter
	private List<String> effectTypeBlacklist = new ArrayList<>();
	@Path("extra-slot-settings")
	@ValueType(ExtraSlotSettingsData.class)
	private Map<String, ExtraSlotSettingsData> extraSlotSettingMap = new LinkedHashMap<>();
	@Path("combat-settings.require-weapon")
	@Getter
	private boolean combatSettingsRequireWeapon = true;

	@Override
	public void loadConfig(String s, ConfigurationSection configurationSection) {
		for (String key : materialGroupStringList.keySet()) {
			MaterialList.defineMaterialList(key, MaterialList.getMaterialList(materialGroupStringList.get(key)));
		}

		for (String key : entityGroupStringList.keySet()) {
			EntityTypeList.defineEntityTypeList(key, EntityTypeList.getEntityTypeList(entityGroupStringList.get(key)));
		}

		CEWeapon.setWhitelist(new MaterialList(MaterialList.getMaterialList(ceItemMaterialWhitelist)));
	}

	public boolean isEnchantDisableLocation(Location location) {
		return enchantDisableWorlds.contains(location.getWorld().getName());
	}

	public boolean isEnchantDisableLocation(World world) {
		return enchantDisableWorlds.contains(world.getName());
	}

	public boolean isEnchantDisableLocation(String world) {
		return enchantDisableWorlds.contains(world);
	}

	public ExtraSlotSettingsData getExtraSlotSettings(CEItem ceItem) {
		String id = null;
		if (ceItem instanceof CEArtifact ceArtifact) {
			id = "artifact " + ceArtifact.getData().getConfigData().getGroup();
		}

		if (ceItem instanceof CESigil ceSigil) {
			id = "sigil " + ceSigil.getData().getPattern();
		}

		if (id == null) {
			return null;
		}

		for (String key : extraSlotSettingMap.keySet()) {
			List<String> list = extraSlotSettingMap.get(key).getList();
			if (list.contains(id)) {
				return extraSlotSettingMap.get(key);
			}
			if (id.startsWith("artifact ") && list.contains("artifact")) {
				return extraSlotSettingMap.get(key);
			}
			if (id.startsWith("sigil ") && list.contains("sigil")) {
				return extraSlotSettingMap.get(key);
			}
		}

		return null;
	}

	public Map<String, ExtraSlotSettingsData> getExtraSlotSettingMap() {
		return new LinkedHashMap<>(extraSlotSettingMap);
	}
}
