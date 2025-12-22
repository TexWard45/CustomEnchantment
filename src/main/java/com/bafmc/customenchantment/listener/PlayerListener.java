package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.bafframework.event.ItemEquipEvent;
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.MessageUtils;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.CECallerBuilder;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEType;
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.feature.other.DashFeature;
import com.bafmc.customenchantment.feature.other.DoubleJumpFeature;
import com.bafmc.customenchantment.feature.other.FlashFeature;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.skin.CESkin;
import com.bafmc.customenchantment.player.*;
import com.bafmc.customenchantment.player.PlayerAbility.Type;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PlayerListener implements Listener {
	private CustomEnchantment plugin;

	public PlayerListener(CustomEnchantment plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onStatsChange(CEPlayerStatsModifyEvent e) {
		if (e.getChangeValue() == e.getCurrentValue()) {
			return;
		}

		CEPlayer cePlayer = e.getCEPlayer();

		PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

		CustomAttributeType type = e.getStatsType();

		double changeValue = e.getChangeValue();
		double newChangeValue = attribute.getValue(type, changeValue);

		e.setValue(newChangeValue);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.onJoin();

		CECallerBuilder
				.build(player)
				.setCEType(CEType.ARMOR_UNDRESS)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.ARMOR_ARRAY))
				.call();

		CECallerBuilder
				.build(player)
				.setCEType(CEType.CHANGE_HAND)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HAND_ARRAY))
				.call();

		CECallerBuilder
				.build(player)
				.setCEType(CEType.ARMOR_EQUIP)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.ARMOR_ARRAY))
				.call();
		
		CECallerBuilder
				.build(player)
				.setCEType(CEType.HOLD)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HAND_ARRAY))
				.call();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		CECallerBuilder
				.build(player)
				.setCEType(CEType.ARMOR_UNDRESS)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.ARMOR_ARRAY))
				.setExecuteLater(false)
				.call();

		CECallerBuilder
				.build(player)
				.setCEType(CEType.CHANGE_HAND)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HAND_ARRAY))
				.setExecuteLater(false)
				.call();

		cePlayer.onQuit();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChancedWorld(PlayerChangedWorldEvent e) {
		Player player = e.getPlayer();

		if (CustomEnchantment.instance().getMainConfig().isEnchantDisableLocation(player.getLocation())) {
			CECallerBuilder
					.build(player)
					.setCEType(CEType.ARMOR_UNDRESS)
					.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.ARMOR_ARRAY))
					.setExecuteLater(false)
					.call();
			
			CECallerBuilder
					.build(player)
					.setCEType(CEType.CHANGE_HAND)
					.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HAND_ARRAY))
					.setExecuteLater(false)
					.call();
		} else if (CustomEnchantment.instance().getMainConfig().isEnchantDisableLocation(e.getFrom())) {
			CECallerBuilder
					.build(player)
					.setCEType(CEType.ARMOR_EQUIP)
					.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.ARMOR_ARRAY))
					.setExecuteLater(false)
					.call();
			
			CECallerBuilder
					.build(player)
					.setCEType(CEType.HOLD)
					.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HAND_ARRAY))
					.setExecuteLater(false)
					.call();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		Player killer = player.getKiller();

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		if (!cePlayer.isDeathTimeBefore()) {
			cePlayer.setDeathTime(cePlayer.getDeathTime() + 1);
		} else {
			cePlayer.setDeathTimeBefore(false);
		}

		CECallerBuilder.build(player).setCEType(CEType.DEATH).call();
		
		if (killer != null) {
			CECallerBuilder.build(killer).setCEType(CEType.KILL_PLAYER).call();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemEquip(ItemEquipEvent e) {
		Player player = e.getPlayer();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		EquipSlot slot = e.getEquipSlot();
		ItemStack newEquipItemStack = e.getNewItemStack();

		PlayerEquipment equipment = cePlayer.getEquipment();
		CEWeaponAbstract ceOldWeapon = equipment.getSlot(slot);
		CEWeaponAbstract ceNewWeapon = null;
		if (newEquipItemStack != null && newEquipItemStack.getType() != Material.AIR) {
			ceNewWeapon = CEWeaponAbstract.getCEWeapon(newEquipItemStack);
			// Skip calling if skin without weapon is equipped (wings logic)
			if (ceNewWeapon instanceof CESkin skin && skin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON) == null) {
				return;
			}

			cePlayer.getEquipment().setSlot(slot, ceNewWeapon);
		}else {
			cePlayer.getEquipment().setSlot(slot, null);
		}

		// Handle offhand separately (wings logic)
		if (slot == EquipSlot.OFFHAND) {
			cePlayer.getEquipment().setOffhandItemStack(newEquipItemStack);
		}

		// Check if swapping between skin and weapon of same type
		if (ceOldWeapon != null && ceNewWeapon != null) {
			ItemStack oldItemStack = null;
			ItemStack newItemStack = null;

			if (ceNewWeapon instanceof CESkin newWeaponSkin) {
				newItemStack = newWeaponSkin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON);
				oldItemStack = ceOldWeapon.getDefaultItemStack();
			} else if (ceOldWeapon instanceof CESkin oldWeaponSkin) {
				oldItemStack = oldWeaponSkin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON);
				newItemStack = ceNewWeapon.getDefaultItemStack();
			}

			if (oldItemStack != null && newItemStack != null && oldItemStack.equals(newItemStack)) {
				return;
			}
		}

		// Call on old equip
		if (ceOldWeapon != null) {
			Map<EquipSlot, CEWeaponAbstract> map = CEAPI.getCEWeaponMap(player);
			map.put(slot, ceOldWeapon);

			CEType ceType;

			if (slot.isArmor()) {
				ceType = CEType.ARMOR_UNDRESS;
			} else if (slot.isHotbar()){
				ceType = CEType.HOTBAR_CHANGE;
			}else {
				ceType = CEType.CHANGE_HAND;
			}

			if (ceType != CEType.HOTBAR_CHANGE) {
				CECallerBuilder
						.build(player)
						.setCEType(ceType)
						.setWeaponMap(map)
						.setActiveEquipSlot(slot)
						.call();
			}
		}

		// Call on new equip
		if (ceNewWeapon != null) {
			Map<EquipSlot, CEWeaponAbstract> map = CEAPI.getCEWeaponMap(player);
			map.put(slot, ceNewWeapon);
			
			CEType ceType;

			if (slot.isArmor()) {
				ceType = CEType.ARMOR_EQUIP;
			} else if (slot.isHotbar()){
				ceType = CEType.HOTBAR_HOLD;
			}else {
				ceType = CEType.HOLD;
			}

			if (ceType != CEType.HOTBAR_HOLD) {
				CECallerBuilder
						.build(player)
						.setCEType(ceType)
						.setWeaponMap(map)
						.setActiveEquipSlot(slot)
						.call();
			}
		}
	}

	public CEWeaponAbstract getRelativeCEWeapon(CEPlayer cePlayer, EquipSlot slot, ItemStack itemStack) {
		if (slot != EquipSlot.MAINHAND && !slot.isHotbar()) {
			return CEWeaponAbstract.getCEWeapon(itemStack);
		}

		int heldItemSlot = cePlayer.getPlayer().getInventory().getHeldItemSlot();
		if (slot == EquipSlot.MAINHAND) {
			CEWeaponAbstract currentWeapon = cePlayer.getEquipment().getSlot(EquipSlot.getHotbar(heldItemSlot));
			if (currentWeapon != null && currentWeapon.getDefaultItemStack().isSimilar(itemStack)) {
				return currentWeapon;
			}
		}else {
			int hotbarSlot = EquipSlot.getSlot(slot);

			if (hotbarSlot == heldItemSlot) {
				CEWeaponAbstract currentWeapon = cePlayer.getEquipment().getSlot(EquipSlot.MAINHAND);
				if (currentWeapon != null) {
					return currentWeapon;
				}
			}
		}

		return CEWeaponAbstract.getCEWeapon(itemStack);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();

		try {
			CEPlayer cePlayer = CEAPI.getCEPlayer(player);
			Location from = e.getFrom();
			Location to = e.getTo();

			PlayerAbility ability = cePlayer.getAbility();
			if (ability.isCancel(Type.MOVE) && isDifferentLocation(from, to))
				e.setCancelled(true);
			if (ability.isCancel(Type.LOOK) && (from.getYaw() != to.getYaw() || from.getPitch() != to.getPitch()))
				e.setCancelled(true);
			if (ability.isCancel(Type.JUMP)) {
				PotionEffect effect = player.getPotionEffect(PotionEffectType.JUMP_BOOST);
				int level = 0;
				if (effect != null)
					level = effect.getAmplifier() + 1;
				double up = to.getY() - from.getY();
				double min = 0.419D + 0.1D * level;
				double max = 0.421D + 0.1D * level;
				if (up > min && up < max) {
					e.setCancelled(true);
					return;
				}
			}

			if (isDifferentLocation(from, to)) {
				callMove(cePlayer, from, to);
			}
		} catch (Exception ex) {
			System.out.println("Error Move at player " + player);
			ex.printStackTrace();
		}
	}

	public boolean isDifferentLocation(Location from, Location to) {
		return roundToOneDecimal(from.getX()) != roundToOneDecimal(to.getX())
				|| roundToOneDecimal(from.getY()) != roundToOneDecimal(to.getY())
				|| roundToOneDecimal(from.getZ()) != roundToOneDecimal(to.getZ());
	}

	private double roundToOneDecimal(double value) {
		return Math.floor(value * 10) / 10.0;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCEItemUsable(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.HAND
				&& (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& e.getItem() != null) {
			ItemStack itemStack = e.getItem();

			CEItem ceItem = CEAPI.getCEItem(itemStack);
			if (ceItem == null || !(ceItem instanceof CEItemUsable)) {
				return;
			}

			Player player = e.getPlayer();
			CEItemUsable usable = (CEItemUsable) ceItem;
			if (usable.useBy(player)) {
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    player.getInventory().setItemInHand(ItemStackUtils.getItemStack(itemStack, itemStack.getAmount() - 1));
                }
            }
		}
	}

	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStack = e.getItem().clone();

		CEItem ceItem = CEAPI.getCEItem(itemStack);
		if (ceItem instanceof VanillaItem) {
			e.setCancelled(true);
			return;
		}

		CEFunctionData data = new CEFunctionData(player);
		data.setItemConsume(itemStack);

		CECallerBuilder
				.build(player)
				.setCEType(CEType.ITEM_CONSUME)
				.setCEFunctionData(data)
				.call();
	}

	public void callMove(CEPlayer cePlayer, Location from, Location to) {
        cePlayer.getTemporaryStorage().set(TemporaryKey.IN_AIR, from.getY() != to.getY());
		cePlayer.getTemporaryStorage().set(TemporaryKey.LAST_MOVE_TIME, System.currentTimeMillis());

		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
		long lastCallMoveTime = storage.getLong(TemporaryKey.LAST_CALL_MOVE_TIME, 0);

		if (System.currentTimeMillis() - lastCallMoveTime > CustomEnchantment.instance().getMainConfig().getMoveEventPeriod()) {
			CECallerBuilder
					.build(cePlayer.getPlayer())
					.setCEType(CEType.MOVE)
					.call();
			
			storage.set(TemporaryKey.LAST_CALL_MOVE_TIME, System.currentTimeMillis());
		}
	}

    @EventHandler
    public void onPlayerSneakEvent(PlayerToggleSneakEvent e) {
        if (!e.isSneaking() || e.getPlayer().isFlying()) {
            return;
        }

        handleDash(e.getPlayer());
        handleJump(e.getPlayer());
		handleFlash(e.getPlayer());
		callSneak(e.getPlayer());
    }

	public void callSneak(Player player) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		cePlayer.getTemporaryStorage().set(TemporaryKey.LAST_SNEAK_TIME, System.currentTimeMillis());

		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
		long lastCallSneakTime = storage.getLong(TemporaryKey.LAST_CALL_SNEAK_TIME, 0);

		if (System.currentTimeMillis() - lastCallSneakTime > CustomEnchantment.instance().getMainConfig().getSneakEventPeriod()) {
			CECallerBuilder
					.build(cePlayer.getPlayer())
					.setCEType(CEType.SNEAK)
					.call();

			storage.set(TemporaryKey.LAST_CALL_SNEAK_TIME, System.currentTimeMillis());
		}
	}

    public void handleDash(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        PlayerAbility ability = cePlayer.getAbility();
        if (ability.isCancel(Type.MOVE)) {
            return;
        }

        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        if (!storage.isBoolean(TemporaryKey.DASH_ENABLE)) {
            return;
        }

        long lastMoveTime = storage.getLong(TemporaryKey.LAST_MOVE_TIME);
        if (System.currentTimeMillis() - lastMoveTime > 1000) {
            return;
        }

        double dashCooldown = storage.getDouble(TemporaryKey.DASH_COOLDOWN);
        long currentTime = System.currentTimeMillis();
        long dashLastUse = storage.getLong(TemporaryKey.DASH_LAST_USE);
        if (System.currentTimeMillis() - dashLastUse < dashCooldown) {
            String dashCooldownMessage = storage.getString(TemporaryKey.DASH_COOLDOWN_MESSAGE);
            double timeLeft = (dashCooldown - (currentTime - dashLastUse)) / 1000d;

            Placeholder placeholder = PlaceholderBuilder.builder().put("{time_left}", StringUtils.formatNumber(timeLeft)).build();
            dashCooldownMessage = placeholder.apply(dashCooldownMessage);

            MessageUtils.send(player, dashCooldownMessage);
            return;
        }

        double power = storage.getDouble(TemporaryKey.DASH_POWER);
        String particle = storage.getString(TemporaryKey.DASH_PARTICLE);

        DashFeature.dash(player, power, particle);
        storage.set(TemporaryKey.DASH_LAST_USE, currentTime);
    }

    public void handleJump(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        PlayerAbility ability = cePlayer.getAbility();
        if (ability.isCancel(Type.JUMP)) {
            return;
        }

        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        if (!storage.isBoolean(TemporaryKey.DOUBLE_JUMP_ENABLE)) {
            return;
        }

        long lastMoveTime = storage.getLong(TemporaryKey.LAST_MOVE_TIME);
        if (System.currentTimeMillis() - lastMoveTime > 1000) {
            return;
        }


        Block downBlock = player.getLocation().getBlock();
        if (downBlock.isLiquid()) {
            return;
        }

        if (!storage.getBoolean(TemporaryKey.IN_AIR)) {
            return;
        }

        double doubleJumpCooldown = storage.getDouble(TemporaryKey.DOUBLE_JUMP_COOLDOWN);
        long currentTime = System.currentTimeMillis();
        long doubleJumpLastUse = storage.getLong(TemporaryKey.DOUBLE_JUMP_LAST_USE);
        if (System.currentTimeMillis() - doubleJumpLastUse < doubleJumpCooldown) {
            String doubleJumpCooldownMessage = storage.getString(TemporaryKey.DOUBLE_JUMP_COOLDOWN_MESSAGE);
            double timeLeft = (doubleJumpCooldown - (currentTime - doubleJumpLastUse)) / 1000d;

            Placeholder placeholder = PlaceholderBuilder.builder().put("{time_left}", StringUtils.formatNumber(timeLeft)).build();
            doubleJumpCooldownMessage = placeholder.apply(doubleJumpCooldownMessage);

            MessageUtils.send(player, doubleJumpCooldownMessage);
            return;
        }

        double power = storage.getDouble(TemporaryKey.DOUBLE_JUMP_POWER);
        String particle = storage.getString(TemporaryKey.DOUBLE_JUMP_PARTICLE);

        DoubleJumpFeature.jump(player, power, particle);
        storage.set(TemporaryKey.DOUBLE_JUMP_LAST_USE, currentTime);
    }

	public void handleFlash(Player player) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
		if (!storage.isBoolean(TemporaryKey.FLASH_ENABLE)) {
			return;
		}

		long lastMoveTime = storage.getLong(TemporaryKey.LAST_MOVE_TIME);
		if (System.currentTimeMillis() - lastMoveTime > 1000) {
			return;
		}

		double flashCooldown = storage.getDouble(TemporaryKey.FLASH_COOLDOWN);
		long currentTime = System.currentTimeMillis();
		long flashLastUse = storage.getLong(TemporaryKey.FLASH_LAST_USE);
		if (System.currentTimeMillis() - flashLastUse < flashCooldown) {
			String flashCooldownMessage = storage.getString(TemporaryKey.FLASH_COOLDOWN_MESSAGE);
			double timeLeft = (flashCooldown - (currentTime - flashLastUse)) / 1000d;

			Placeholder placeholder = PlaceholderBuilder.builder().put("{time_left}", StringUtils.formatNumber(timeLeft)).build();
			flashCooldownMessage = placeholder.apply(flashCooldownMessage);

			MessageUtils.send(player, flashCooldownMessage);
			return;
		}

		String particle = storage.getString(TemporaryKey.FLASH_PARTICLE);

		double power = storage.getDouble(TemporaryKey.FLASH_POWER);
		boolean smart = storage.getBoolean(TemporaryKey.FLASH_SMART);
		boolean flashSuccess = FlashFeature.flash(player, power, particle, smart);
		if (!flashSuccess) {
			storage.set(TemporaryKey.FLASH_LAST_USE, flashLastUse + 1000);
			return;
		}
		storage.set(TemporaryKey.FLASH_LAST_USE, currentTime);
	}
}
