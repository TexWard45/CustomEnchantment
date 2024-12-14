package com.bafmc.customenchantment.guard;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.task.GuardTask;
import lombok.Getter;

@Getter
public class GuardModule extends PluginModule<CustomEnchantment> {
    private GuardManager guardManager;
    private GuardTask guardTask;

    public GuardModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        this.guardManager = new GuardManager();
        this.setupTask();
    }

    public void setupTask() {
        this.guardTask = new GuardTask(getPlugin(), this.guardManager);
        this.guardTask.runTaskTimer(getPlugin(), 0, 20);
    }

    public void onDisable() {
        this.guardTask.cancel();
    }
}
