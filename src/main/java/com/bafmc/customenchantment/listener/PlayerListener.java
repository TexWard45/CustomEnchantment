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
import com.bafmc.customenchantment.feature.DashFeature;
import com.bafmc.customenchantment.feature.DoubleJumpFeature;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemUsable;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.*;
import com.bafmc.customenchantment.player.PlayerAbility.Type;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
		CEPlayer cePlayer = e.getCEPlayer();

		PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

		CustomAttributeType type = e.getStatsType();
		double value = e.getCurrentValue();
		double newValue = attribute.getValue(type, value);

		e.setValue(newValue);
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

		if (CustomEnchantment.instance().getArtifactTask().updateArtifact(player)) {
			CECallerBuilder
					.build(player)
					.setCEType(CEType.CHANGE_HAND)
					.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HAND_ARRAY))
					.call();
		}

		CECallerBuilder
				.build(player)
				.setCEType(CEType.HOTBAR_CHANGE)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HOTBAR_ARRAY))
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

		CECallerBuilder
				.build(player)
				.setCEType(CEType.HOTBAR_HOLD)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HOTBAR_ARRAY))
				.call();

//		CEAPI.callCEList(player, CEType.ARMOR_UNDRESS, armorMap);
//		CEAPI.callCEList(player, CEType.CHANGE_HAND, handMap);
//
//		CEAPI.callCEList(player, CEType.ARMOR_EQUIP, armorMap);
//		CEAPI.callCEList(player, CEType.HOLD, handMap);
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

		CECallerBuilder
				.build(player)
				.setCEType(CEType.HOTBAR_CHANGE)
				.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HOTBAR_ARRAY))
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

			CECallerBuilder
					.build(player)
					.setCEType(CEType.HOTBAR_CHANGE)
					.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HOTBAR_ARRAY))
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

			if (CustomEnchantment.instance().getArtifactTask().updateArtifact(player)) {
				CECallerBuilder
						.build(player)
						.setCEType(CEType.HOTBAR_HOLD)
						.setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.HOTBAR_ARRAY))
						.setExecuteLater(false)
						.call();
			}
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

		ItemStack oldItemStack = e.getOldItemStack();
		CEWeaponAbstract ceOldWeapon = CEWeaponAbstract.getCEWeapon(oldItemStack);
		// Call on old equip
		if (ceOldWeapon != null) {
			Map<EquipSlot, CEWeaponAbstract> map = CEAPI.getCEWeaponMap(player);
			map.put(slot, ceOldWeapon);
			
			CEType ceType = null;

			if (slot.isArmor()) {
				ceType = CEType.ARMOR_UNDRESS;
			} else if (slot.isHotbar()){
				ceType = CEType.HOTBAR_CHANGE;
			}else {
				ceType = CEType.CHANGE_HAND;
			}

			CECallerBuilder
					.build(player)
					.setCEType(ceType)
					.setWeaponMap(map)
					.setActiveEquipSlot(slot)
					.call();
		}

		ItemStack newItemStack = e.getNewItemStack();
		CEWeaponAbstract ceNewWeapon = CEWeaponAbstract.getCEWeapon(newItemStack);
		cePlayer.setSlot(slot, ceNewWeapon);
		// Call on new equip
		if (ceNewWeapon != null) {
			Map<EquipSlot, CEWeaponAbstract> map = CEAPI.getCEWeaponMap(player);
			map.put(slot, ceNewWeapon);
			
			CEType ceType = null;

			if (slot.isArmor()) {
				ceType = CEType.ARMOR_EQUIP;
			} else if (slot.isHotbar()){
				ceType = CEType.HOTBAR_HOLD;
			}else {
				ceType = CEType.HOLD;
			}

			if (ceType != CEType.HOTBAR_HOLD || (CustomEnchantment.instance().getArtifactTask().updateArtifact(player))) {
				CECallerBuilder
						.build(player)
						.setCEType(ceType)
						.setWeaponMap(map)
						.setActiveEquipSlot(slot)
						.call();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();

		try {
			CEPlayer cePlayer = CEAPI.getCEPlayer(player);
			Location from = e.getFrom();
			Location to = e.getTo();

			PlayerAbility ability = cePlayer.getAbility();
			if (ability.isCancel(Type.MOVE)
					&& (from.getX() != to.getX() || from.getY() < to.getY() || from.getZ() != to.getZ()))
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

			if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
				callMove(cePlayer, from, to);
			}
		} catch (Exception ex) {
			System.out.println("Error Move at player " + player);
			ex.printStackTrace();
		}
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
}
