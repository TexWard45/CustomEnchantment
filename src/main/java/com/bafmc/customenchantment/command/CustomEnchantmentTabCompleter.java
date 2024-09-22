package com.bafmc.customenchantment.command;

public class CustomEnchantmentTabCompleter {

//	private CustomEnchantment plugin;
//
//	public CustomEnchantmentTabCompleter(CustomEnchantment plugin) {
//		this.plugin = plugin;
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2) {
//		if (!sender.hasPermission("customenchantment.tab")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "book", "$name", "$level", "$success", "$destroy")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "book", "$name", "$level", "$success")) {
//			return Arrays.asList("0");
//		}
//
//		if (check("give", "$player", "book", "$name", "$level")) {
//			return Arrays.asList("100");
//		}
//
//		if (check("give", "$player", "book", "$name")) {
//			String name = args("$name");
//
//			CEEnchant enchant = CEAPI.getCEEnchant(name);
//
//			if (enchant != null) {
//				List<String> levels = new ArrayList<String>();
//
//				for (Integer level : enchant.getCELevelMap().keySet()) {
//					levels.add(level.toString());
//				}
//
//				return levels;
//			}
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "book")) {
//			return plugin.getCEEnchantMap().getKeys();
//		}
//
//		if (check("give", "$player", "protectdead", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "protectdead")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.PROTECT_DEAD).getKeys();
//		}
//
//		if (check("give", "$player", "protectdestroy", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "protectdestroy")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.PROTECT_DESTROY).getKeys();
//		}
//
//		if (check("give", "$player", "nametag", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "nametag")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG).getKeys();
//		}
//
//		if (check("give", "$player", "enchantpoint", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "enchantpoint")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.ENCHANT_POINT).getKeys();
//		}
//
//		if (check("give", "$player", "increaseratebook", "$name", "$success", "$destroy")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "increaseratebook", "$name", "$success")) {
//			return Arrays.asList("100");
//		}
//
//		if (check("give", "$player", "increaseratebook", "$name")) {
//			return Arrays.asList("100");
//		}
//
//		if (check("give", "$player", "increaseratebook")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.INCREASE_RATE_BOOK).getKeys();
//		}
//
//		if (check("give", "$player", "randombook", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "randombook")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.RANDOM_BOOK).getKeys();
//		}
//
//		if (check("give", "$player", "voucher", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "voucher")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.VOUCHER).getKeys();
//		}
//
//		if (check("give", "$player", "removeenchant", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "removeenchant")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.REMOVE_ENCHANT).getKeys();
//		}
//
//		if (check("give", "$player", "storage", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "storage")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE).getKeys();
//		}
//
//		if (check("give", "$player", "mask", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("give", "$player", "mask")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.MASK).getKeys();
//		}
//
//		if (check("give", "$player")) {
//			return Arrays.asList("book", "protectdead", "protectdestroy", "nametag", "enchantpoint", "increaseratebook",
//					"randombook", "voucher", "storage", "removeenchant", "mask");
//		}
//
//		if (check("give")) {
//			return null;
//		}
//
//		if (check("use", "$player", "randombook", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("use", "$player", "randombook")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.RANDOM_BOOK).getKeys();
//		}
//
//		if (check("use", "$player", "voucher", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("use", "$player", "voucher")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.VOUCHER).getKeys();
//		}
//
//		if (check("use", "$player")) {
//			return Arrays.asList("randombook", "voucher");
//		}
//
//		if (check("use")) {
//			return null;
//		}
//
//		if (check("addenchant", "$player", "$name", "$level")) {
//			return Arrays.asList("");
//		}
//
//		if (check("addenchant", "$player", "$name")) {
//			String name = args("$name");
//
//			CEEnchant enchant = CEAPI.getCEEnchant(name);
//
//			if (enchant != null) {
//				List<String> levels = new ArrayList<String>();
//
//				for (Integer level : enchant.getCELevelMap().keySet()) {
//					levels.add(level.toString());
//				}
//
//				return levels;
//			}
//			return Arrays.asList("");
//		}
//
//		if (check("addenchant", "$player")) {
//			return plugin.getCEEnchantMap().getKeys();
//		}
//
//		if (check("addenchant")) {
//			return null;
//		}
//
//		if (check("removeenchant", "$player")) {
//			Player player = Bukkit.getPlayer(args("$player"));
//			if (player == null) {
//				return Arrays.asList("");
//			}
//			ItemStack itemStack = player.getItemInHand();
//			if (itemStack == null) {
//				return Arrays.asList("");
//			}
//			CEItem ceItem = CEAPI.getCEItem(itemStack);
//			if (ceItem == null || !(ceItem instanceof CEWeaponAbstract)) {
//				return Arrays.asList("");
//			}
//			CEWeaponAbstract weapon = (CEWeaponAbstract) ceItem;
//			return weapon.getWeaponEnchant().getCESimpleNameList();
//		}
//
//		if (check("removeenchant")) {
//			return null;
//		}
//
//		if (check("open", "$player", "storage")) {
//			CEItemStorage storage = CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE);
//			int maxPage = (int) Math.ceil(storage.size() / 54d);
//
//			List<String> list = new ArrayList<String>();
//			for (int i = 1; i <= maxPage; i++) {
//				list.add("" + i);
//			}
//
//			return list;
//		}
//		
//		if (check("open", "$player", "book")) {
//			return new ArrayList<String>(CustomEnchantment.instance().getCEGroupMap().keySet());
//		}
//
//		if (check("open", "$player")) {
//			return Arrays.asList("storage", "book");
//		}
//
//		if (check("open")) {
//			return null;
//		}
//
//		if (check("additem", "$player", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("additem", "$player")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE).getKeys();
//		}
//
//		if (check("additem")) {
//			return null;
//		}
//
//		if (check("removeitem", "$player", "$name")) {
//			return Arrays.asList("");
//		}
//
//		if (check("removeitem", "$player")) {
//			return CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.STORAGE).getKeys();
//		}
//
//		if (check("removeitem")) {
//			return null;
//		}
//
//		if (check("updateitem", "$player")) {
//			return Arrays.asList("");
//		}
//
//		if (check("updateitem")) {
//			return null;
//		}
//
//		if (check("cleartime", "$player")) {
//			return Arrays.asList("");
//		}
//
//		if (check("cleartime")) {
//			return null;
//		}
//
//		if (check("enablehelmet", "$player")) {
//			return Arrays.asList("");
//		}
//
//		if (check("enablehelmet")) {
//			return null;
//		}
//
//		if (check("disablehelmet", "$player")) {
//			return Arrays.asList("");
//		}
//
//		if (check("disablehelmet")) {
//			return null;
//		}
//
//		if (check("admin", "$player", "$toggle")) {
//			return Arrays.asList("");
//		}
//
//		if (check("admin", "$player")) {
//			return Arrays.asList("yes", "no", "true", "false", "on", "off");
//		}
//
//		if (check("admin")) {
//			return null;
//		}
//
//		if (check("info")) {
//			return null;
//		}
//
//		if (check("reload")) {
//			return Arrays.asList("");
//		}
//
//		return Arrays.asList("reload", "give", "use", "info", "addenchant", "removeenchant", "additem", "removeitem",
//				"updateitem", "admin", "cleartime", "enablehelmet", "disablehelmet", "open");
//	}

}
