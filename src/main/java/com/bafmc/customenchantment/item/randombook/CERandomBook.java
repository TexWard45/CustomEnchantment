package com.bafmc.customenchantment.item.randombook;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.item.book.CEBookStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentLog;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CERandomBook extends CEItemUsable<CERandomBookData> {

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
		Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceEnchantSimple);
		CustomEnchantmentMessage.send(player, "ce-item." + getType() + ".success", placeholder);

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

		InventoryUtils.addItem(player, Arrays.asList(itemStack));
        return true;
	}

}
