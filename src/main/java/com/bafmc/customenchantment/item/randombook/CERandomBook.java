package com.bafmc.customenchantment.item.randombook;

import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentLog;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.item.book.CEBookStorage;
import com.bafmc.customenchantment.menu.tinkerer.TinkererCustomMenu;
import com.bafmc.customenchantment.menu.tinkerer.TinkererReward;
import com.bafmc.customenchantment.menu.tinkerer.TinkererSettings;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CERandomBook extends CEItemUsable<CERandomBookData> {
	private static Map<String, Integer> bookOpen = new HashMap<String, Integer>();

	public CERandomBook(ItemStack itemStack) {
		super(CEItemType.RANDOM_BOOK, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CERandomBook item = (CERandomBook) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.RANDOM_BOOK).get(pattern);

		if (item != null) {
			setData(item.getData());
		}
	}

	public ItemStack exportTo(CERandomBookData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CERandomBookData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	public boolean useBy(Player player) {
        if (InventoryUtils.isFullSlot(player, 1)) {
            CustomEnchantmentMessage.send(player, "ce-item." + getType() + ".full");
            return false;
        }

		CEBookStorage bookStorage = (CEBookStorage) CustomEnchantment.instance().getCeItemStorageMap()
				.get(CEItemType.BOOK);
		CEBook ceBook = bookStorage.getCEBook(getData().getFilter().getRandomEnchant());
		ItemStack itemStack = ceBook.exportTo();

		CEEnchantSimple ceEnchantSimple = ceBook.getData().getCESimple();

        if (CustomEnchantment.instance().getConfig().getBoolean("log.random-book.enable")) {
            ApplyReason reason = new ApplyReason("SUCCESS", ApplyResult.SUCCESS);
            reason.setWriteLogs(true);
            reason.setPlayer(player);
            reason.setCEItem1(this);
            reason.putData("pattern", getData().getPattern());
            reason.putData("enchant", ceEnchantSimple.getName());
            reason.putData("level", ceEnchantSimple.getLevel());
            reason.putData("success", ceEnchantSimple.getSuccess().getValue());
            reason.putData("destroy", ceEnchantSimple.getDestroy().getValue());
            reason.putData("world", player.getLocation().getWorld().getName());
            reason.putData("x", (int) player.getLocation().getX());
            reason.putData("y", (int) player.getLocation().getY());
            reason.putData("z", (int) player.getLocation().getZ());
            CustomEnchantmentLog.writeItemActionLogs(reason);
        }

		Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceEnchantSimple);

		if (CERandomBookPlayerFilter.isFilter(player)) {
			if (CERandomBookPlayerFilter.isFilter(player, ceEnchantSimple.getName())) {
				InventoryUtils.addItem(player, Arrays.asList(itemStack));
				CustomEnchantmentMessage.send(player, "ce-item." + getType() + ".success", placeholder);
			} else {
				TinkererSettings settings = TinkererCustomMenu.getSettings();
				TinkererReward reward = settings.getReward(ceBook);
				if (reward != null) {
					reward.getExecute().execute(player);
					CustomEnchantmentMessage.send(player, "ce-item." + getType() + ".success-tinker", placeholder);
				} else {
					InventoryUtils.addItem(player, Arrays.asList(itemStack));
					CustomEnchantmentMessage.send(player, "ce-item." + getType() + ".success", placeholder);
				}
			}
		}else {
			InventoryUtils.addItem(player, Arrays.asList(itemStack));
			CustomEnchantmentMessage.send(player, "ce-item." + getType() + ".success", placeholder);

			bookOpen.put(player.getName(), bookOpen.getOrDefault(player.getName(), 0) + 1);

			if (bookOpen.get(player.getName()) % 10 == 0) {
				CustomEnchantmentMessage.send(player, "command.cefilter.notify");
			}
		}
        return true;
	}

}
