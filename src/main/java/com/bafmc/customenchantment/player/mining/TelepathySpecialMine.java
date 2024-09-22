package com.bafmc.customenchantment.player.mining;

import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.TemporaryKey;
import com.bafmc.customenchantment.utils.McMMOUtils;
import com.bafmc.bukkit.utils.InventoryUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TelepathySpecialMine extends AbstractSpecialMine {

	public TelepathySpecialMine(PlayerSpecialMining playerSpecialMining) {
		super(playerSpecialMining);
	}
	
	public int getPriority() {
		return 20;
	}

	public Boolean isWork(boolean fake) {
		return getPlayerSpecialMining().getCEPlayer().getTemporaryStorage()
				.isBoolean(TemporaryKey.MINING_TELEPATHY_ENABLE);
	}

	public List<ItemStack> getDrops(SpecialMiningData data, List<ItemStack> drops, boolean fake) {
        List<ItemStack> sellDrops = McMMOUtils.getMcMMOBonusDrop(data.getBlock(), drops);

		InventoryUtils.addItem(getPlayerSpecialMining().getPlayer(), sellDrops);
		drops.clear();
		return drops;
	}

	public void doSpecialMine(SpecialMiningData data, boolean fake) {
	}
}
