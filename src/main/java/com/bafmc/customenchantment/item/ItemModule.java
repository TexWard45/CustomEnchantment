package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.artifact.CEArtifactFactory;
import com.bafmc.customenchantment.item.banner.CEBannerFactory;
import com.bafmc.customenchantment.item.book.CEBookFactory;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPointFactory;
import com.bafmc.customenchantment.item.eraseenchant.CEEraseEnchantFactory;
import com.bafmc.customenchantment.item.gem.CEGemFactory;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillFactory;
import com.bafmc.customenchantment.item.increaseratebook.CEIncreaseRateBookFactory;
import com.bafmc.customenchantment.item.loreformat.CELoreFormatFactory;
import com.bafmc.customenchantment.item.mask.CEMaskFactory;
import com.bafmc.customenchantment.item.nametag.CENameTagFactory;
import com.bafmc.customenchantment.item.protectdead.CEProtectDeadFactory;
import com.bafmc.customenchantment.item.protectdestroy.CEProtectDestroyFactory;
import com.bafmc.customenchantment.item.randombook.CERandomBookFactory;
import com.bafmc.customenchantment.item.removeenchant.CERemoveEnchantFactory;
import com.bafmc.customenchantment.item.removeenchantpoint.CERemoveEnchantPointFactory;
import com.bafmc.customenchantment.item.removegem.CERemoveGemFactory;
import com.bafmc.customenchantment.item.removeprotectdead.CERemoveProtectDeadFactory;
import com.bafmc.customenchantment.item.sigil.CESigilFactory;

public class ItemModule extends PluginModule<CustomEnchantment> {
    public ItemModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        new CEArtifactFactory().register();
        new CEMaskFactory().register();
        new CEBannerFactory().register();
        new CEWeaponFactory().register();
        new CEBookFactory().register();
        new CEProtectDeadFactory().register();
        new CEGemFactory().register();
        new CERemoveProtectDeadFactory().register();
        new CEProtectDestroyFactory().register();
        new CENameTagFactory().register();
        new CEEnchantPointFactory().register();
        new CEIncreaseRateBookFactory().register();
        new CERandomBookFactory().register();
        new CERemoveEnchantFactory().register();
        new CERemoveGemFactory().register();
        new CERemoveEnchantPointFactory().register();
        new CEEraseEnchantFactory().register();
        new CELoreFormatFactory().register();
        new CEGemDrillFactory().register();
        new CESigilFactory().register();
    }
}
