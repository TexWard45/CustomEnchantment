package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.LocationUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.guard.Guard;
import com.bafmc.customenchantment.guard.PlayerGuard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftVex;
import org.bukkit.entity.*;
import org.bukkit.entity.Panda.Gene;

public class EffectSummonCustomGuard extends EffectHook {
	protected String name;
	protected double damage;
	protected boolean suicide;
	protected EntityType entityType;
	protected String locationFormat;
	protected double speed = 1.5;
	protected double playerRange = 16;
	protected double attackRange = 16;
	protected long aliveTime = 15000;
	protected Gene pandaGene;
	protected Boolean vexCharge;
	protected Material vexHold;

	public String getIdentify() {
		return "SUMMON_CUSTOM_GUARD";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		for (String arg : args[0].split(",")) {
			String key = arg.split("=")[0];
			String value = arg.split("=")[1];

			switch (key) {
			case "NAME":
				this.name = value;
				continue;
			case "LOCATION":
				this.locationFormat = value;
				continue;
			case "ENTITY_TYPE":
				this.entityType = EntityType.valueOf(value);
				continue;
			case "SPEED":
				this.speed = Double.parseDouble(value);
				continue;
			case "PLAYER_RANGE":
				this.playerRange = Double.parseDouble(value);
				continue;
			case "ATTACK_RANGE":
				this.attackRange = Double.parseDouble(value);
				continue;
			case "ALIVE_TIME":
				this.aliveTime = Long.parseLong(value);
				continue;
			case "SUICIDE":
				this.suicide = Boolean.valueOf(value);
				continue;
			case "DAMAGE":
				this.damage = Double.parseDouble(value);
				continue;
			case "PANDA_GENE":
				this.pandaGene = Gene.valueOf(value);
				continue;
			case "VEX_CHARGE":
				this.vexCharge = Boolean.valueOf(value);
				continue;
			case "VEX_HOLD":
				this.vexHold = Material.valueOf(value);
				continue;
			}
		}
	}

	public void execute(CEFunctionData data) {
		summon(data);
	}

	public Entity summon(CEFunctionData data) {
		Player player = data.getPlayer();

		if (player == null) {
			return null;
		}

		PlayerGuard playerGuard = CustomEnchantment.instance().getGuardModule().getGuardManager().getPlayerGuard(player);

		String name = this.name.replace("%player%", player.getName()).replace("%random_id%",
				"" + System.nanoTime());

		if (playerGuard.containsGuardName(name)) {
			return null;
		}

		Guard guard = new Guard(playerGuard, name, player.getName(), entityType, playerRange, attackRange, aliveTime);
		guard.setDamage(damage);
		guard.setSuicide(suicide);

		LocationFormat locationFormat = new LocationFormat(this.locationFormat);

		Location location = locationFormat.getLocation(player, null);
		location = LocationUtils.getLegitLocation(player.getLocation(), location);

		Entity entity = guard.summon(location, speed);

		if (pandaGene != null && entity instanceof Panda) {
			((Panda) entity).setMainGene(pandaGene);
			((Panda) entity).setHiddenGene(pandaGene);
		}
		
		if (vexHold != null && entity instanceof Vex) {
			ItemStack itemStack = new ItemStack(Items.IRON_SWORD);
			switch(vexHold) {
			case DIAMOND_SWORD:
				itemStack = new ItemStack(Items.DIAMOND_SWORD);
				break;
			case GOLDEN_SWORD:
				itemStack = new ItemStack(Items.GOLDEN_SWORD);
				break;
			case STONE_SWORD:
				itemStack = new ItemStack(Items.STONE_SWORD);
				break;
			case IRON_SWORD:
				itemStack = new ItemStack(Items.IRON_SWORD);
				break;
			case NETHERITE_SWORD:
				itemStack = new ItemStack(Items.NETHERITE_SWORD);
				break;
			case WOODEN_SWORD:
				itemStack = new ItemStack(Items.WOODEN_SWORD);
				break;
			}
			((CraftVex) entity).getHandle().setItemSlot(EquipmentSlot.MAINHAND, itemStack);
		}
		
		if (vexCharge != null && entity instanceof Vex) {
			((Vex) entity).setCharging(vexCharge);
		}

		playerGuard.addGuard(guard);
		return entity;
	}
}
