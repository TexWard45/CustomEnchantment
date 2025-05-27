package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AbstractCommand;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.item.randombook.CERandomBookPlayerFilter;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandFilterEnchant implements AbstractCommand {

	public void onRegister(AdvancedCommandBuilder builder) {
		builder
			.subCommand("add")
                .subCommand("<enchant>")
                    .tabCompleter((arg0, arg) -> CERandomBookPlayerFilter.getFilterCEList())
                    .commandExecutor((sender, arg) -> {
                        if (!(sender instanceof Player player)) {
                            return true;
                        }

                        CEEnchantSimple ceEnchantSimple = new CEEnchantSimple(arg.get("<enchant>"), 1);
                        if (ceEnchantSimple.getCEEnchant() == null) {
                            CustomEnchantmentMessage.send(player, "command.cefilter.add.not-found");
                            return true;
                        }

                        CERandomBookPlayerFilter.add(player, ceEnchantSimple);

                        Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceEnchantSimple);
                        CustomEnchantmentMessage.send(player, "command.cefilter.add.success", placeholder);
                        return true;
                    })
				.end()
			.end()
            .subCommand("remove")
                .subCommand("<enchant>")
                    .tabCompleter((arg0, arg) -> CERandomBookPlayerFilter.getFilterCEList())
                    .commandExecutor((sender, arg) -> {
                        if (!(sender instanceof Player player)) {
                            return true;
                        }

                        CEEnchantSimple ceEnchantSimple = new CEEnchantSimple(arg.get("<enchant>"), 1);
                        if (ceEnchantSimple.getCEEnchant() == null) {
                            CustomEnchantmentMessage.send(player, "command.cefilter.remove.not-found");
                            return true;
                        }

                        CERandomBookPlayerFilter.remove(player, arg.get("<enchant>"));

                        Map<String, String> placeholder = CEPlaceholder.getCESimplePlaceholder(ceEnchantSimple);
                        CustomEnchantmentMessage.send(player, "command.cefilter.remove.success", placeholder);
                            return true;
                    })
                .end()
            .end()
            .subCommand("clear")
                .commandExecutor((sender, arg) -> {
                    if (!(sender instanceof Player player)) {
                        return true;
                    }

                    if (CERandomBookPlayerFilter.isEmpty(player)) {
                        CustomEnchantmentMessage.send(player, "command.cefilter.clear.empty");
                        return true;
                    }

                    CERandomBookPlayerFilter.clear(player);
                    CustomEnchantmentMessage.send(player, "command.cefilter.clear.success");
                    return true;
                })
            .end()
            .subCommand("list")
                .commandExecutor((sender, arg) -> {
                    if (!(sender instanceof Player player)) {
                        return true;
                    }

                    if (CERandomBookPlayerFilter.isEmpty(player)) {
                        CustomEnchantmentMessage.send(player, "command.cefilter.list.empty");
                        return true;
                    }

                    List<String> enchantList = new ArrayList<>();

                    for (CEEnchantSimple ceEnchantSimple : CERandomBookPlayerFilter.get(player)) {
                        enchantList.add(ceEnchantSimple.getCEEnchant().getName());
                    }

                    String enchantListComma = String.join(", ", enchantList);

                    Map<String, String> placeholder = new HashMap<>();
                    placeholder.put("%enchant_list%", enchantListComma);

                    CustomEnchantmentMessage.send(player, "command.cefilter.list.success", placeholder);
                    return true;
                })
            .end()
                .subCommand("help")
                .commandExecutor((sender, arg) -> {
                    if (!(sender instanceof Player player)) {
                        return true;
                    }

                    CustomEnchantmentMessage.send(player, "command.cefilter.help");
                    return true;
                })
            .end();
	}
	
}