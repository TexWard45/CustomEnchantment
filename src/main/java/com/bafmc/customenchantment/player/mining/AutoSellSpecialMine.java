package com.bafmc.customenchantment.player.mining;

import com.bafmc.topstatistic.TopStatistic;
import com.bafmc.topstatistic.settings.customshop.config.CustomShopSettings;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.TemporaryKey;
import com.bafmc.customenchantment.utils.McMMOUtils;
import com.bafmc.customshop.ShopManager;
import com.bafmc.customshop.shop.ShopItem;
import com.bafmc.customshop.shop.ShopMenuType;
import com.bafmc.customshop.shop.ShopVanillaFactory;
import com.bafmc.bukkit.api.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AutoSellSpecialMine extends AbstractSpecialMine {

	public AutoSellSpecialMine(PlayerSpecialMining playerSpecialMining) {
		super(playerSpecialMining);
	}
	
	public int getPriority() {
		return 10;
	}

	public Boolean isWork(boolean fake) {
		return getPlayerSpecialMining().getCEPlayer().getTemporaryStorage()
				.isBoolean(TemporaryKey.AUTO_SELL_ENABLE);
	}

	public List<ItemStack> getDrops(SpecialMiningData data, List<ItemStack> drops, boolean fake) {
		return doAutoSell(data, drops, fake);
	}

    public void doSpecialMine(SpecialMiningData data, boolean fake) {

    }

    public static List<ItemStack> doAutoSell(SpecialMiningData data, List<ItemStack> drops, boolean fake) {
        if (!Bukkit.getPluginManager().isPluginEnabled("CustomShop")) {
            return drops;
        }
        List<ItemStack> sellDrops = McMMOUtils.getMcMMOBonusDrop(data.getBlock(), drops);

        drops.clear();
        drops.addAll(sellDrops);

        boolean topStatisticEnable = Bukkit.getPluginManager().isPluginEnabled("TopStatistic");

        Player player = data.getPlayer();
        ListIterator<ItemStack> ite = drops.listIterator();

        while(ite.hasNext()) {
            ItemStack itemStack = ite.next();

            ItemStack sellItemStack = doAutoSell(player, itemStack, topStatisticEnable);
            if (sellItemStack == null) {
                continue;
            }

            ite.remove();
        }

        return drops;
    }

    public static ItemStack doAutoSell(Player player, ItemStack itemStack, boolean topStatisticEnable) {
        ShopItem shopItem = ShopVanillaFactory.getShopItem(itemStack.getType());
        if (shopItem == null) {
            return null;
        }

        double price = shopItem.getSellPrice() * itemStack.getAmount();
        if (price <= 0) {
            return null;
        }

        if (topStatisticEnable) {
            updateTopStatistic(player, shopItem, price);
        }

        ShopManager.addTotalSell(shopItem.getName(), shopItem.getSellPrice(), itemStack.getAmount());
        EconomyAPI.giveMoney(player, price);
        return itemStack;
    }

    private static void updateTopStatistic(Player player, ShopItem shopItem, double totalPrice) {
        CustomShopSettings settings = TopStatistic.instance().getSettingsModule().getSettingsConfig().getCustomShopSettings();

        Map<String, CustomShopSettings.Data> pointMap = settings.getPointMap();

        for (String key : pointMap.keySet()) {
            CustomShopSettings.Data data = pointMap.get(key);

            if (!data.getShopMenuTypeWhitelist().contains(ShopMenuType.SELL) && !data.getShopMenuTypeWhitelist().contains(ShopMenuType.SELL_ALL)) {
                continue;
            }

            String shopName = shopItem.getShop().getName();
            if (!data.getShopWhitelist().contains(shopName)) {
                continue;
            }

            TopStatistic.instance().getPlayerModule().getPlayerManager().addPoint(player, key, totalPrice);
        }
    }
}
