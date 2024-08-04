package me.texward.customenchantment.player.mining;

import com._3fmc.topstatistic.TopStatistic;
import com._3fmc.topstatistic.settings.customshop.config.CustomShopSettings;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import me.texward.customenchantment.listener.BlockListener;
import me.texward.customenchantment.player.PlayerSpecialMining;
import me.texward.customenchantment.player.TemporaryKey;
import me.texward.customenchantment.utils.McMMOUtils;
import me.texward.customshop.shop.ShopItem;
import me.texward.customshop.shop.ShopMenuType;
import me.texward.customshop.shop.ShopVanillaFactory;
import me.texward.texwardlib.api.EconomyAPI;
import me.texward.texwardlib.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
