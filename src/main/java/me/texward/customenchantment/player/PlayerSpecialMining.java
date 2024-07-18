package me.texward.customenchantment.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.player.mining.*;
import me.texward.customenchantment.task.SpecialMiningTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityPlayer;

public class PlayerSpecialMining extends CEPlayerExpansion {
	private Map<Class<? extends AbstractSpecialMine>, AbstractSpecialMine> map;
	private SpecialMiningData fakeMiningData;

	public PlayerSpecialMining(CEPlayer cePlayer) {
		super(cePlayer);
		this.map = new LinkedHashMap<>();
		
		PlayerSpecialMiningRegister.setup(this);
	}

	public void addExpantion(AbstractSpecialMine expansion) {
		map.put(expansion.getClass(), expansion);
	}

	public void onJoin() {
	}

	public void onQuit() {
	}

	public void setFakeMiningData(SpecialMiningData data) {
		this.fakeMiningData = data;
	}

	public SpecialMiningData getMiningData() {
		return fakeMiningData;
	}

	public boolean hasFakeMiningData() {
		return fakeMiningData != null;
	}

	public void onMining(BlockDropItemEvent e) {
		boolean fake = hasFakeMiningData();
		onMining(e, fake);
	}

	public void onMining(BlockDropItemEvent e, boolean fake) {
		Player player = e.getPlayer();

		Block block = e.getBlock();
		ItemStack itemStack = player.getItemInHand();

		List<Class<? extends AbstractSpecialMine>> workMap = getWorkSpecialMining(fake);
        if (workMap.isEmpty()) {
            return;
        }

		List<ItemStack> drops = toItemStackList(e.getItems());
		drops = getDrops(drops, workMap, fake);
        drops = optimizeItemStacks(drops);

        e.getItems().clear();
        for (ItemStack droppedItemStack : drops) {
            Item item = block.getWorld().dropItemNaturally(block.getLocation(), droppedItemStack);
            e.getItems().add(item);
        }

		SpecialMiningData data = null;
		if (fake) {
			data = getMiningData();
		} else {
			data = new SpecialMiningData(player, block, itemStack);
		}

		doSpecialMine(data, workMap, fake);
	}

    public static List<ItemStack> optimizeItemStacks(List<ItemStack> itemStacks) {
        Map<Material, Integer> itemCountMap = new HashMap<>();

        // Aggregate item counts
        for (ItemStack itemStack : itemStacks) {
            Material material = itemStack.getType();
            int count = itemStack.getAmount();
            itemCountMap.put(material, itemCountMap.getOrDefault(material, 0) + count);
        }

        // Create optimized item stacks
        List<ItemStack> optimizedStacks = new ArrayList<>();
        for (Map.Entry<Material, Integer> entry : itemCountMap.entrySet()) {
            Material material = entry.getKey();
            int totalAmount = entry.getValue();
            int maxStackSize = material.getMaxStackSize();

            while (totalAmount > 0) {
                int stackSize = Math.min(totalAmount, maxStackSize);
                optimizedStacks.add(new ItemStack(material, stackSize));
                totalAmount -= stackSize;
            }
        }

        return optimizedStacks;
    }
	public List<Class<? extends AbstractSpecialMine>> getWorkSpecialMining(boolean fake) {
		SpecialMiningData data = getMiningData();

		List<Class<? extends AbstractSpecialMine>> workMap = new ArrayList<>();
		if (fake) {
			workMap = data.getWorkMap();
		}

		for (Class<? extends AbstractSpecialMine> clazz : this.map.keySet()) {
			AbstractSpecialMine specialMine = this.map.get(clazz);

			Boolean work = specialMine.isWork(fake);

			if (work != null && work) {
				workMap.add(clazz);
			}
		}
		return workMap;
	}

	public List<ItemStack> getDrops(List<ItemStack> drops, List<Class<? extends AbstractSpecialMine>> workMap,
			boolean fake) {
		SpecialMiningData data = getMiningData();

		for (Class<? extends AbstractSpecialMine> clazz : workMap) {
			AbstractSpecialMine specialMine = this.map.get(clazz);

			List<ItemStack> newDrops = specialMine.getDrops(drops, fake);
			if (newDrops == null) {
				continue;
			}

			drops = newDrops;
		}

		if (data != null && !data.isDropItem()) {
			drops.clear();
		}
		
		return drops;
	}

	public void doSpecialMine(SpecialMiningData data, List<Class<? extends AbstractSpecialMine>> workMap,
			boolean fake) {
		for (Class<? extends AbstractSpecialMine> clazz : workMap) {
			AbstractSpecialMine specialMine = this.map.get(clazz);
			specialMine.doSpecialMine(data, fake);
		}
	}

	public List<ItemStack> toItemStackList(List<Item> items) {
		List<ItemStack> list = new ArrayList<ItemStack>();

		for (Item item : items) {
			list.add(item.getItemStack());
		}

		return list;
	}

	public void callFakeBreakBlock(Block block, SpecialMiningData data) {
		setFakeMiningData(data);

		if (data.isDropItem()) {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			entityPlayer.playerInteractManager.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
		} else {
			block.setType(Material.AIR);
		}

		setFakeMiningData(null);
	}

	public AbstractSpecialMine getSpecialMine(Class<? extends AbstractSpecialMine> clazz) {
		return map.get(clazz);
	}

	public BlockDropBonusSpecialMine getBlockDropBonusSpecialMine() {
		return (BlockDropBonusSpecialMine) getSpecialMine(BlockDropBonusSpecialMine.class);
	}

	public ExplosionSpecialMine getExplosionSpecialMine() {
		return (ExplosionSpecialMine) getSpecialMine(ExplosionSpecialMine.class);
	}

	public FurnaceSpecialMine getFurnaceSpecialMine() {
		return (FurnaceSpecialMine) getSpecialMine(FurnaceSpecialMine.class);
	}

	public TelepathySpecialMine getTelepathySpecialMine() {
		return (TelepathySpecialMine) getSpecialMine(TelepathySpecialMine.class);
	}

    public AutoSellSpecialMine getAutoSellSpecialMine() {
        return (AutoSellSpecialMine) getSpecialMine(AutoSellSpecialMine.class);
    }
}
