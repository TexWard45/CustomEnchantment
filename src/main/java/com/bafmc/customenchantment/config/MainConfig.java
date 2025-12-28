package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.IConfigurationLoader;
import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.Path;
import com.bafmc.bukkit.config.annotation.ValueType;
import com.bafmc.bukkit.feature.placeholder.PlaceholderManager;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.api.EntityTypeList;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.api.ParticleAPI;
import com.bafmc.customenchantment.config.data.ExtraSlotSettingsData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponFactory;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.sigil.CESigil;
import lombok.Getter;
import net.minecraft.core.particles.ParticleOptions;
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
	@Path("event.move-period")
	@Getter
	private long moveEventPeriod = 1000;
	@Path("event.sneak-period")
	@Getter
	private long sneakEventPeriod = 250;
	@Path("max-extra-slot-use-count")
	@Getter
	private int maxExtraSlotUseCount = 3;
	@Path("combat.time")
	@Getter
	private int combatTime = 10000;
	@Path("combat.staff-min-required-attack-strength-scale")
	@Getter
	private double combatStaffMinRequiredAttackStrengthScale = 0.5;
	@Path("combat.require-weapon")
	@Getter
	private boolean combatSettingsRequireWeapon = true;
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
	@Path("enchant.disable-worlds")
	@Getter
	private List<String> enchantDisableWorlds = new ArrayList<>();
	@Path("enchant.value")
	private AdvancedConfigurationSection enchantValueConfig = new AdvancedConfigurationSection();
	@Path("sigil.extra-slot")
	@Getter
	private EquipSlot sigilExtraSlot = EquipSlot.EXTRA_SLOT_8;
	@Path("sigil.display-enable")
	@Getter
	private boolean sigilDisplayEnable;
	@Path("outfit.extra-slot")
	@Getter
	private EquipSlot outfitExtraSlot = EquipSlot.EXTRA_SLOT_9;
	@Path("outfit.task.per-player")
	@Getter
	private int outfitTaskPerPlayer = 5;
	@Path("outfit.task.per-item")
	@Getter
	private int outfitTaskPerItem = 10;
	@Path("outfit.title-update-blacklist")
	@Getter
	private List<String> outfitTitleUpdateBlacklist = new ArrayList<>();
	@Path("staff-color")
	@Getter
	private String staffColor = "#FFCC00";
	@Getter
	private List<ParticleOptions> staffParticles;

	@Override
	public void loadConfig(String s, ConfigurationSection config) {
		staffParticles = ParticleAPI.getDustColor(staffColor, 1.0f);

		for (String key : materialGroupStringList.keySet()) {
			MaterialList.defineMaterialList(key, MaterialList.getMaterialList(materialGroupStringList.get(key)));
		}

		for (String key : entityGroupStringList.keySet()) {
			EntityTypeList.defineEntityTypeList(key, EntityTypeList.getEntityTypeList(entityGroupStringList.get(key)));
		}

		CEWeaponFactory.setWhitelist(new MaterialList(MaterialList.getMaterialList(ceItemMaterialWhitelist)));

		PlaceholderManager.getInstance().registerPlaceholder("ce", config.getConfigurationSection("enchant.value"));
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

		if (ceItem instanceof CEOutfit ceOutfit) {
			id = "outfit " + ceOutfit.getData().getPattern();
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
			if (id.startsWith("outfit ") && list.contains("outfit")) {
				return extraSlotSettingMap.get(key);
			}
		}

		return null;
	}

	public Map<String, ExtraSlotSettingsData> getExtraSlotSettingMap() {
		return new LinkedHashMap<>(extraSlotSettingMap);
	}
}
