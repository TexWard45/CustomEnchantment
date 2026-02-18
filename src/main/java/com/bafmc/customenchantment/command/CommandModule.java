package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;

public class CommandModule extends PluginModule<CustomEnchantment> {
    public CommandModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        AdvancedCommandBuilder builder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("customenchantment");

        new CustomEnchantmentCommand(getPlugin()).onRegister(builder);

        builder.build();

        AdvancedCommandBuilder tinkererBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("tinkerer");
        tinkererBuilder.commandExecutor(new TinkererCommand(getPlugin())).end();
        tinkererBuilder.build();

        AdvancedCommandBuilder bookcraftBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("bookcraft");
        bookcraftBuilder.commandExecutor(new BookCraftCommand(getPlugin())).end();
        bookcraftBuilder.build();

        AdvancedCommandBuilder ceanvilBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("ceanvil");
        ceanvilBuilder.commandExecutor(new CEAnvilCommand(getPlugin())).end();
        ceanvilBuilder.build();

        AdvancedCommandBuilder bookUpgradeBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("bookupgrade");
        bookUpgradeBuilder.commandExecutor(new BookUpgradeCommand(getPlugin())).end();
        bookUpgradeBuilder.build();

        AdvancedCommandBuilder artifactUpgradeBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("artifactupgrade");
        artifactUpgradeBuilder.commandExecutor(new ArtifactUpgradeCommand(getPlugin())).end();
        artifactUpgradeBuilder.build();

        AdvancedCommandBuilder equipmentBuilder = AdvancedCommandBuilder.builder()
                .plugin(getPlugin())
                .rootCommand("equipment");
        equipmentBuilder.commandExecutor(new EquipmentCommand(getPlugin())).end();
        equipmentBuilder.build();

        AdvancedCommandBuilder nameTagBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("nametag");
        new CommandNameTag().onRegister(nameTagBuilder);
        nameTagBuilder.build();

        AdvancedCommandBuilder filterEnchantBuilder = AdvancedCommandBuilder.builder().plugin(getPlugin()).rootCommand("cefilter");
        new CommandFilterEnchant().onRegister(filterEnchantBuilder);
        filterEnchantBuilder.build();
    }
}
