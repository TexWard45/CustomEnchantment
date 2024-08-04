package me.texward.customenchantment.listener;

import java.util.*;

import com._3fmc.bukkit.utils.LocationUtils;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import me.texward.customenchantment.player.mining.AutoSellSpecialMine;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.MaterialData;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.bonus.BlockBonus;
import me.texward.customenchantment.task.BlockTask;
import me.texward.texwardlib.api.EconomyAPI;
import me.texward.texwardlib.util.InventoryUtils;
import me.texward.texwardlib.util.ItemStackUtils;

public class BlockListener implements Listener {
	private CustomEnchantment plugin;
	private BlockTask blockTask;

	public BlockListener(CustomEnchantment plugin) {
		this.plugin = plugin;
		this.blockTask = plugin.getBlockTask();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockFromTo(BlockFromToEvent e) {
		Block toBlock = e.getToBlock();

		if (blockTask.contains(toBlock.getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		blockTask.remove(e.getBlock().getLocation());

		Player player = e.getPlayer();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		Block block = e.getBlock();
		MaterialData materialNMS = new MaterialData(block);

		ItemStack itemStack = player.getItemInHand();
		if (itemStack == null) {
			return;
		}

		int silkTouch = itemStack.getEnchantmentLevel(Enchantment.SILK_TOUCH);
		if (silkTouch > 0) {
			return;
		}

		BlockBonus xpBonus = cePlayer.getBlockBonus().getExpBonus();
		if (!xpBonus.isEmpty()) {
			e.setExpToDrop(e.getExpToDrop() + (int) xpBonus.getBonus(materialNMS));
		}

		BlockBonus mBonus = cePlayer.getBlockBonus().getMoneyBonus();
		if (!mBonus.isEmpty()) {
			EconomyAPI.giveMoney(player, mBonus.getBonus(materialNMS));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockDropItemEvent e) {
		Player player = e.getPlayer();

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getSpecialMining().onMining(e);
	}

	@EventHandler(ignoreCancelled = true)
	public void onSugarCaneBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
		if (block.getType() != Material.SUGAR_CANE) {
			return;
		}
        Player player = e.getPlayer();
		Location location = block.getLocation();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        boolean telepathy = cePlayer.getSpecialMining().getTelepathySpecialMine().isWork(true);
        boolean autosell = cePlayer.getSpecialMining().getAutoSellSpecialMine().isWork(true);

        if (!telepathy && !autosell) {
            return;
        }

        int count = 0;

        for (int i = 1; i <= 2; i++) {
            location.setY(location.getY() + 1);
            block = location.getBlock();
            if (block.getType() == Material.SUGAR_CANE) {
                block.setType(Material.AIR);
                count++;
            }
        }

        if (count != 0) {
            if (autosell) {
                AutoSellSpecialMine.doAutoSell(player, new ItemStack(Material.SUGAR_CANE, count + 1), true);
            }

            if (!autosell) {
                InventoryUtils.addItem(player, new ItemStack(Material.SUGAR_CANE, count + 1));
            }
        }
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreakDropItemMornitor(BlockDropItemEvent e) {
		Player player = e.getPlayer();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		if (cePlayer.getSpecialMining().getTelepathySpecialMine().isWork(true)) {
			InventoryUtils.addItem(player, ItemStackUtils.getItemStacks(e.getItems()));
			e.getItems().clear();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent e) {
		ListIterator<Block> blocks = e.blockList().listIterator();
		while (blocks.hasNext()) {
			Block block = blocks.next();
			if (blockTask.contains(block.getLocation())) {
				blocks.remove();
				block.setType(Material.AIR);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockExplode(EntityExplodeEvent e) {
		ListIterator<Block> blocks = e.blockList().listIterator();
		while (blocks.hasNext()) {
			Block block = blocks.next();
			if (blockTask.contains(block.getLocation())) {
				blocks.remove();
				block.setType(Material.AIR);
			}
		}
	}
}
