package me.texward.customenchantment.player.mining;

import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import me.texward.customenchantment.player.PlayerSpecialMining;
import me.texward.customenchantment.player.TemporaryKey;
import me.texward.customenchantment.utils.McMMOUtils;
import me.texward.texwardlib.util.InventoryUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
