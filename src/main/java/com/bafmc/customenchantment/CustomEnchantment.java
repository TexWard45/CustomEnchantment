package com.bafmc.customenchantment;

import com.bafmc.bukkit.BafPlugin;
import com.bafmc.customenchantment.attribute.AttributeModule;
import com.bafmc.customenchantment.command.CommandModule;
import com.bafmc.customenchantment.config.BookCraftConfig;
import com.bafmc.customenchantment.config.ConfigModule;
import com.bafmc.customenchantment.config.MainConfig;
import com.bafmc.customenchantment.custommenu.CustomMenuModule;
import com.bafmc.customenchantment.database.DatabaseModule;
import com.bafmc.customenchantment.enchant.EffectTaskSeparate;
import com.bafmc.customenchantment.enchant.EnchantModule;
import com.bafmc.customenchantment.execute.ExecuteModule;
import com.bafmc.customenchantment.feature.FeatureModule;
import com.bafmc.customenchantment.filter.FilterModule;
import com.bafmc.customenchantment.guard.GuardModule;
import com.bafmc.customenchantment.item.ItemModule;
import com.bafmc.customenchantment.item.mask.group.CEArtifactGroupMap;
import com.bafmc.customenchantment.listener.ListenerModule;
import com.bafmc.customenchantment.menu.MenuModule;
import com.bafmc.customenchantment.placeholder.PlaceholderModule;
import com.bafmc.customenchantment.player.PlayerModule;
import com.bafmc.customenchantment.task.TaskModule;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Listener;

import java.io.File;

@Getter
@Setter
public class CustomEnchantment extends BafPlugin implements Listener {
	private static CustomEnchantment instance;
	private CEEnchantMap ceEnchantMap;
	private CEGroupMap ceGroupMap;
	private CEItemStorageMap ceItemStorageMap;
	private CEArtifactGroupMap ceArtifactGroupMap;
	private MainConfig mainConfig;
	private BookCraftConfig bookCraftConfig;
	private AttributeModule attributeModule;
	private FilterModule filterModule;
	private CommandModule commandModule;
	private EnchantModule enchantModule;
	private ItemModule itemModule;
	private PlayerModule playerModule;
	private ExecuteModule executeModule;
	private TaskModule taskModule;
	private CustomMenuModule customMenuModule;
	private ListenerModule listenerModule;
	private DatabaseModule databaseModule;
	private MenuModule menuModule;
	private PlaceholderModule placeholderModule;
	private GuardModule guardModule;
	private ConfigModule configModule;
	private FeatureModule featureModule;
	private boolean inReload;

	@Override
	public void onEnable() {
		CustomEnchantment.instance = this;

		super.onEnable();

		getServer().getPluginManager().registerEvents(new MobDamageTrackerListener(), this);
	}

	public void registerModules() {
		registerModule((this.featureModule = new FeatureModule(this)));
		registerModule((this.customMenuModule = new CustomMenuModule(this)));
		registerModule((this.attributeModule = new AttributeModule(this)));
		registerModule((this.filterModule = new FilterModule(this)));
		registerModule((this.commandModule = new CommandModule(this)));
		registerModule((this.itemModule = new ItemModule(this)));
		registerModule((this.playerModule = new PlayerModule(this)));
		registerModule((this.executeModule = new ExecuteModule(this)));
		registerModule((this.enchantModule = new EnchantModule(this)));
		registerModule((this.configModule = new ConfigModule(this)));
		registerModule((this.taskModule = new TaskModule(this)));
		registerModule((this.guardModule = new GuardModule(this)));
		registerModule((this.databaseModule = new DatabaseModule(this)));
		registerModule((this.menuModule = new MenuModule(this)));
		registerModule((this.placeholderModule = new PlaceholderModule(this)));
		registerModule((this.listenerModule = new ListenerModule(this)));
	}

	public void addEffectTask(EffectTaskSeparate effectTask) {
		getTaskModule().getAsyncEffectExecuteTask().addEffectDataList(effectTask.getEffectAsyncList());
		getTaskModule().getEffectExecuteTask().addEffectDataList(effectTask.getEffectList());
	}

	public void removeEffectTask(String playerName, String name) {
		getTaskModule().getAsyncEffectExecuteTask().removeEffectData(playerName, name);
		getTaskModule().getEffectExecuteTask().removeEffectData(playerName, name);
	}

	public File getGeneralDataFolder() {
		return new File(getDataFolder(), "data");
	}

	public File getPlayerDataFolder() {
		return new File(getGeneralDataFolder(), "player");
	}

	public File getWeaponFolder() {
		return new File(getDataFolder(), "weapon");
	}

	public File getPlayerDataFile(OfflinePlayer player) {
		return new File(getPlayerDataFolder(), player.getName().toLowerCase() + ".yml");
	}

	public File getConfigFile() {
		return new File(getDataFolder(), "config.yml");
	}

	public File getMessagesFile() {
		return new File(getDataFolder(), "messages.yml");
	}

	public File getTinkererFile() {
		return new File(getDataFolder(), "tinkerer.yml");
	}

	public File getGroupFile() {
		return new File(getDataFolder(), "groups.yml");
	}

	public File getArtifactGroupFile() {
		return new File(getDataFolder(), "artifact-groups.yml");
	}

	public File getItemFile() {
		return new File(getDataFolder(), "items.yml");
	}

	public File getStorageItemFolder() {
		return new File(getDataFolder(), "storage");
	}

	public File getSaveItemFile() {
		return new File(getStorageItemFolder(), "save-items.yml");
	}

	public File getBookCraftFile() {
		return new File(getDataFolder(), "book-craft.yml");
	}

    public File getBookUpgradeFile() {
        return new File(getDataFolder(), "book-upgrade.yml");
    }

    public File getBookUpgradeFolder() {
        return new File(getDataFolder(), "book-upgrade");
    }

	public File getDatabaseFile() {
		return new File(getDataFolder(), "data.db");
	}

	public File getEnchantFolder() {
		return new File(getDataFolder(), "enchantment");
	}

	public File getArtifactFolder() {
		return new File(getDataFolder(), "artifact");
	}

	public File getArtifactUpgradeFile() {
		return new File(getDataFolder(), "artifact-upgrade.yml");
	}

	public File getMenuFolder() {
		return new File(getDataFolder(), "menu");
	}

    public static CustomEnchantment instance() {
		return instance;
	}
}