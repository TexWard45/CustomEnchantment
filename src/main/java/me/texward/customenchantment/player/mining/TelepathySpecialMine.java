package me.texward.customenchantment.player.mining;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.player.PlayerSpecialMining;
import me.texward.customenchantment.player.TemporaryKey;
import me.texward.texwardlib.util.InventoryUtils;

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

	public List<ItemStack> getDrops(List<ItemStack> drops, boolean fake) {
		InventoryUtils.addItem(getPlayerSpecialMining().getPlayer(), drops);
		drops.clear();
		return drops;
	}

	public void doSpecialMine(SpecialMiningData data, boolean fake) {
		
	}
}
