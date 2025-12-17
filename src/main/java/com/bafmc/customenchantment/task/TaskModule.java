package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import lombok.Getter;

@Getter
public class TaskModule extends PluginModule<CustomEnchantment> {
    private EffectExecuteTask asyncEffectExecuteTask;
    private EffectExecuteTask effectExecuteTask;
    private CECallerTask ceCallerTask;
    private CEExtraSlotTask extraSlotTask;
    private RecalculateAttributeTask attributeTask;
    private RegenerationTask regenerationTask;
    private SlowResistanceTask slowResistanceTask;
    private CEPlayerTask cePlayerTask;
    private SpecialMiningTask specialMiningTask;
    private BlockTask blockTask;
    private ArrowTask arrowTask;
    private SaveTask saveTask;
    private UnbreakableArmorTask unbreakableArmorTask;
    private AutoUpdateItemTask autoUpdateItemTask;
    private ExpTask expTask;
    private UpdateAttributeTask updateAttributeTask;
    private OutfitItemTask outfitItemTask;
    
    public TaskModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        this.asyncEffectExecuteTask = new EffectExecuteTask(true);
        this.asyncEffectExecuteTask.runTaskTimerAsynchronously(getPlugin(), 0, 1);

        this.effectExecuteTask = new EffectExecuteTask(false);
        this.effectExecuteTask.runTaskTimer(getPlugin(), 0, 1);

        this.cePlayerTask = new CEPlayerTask(getPlugin());
        this.cePlayerTask.runTaskTimer(getPlugin(), 0, 20);

        this.ceCallerTask = new CECallerTask(getPlugin());
        this.ceCallerTask.runTaskTimer(getPlugin(), 0, 1);

        this.extraSlotTask = new CEExtraSlotTask(getPlugin());
        this.extraSlotTask.runTaskTimer(getPlugin(), 0, 4);

        this.attributeTask = new RecalculateAttributeTask(getPlugin());
        this.attributeTask.runTaskTimerAsynchronously(getPlugin(), 0, 1);

        this.regenerationTask = new RegenerationTask(getPlugin());
        this.regenerationTask.runTaskTimer(getPlugin(), 0, 4);

        this.updateAttributeTask = new UpdateAttributeTask(getPlugin());
        this.updateAttributeTask.runTaskTimer(getPlugin(), 0, 4);

        this.slowResistanceTask = new SlowResistanceTask(getPlugin());
        this.slowResistanceTask.runTaskTimer(getPlugin(), 0, 4);

        this.specialMiningTask = new SpecialMiningTask(getPlugin());
        this.specialMiningTask.runTaskTimer(getPlugin(), 0, 1);

        this.blockTask = new BlockTask(getPlugin());
        this.blockTask.runTaskTimer(getPlugin(), 0, 1);

        this.arrowTask = new ArrowTask();
        this.arrowTask.runTaskTimer(getPlugin(), 0, 20);

        this.saveTask = new SaveTask(getPlugin());
        this.saveTask.runTaskTimer(getPlugin(), 0, 20 * 60 * 15);

        this.unbreakableArmorTask = new UnbreakableArmorTask();
        this.unbreakableArmorTask.runTaskTimer(getPlugin(), 0, getPlugin().getMainConfig().getUnbreakableArmorTickInterval());

//        this.autoUpdateItemTask = new AutoUpdateItemTask();
//        this.autoUpdateItemTask.runTaskTimer(getPlugin(), 0, 1);

        this.expTask = new ExpTask();
        this.expTask.runTaskTimer(getPlugin(), 200, 200);

        if (getPlugin().getMainConfig().isSigilDisplayEnable()) {
            SigilItemTask sigilItemTask = new SigilItemTask();
            sigilItemTask.runTaskTimerAsynchronously(getPlugin(), 0, 1);
        }

        this.outfitItemTask = new OutfitItemTask();
        this.outfitItemTask.runTaskTimer(getPlugin(), 0, 20);
    }

    public void onDisable() {
        this.asyncEffectExecuteTask.cancel();
        this.effectExecuteTask.cancel();
        this.cePlayerTask.cancel();
        this.ceCallerTask.cancel();
        this.extraSlotTask.cancel();
        this.attributeTask.cancel();
        this.regenerationTask.cancel();
        this.slowResistanceTask.cancel();
        this.specialMiningTask.cancel();
        this.blockTask.cancel();
        this.arrowTask.cancel();
        this.expTask.cancel();
    }
}
