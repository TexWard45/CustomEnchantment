package me.texward.customenchantment.player.mining;

import com._3fmc.topstatistic.TopStatistic;
import com._3fmc.topstatistic.settings.customshop.config.CustomShopSettings;
import me.texward.customenchantment.player.PlayerSpecialMining;
import me.texward.customenchantment.player.TemporaryKey;
import me.texward.customshop.shop.ShopItem;
import me.texward.customshop.shop.ShopMenuType;
import me.texward.customshop.shop.ShopVanillaFactory;
import me.texward.texwardlib.api.EconomyAPI;
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

	public List<ItemStack> getDrops(List<ItemStack> drops, boolean fake) {
		return doAutoSell(getPlayerSpecialMining().getPlayer(), drops, fake);
	}

	public void doSpecialMine(SpecialMiningData data, boolean fake) {

	}

    public static List<ItemStack> doAutoSell(Player player, List<ItemStack> drops, boolean fake) {
        if (!Bukkit.getPluginManager().isPluginEnabled("CustomShop")) {
            return drops;
        }

        boolean topStatisticEnable = Bukkit.getPluginManager().isPluginEnabled("TopStatistic");

        ListIterator<ItemStack> ite = drops.listIterator();

        while(ite.hasNext()) {
            ItemStack next = ite.next();

            ShopItem shopItem = ShopVanillaFactory.getShopItem(next.getType());

            if (shopItem == null) {
                continue;
            }

            double price = shopItem.getSellPrice() * next.getAmount();
            if (price <= 0) {
                continue;
            }

            if (topStatisticEnable) {
                updateTopStatistic(player, shopItem, price);
            }

            EconomyAPI.giveMoney(player, price);
            ite.remove();
        }

        return drops;
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
