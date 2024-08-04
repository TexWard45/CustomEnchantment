package me.texward.customenchantment.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class CustomEnchantmentPlaceholder extends PlaceholderExpansion {
    public String getAuthor() {
        return "TexWard";
    }

    public String getIdentifier() {
        return "customenchantment";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String onRequest(OfflinePlayer player, String params) {
        return null;
    }
}
