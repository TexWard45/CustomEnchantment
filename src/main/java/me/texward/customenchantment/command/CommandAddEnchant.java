package me.texward.customenchantment.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.item.CEWeaponAbstract;
import me.texward.texwardlib.command.AbstractCommand;
import me.texward.texwardlib.command.AdvancedCommandBuilder;
import me.texward.texwardlib.command.AdvancedCommandExecutor;
import me.texward.texwardlib.command.AdvancedTabCompleter;
import me.texward.texwardlib.command.Argument;
import me.texward.texwardlib.command.ArgumentType;

public class CommandAddEnchant extends AbstractCommand {

	public void setup(AdvancedCommandBuilder builder) {
		builder
			.start()
				.nextArgument("addenchant")
				.setPermission("customenchantment.addenchant.other")
				.nextArgument(ArgumentType.PLAYER)
				.setTabCompleter(new AdvancedTabCompleter() {
					public List<String> onTabComplete(CommandSender arg0, Argument arg) {
						return CustomEnchantment.instance().getCEEnchantMap().getKeys();
					}
				})
				.nextArgument("<enchant>")
				.setTabCompleter(new AdvancedTabCompleter() {
					public List<String> onTabComplete(CommandSender arg0, Argument arg) {
						String name = arg.get("<enchant>");

						CEEnchant enchant = CEAPI.getCEEnchant(name);

						if (enchant != null) {
							List<String> levels = new ArrayList<String>();

							for (Integer level : enchant.getCELevelMap().keySet()) {
								levels.add(level.toString());
							}

							return levels;
						}
						
						return Arrays.asList("");
					}
				})
				.nextArgument("<level>")
				.setCommandExecutor(new AdvancedCommandExecutor() {
					public boolean onCommand(CommandSender sender, Argument arg) {
						Player player = arg.getPlayer();
						if (player == null) {
							return true;
						}

						ItemStack itemStack = player.getInventory().getItemInHand();
						if (itemStack == null) {
							return true;
						}

						CEItem ceItem = CEAPI.getCEItem(itemStack);
						if (!(ceItem instanceof CEWeaponAbstract)) {
							return true;
						}

						CEWeaponAbstract weapon = (CEWeaponAbstract) ceItem;
						CESimple ceSimple = null;

						try {
							ceSimple = new CESimple(arg.get("<enchant>"), Integer.valueOf(arg.get("<level>")));
						} catch (Exception e) {
							ceSimple = new CESimple(arg.get("<enchant>"), 1);
						}

						if (ceSimple.getCEEnchant() == null) {
							return true;
						}

						weapon.getWeaponEnchant().forceAddCESimple(ceSimple);
						player.setItemInHand(weapon.exportTo());
						return true;
					}
				})
			.finish();
	}
	
}