package com.bafmc.customenchantment.enchant.effect;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.bukkit.utils.StringUtils;

public class EffectDurability extends EffectHook {
	private ModifyType modifyType;
	private List<String> typeArmor;
	private RandomRange value;

	public String getIdentify() {
		return "DURABILITY";
	}

	public void setup(String[] args) {
		this.modifyType = ModifyType.valueOf(args[0]);
		this.typeArmor = StringUtils.split(args[1], ",", 0);
		this.value = new RandomRange(args[2]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		for (String typeArmor : this.typeArmor) {
			int value = this.value.getIntValue();
			if (value == 0 && (modifyType == ModifyType.ADD || modifyType == ModifyType.REMOVE)) {
				continue;
			}
			ItemStack item = null;

			EquipSlot typeArmor2 = null;
			if (typeArmor.equals("AUTO") && data != null) {
				typeArmor2 = data.getEquipSlot();
			} else {
				typeArmor2 = EquipSlot.valueOf(typeArmor);
			}

			switch (typeArmor2) {
			case BOOTS:
				item = player.getInventory().getBoots();
				break;
			case CHESTPLATE:
				item = player.getInventory().getChestplate();
				break;
			case HELMET:
				item = player.getInventory().getHelmet();
				break;
			case LEGGINGS:
				item = player.getInventory().getLeggings();
				break;
			case MAINHAND:
				item = player.getInventory().getItemInMainHand();
				break;
			case OFFHAND:
				item = player.getInventory().getItemInOffHand();
				break;
			default:
				break;
			}

			if (item == null || item.getType() == Material.AIR)
				return;

			int newDurability = 0;
			
			ItemMeta meta = item.getItemMeta();
			if (!(meta instanceof Damageable)) {
				return;
			}
			
			Damageable damagable = (Damageable) meta;

			switch (modifyType) {
			case ADD:
				newDurability = damagable.getDamage() - value;
				if (newDurability < 0) {
					newDurability = 0;
				}

				damagable.setDamage(newDurability);
				item.setItemMeta((ItemMeta) damagable);
				break;
			case REMOVE:
				newDurability = damagable.getDamage() + value;
				if (newDurability > item.getType().getMaxDurability()) {
					newDurability = item.getType().getMaxDurability();
				}

				damagable.setDamage(newDurability);
				item.setItemMeta((ItemMeta) damagable);
				break;
			case SET:
				newDurability = value;
				if (newDurability < 0) {
					newDurability = 0;
				}
				if (newDurability > item.getType().getMaxDurability()) {
					newDurability = item.getType().getMaxDurability();
				}

				damagable.setDamage(newDurability);
				item.setItemMeta((ItemMeta) damagable);
				break;
			default:
				break;
			}
		}
	}
}
