package com.bafmc.customenchantment.custommenu;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.utils.NumberUtils;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CEEnchantMap;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.custommenu.menu.Catalog;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CEBookCatalog extends Catalog {
	private List<CEBookData> list = new ArrayList<CEBookData>();

	public CEBookCatalog() {
		super("cebook");
	}

	public class CEBookData {
		public CEEnchant ceEnchant;
        public String display;
		public String displayLevel1;
		public String displayAllLevel;
		public String description;
		public String detailDescription;
		public String randomDescription;

		public CEBookData(CEEnchant ceEnchant, String display, String displayLevel1, String displayAllLevel, String description, String randomDescription) {
			this.ceEnchant = ceEnchant;
            this.display = display;
			this.displayLevel1 = displayLevel1;
			this.displayAllLevel = displayAllLevel;
			this.description = description;
			this.randomDescription = randomDescription;
		}

	}

	public void setup(AdvancedConfigurationSection config) {
		List<String> applies = config.getStringList("applies");
		List<String> groups = config.getStringList("groups");
		List<String> ceBlacklist = config.getStringList("ce-blacklist");

		this.list = new ArrayList<CEBookData>();
		CEEnchantMap map = CustomEnchantment.instance().getCeEnchantMap();

		List<String> ceNameList = new ArrayList<String>();
		for (CEEnchant enchant : map.values()) {
			if (!applies.isEmpty() && !applies.contains(enchant.getCEDisplay().getAppliesDescription().get(0))) {
				continue;
			}

			if (!groups.isEmpty() && !groups.contains(enchant.getGroupName())) {
				continue;
			}
			
			if (ceBlacklist.contains(enchant.getName())) {
				continue;
			}

			ceNameList.add(enchant.getName());
		}

		Collections.sort(ceNameList);

		String ceBookType = config.getString("ce-book-type", "default");
		for (String ceName : ceNameList) {
			CEEnchant enchant = CEAPI.getCEEnchant(ceName);
			
			ItemStack itemStack = CEAPI.getCEBookItemStack(ceBookType, new CEEnchantSimple(enchant.getName(), 1, 100, 0));
			ItemMeta meta = itemStack.getItemMeta();
            String originDisplay = meta.getDisplayName();
			String display = originDisplay;

			String displayLevel1 = display;

			if (enchant.getMaxLevel() > 1) {
				display += "-" + NumberUtils.toRomanNumber(enchant.getMaxLevel());
			}
			String displayAllLevel = display;
			String description = StringUtils.toString(meta.getLore());
			String randomDescription = StringUtils.toString(meta.getLore()).replace("100%", "?%").replace("0%", "?%");

			meta.setDisplayName(display);
			itemStack.setItemMeta(meta);

			list.add(new CEBookData(enchant, originDisplay, displayLevel1, displayAllLevel, description, randomDescription));
		}
	}

	public HashMap<String, String> getPlaceholder(Player player, int index) {
		CEBookData data = list.get(index);

		HashMap<String, String> map = new HashMap<String, String>();
		if (data == null) {
			return map;
		}
		CEEnchant enchant = data.ceEnchant;
        map.put("display", data.display);
		map.put("display_all_level", data.displayAllLevel);
		map.put("display_level_1", data.displayLevel1);
		map.put("description", data.description);
		map.put("random_description", data.randomDescription);
		map.put("name", enchant.getName());
		map.put("citem_name", enchant.getName());
		return map;
	}

	public int getSize() {
		return list.size();
	}

}
