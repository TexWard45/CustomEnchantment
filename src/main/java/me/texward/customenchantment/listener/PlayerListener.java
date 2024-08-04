package me.texward.customenchantment.listener;

import me.texward.customenchantment.ConfigVariable;
import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CECallerBuilder;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEType;
import me.texward.customenchantment.event.CEPlayerStatsModifyEvent;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEItemUsable;
import me.texward.customenchantment.item.CEWeaponAbstract;
import me.texward.customenchantment.player.*;
import me.texward.customenchantment.player.PlayerAbility.Type;
import me.texward.texwardlib.event.ItemEquipEvent;
import me.texward.texwardlib.util.EquipSlot;
import me.texward.texwardlib.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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

		e.setValue(attribute.getAttributeValue(e.getStatsType().name(), e.getCurrentValue()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.onJoin();

		CECallerBuilder
				.build(player)
				.setCEType(CEType.ARMOR_UNDRESS)
				.call();
		
		CECallerBuilder
				.build(player)
				.setCEType(CEType.CHANGE_HAND)
				.call();

		CECallerBuilder
				.build(player)
				.setCEType(CEType.ARMOR_EQUIP)
				.call();
		
		CECallerBuilder
				.build(player)
				.setCEType(CEType.HOLD)
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
				.setExecuteLater(false)
				.call();

		CECallerBuilder
				.build(player)
				.setCEType(CEType.CHANGE_HAND)
				.setExecuteLater(false)
				.call();

		cePlayer.onQuit();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChancedWorld(PlayerChangedWorldEvent e) {
		Player player = e.getPlayer();

		if (ConfigVariable.isEnchantDisableLocation(player.getLocation())) {
			CECallerBuilder
					.build(player)
					.setCEType(CEType.ARMOR_UNDRESS)
					.setExecuteLater(false)
					.call();
			
			CECallerBuilder
					.build(player)
					.setCEType(CEType.CHANGE_HAND)
					.setExecuteLater(false)
					.call();
		} else if (ConfigVariable.isEnchantDisableLocation(e.getFrom())) {
			CECallerBuilder
					.build(player)
					.setCEType(CEType.ARMOR_EQUIP)
					.setExecuteLater(false)
					.call();
			
			CECallerBuilder
					.build(player)
					.setCEType(CEType.HOLD)
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

		ItemStack oldItemStack = e.getOldItemStack();
		CEWeaponAbstract ceOldWeapon = CEWeaponAbstract.getCEWeapon(oldItemStack);
		// Call on old equip
		if (ceOldWeapon != null) {
			Map<EquipSlot, CEWeaponAbstract> map = CEAPI.getCEWeaponMap(player);
			map.put(slot, ceOldWeapon);
			
			CEType ceType = slot.isArmor() ? CEType.ARMOR_UNDRESS : CEType.CHANGE_HAND;
			
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
			
			CEType ceType = slot.isArmor() ? CEType.ARMOR_EQUIP : CEType.HOLD;
			
			CECallerBuilder
					.build(player)
					.setCEType(ceType)
					.setWeaponMap(map)
					.setActiveEquipSlot(slot)
					.call();
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
				PotionEffect effect = player.getPotionEffect(PotionEffectType.JUMP);
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
				callMove(cePlayer);
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
			usable.useBy(player);

			if (player.getGameMode() == GameMode.SURVIVAL) {
				player.getInventory().setItemInHand(ItemStackUtils.getItemStack(itemStack, itemStack.getAmount() - 1));
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

	public void callMove(CEPlayer cePlayer) {
		cePlayer.getTemporaryStorage().set(TemporaryKey.LAST_MOVE_TIME, System.currentTimeMillis());

		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
		long lastCallMoveTime = storage.getLong(TemporaryKey.LAST_CALL_MOVE_TIME, 0);

		if (System.currentTimeMillis() - lastCallMoveTime > ConfigVariable.MOVE_EVENT_PERIOD) {
			CECallerBuilder
					.build(cePlayer.getPlayer())
					.setCEType(CEType.MOVE)
					.call();
			
			storage.set(TemporaryKey.LAST_CALL_MOVE_TIME, System.currentTimeMillis());
		}
	}
}
