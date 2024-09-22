package com.bafmc.customenchantment;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.command.CommandNameTag;
import com.bafmc.customenchantment.command.CustomEnchantmentCommand;
import com.bafmc.customenchantment.config.*;
import com.bafmc.customenchantment.enchant.condition.*;
import com.bafmc.customenchantment.enchant.effect.*;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.listener.*;
import com.bafmc.customenchantment.menu.anvil.*;
import com.bafmc.customenchantment.player.*;
import com.bafmc.customenchantment.player.mining.*;
import com.bafmc.customenchantment.task.*;
import com.bafmc.customenchantment.custommenu.CEBookCatalog;
import com.bafmc.customenchantment.custommenu.CustomEnchantmentItemDisplaySetup;
import com.bafmc.customenchantment.custommenu.CustomEnchantmentTradeItemCompare;
import com.bafmc.customenchantment.database.Database;
import com.bafmc.customenchantment.enchant.EffectTaskSeparate;
import com.bafmc.customenchantment.execute.GiveItemExecute;
import com.bafmc.customenchantment.execute.GiveVoucherItemExecute;
import com.bafmc.customenchantment.execute.UseItemExecute;
import com.bafmc.customenchantment.filter.FilterRegister;
import com.bafmc.customenchantment.guard.GuardManager;
import com.bafmc.customenchantment.menu.BookcraftMenu;
import com.bafmc.customenchantment.menu.BookUpgradeMenu;
import com.bafmc.customenchantment.menu.CEAnvilMenu;
import com.bafmc.customenchantment.placeholder.CustomEnchantmentPlaceholder;
import com.bafmc.custommenu.api.CustomMenuAPI;
import com.bafmc.bukkit.utils.ConfigUtils;
import com.bafmc.bukkit.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class CustomEnchantment extends JavaPlugin implements Listener {
	private static CustomEnchantment instance;
	private CEPlayerMap cePlayerMap;
	private CEEnchantMap ceEnchantMap;
	private CEGroupMap ceGroupMap;
	private CEItemStorageMap ceItemStorageMap;
	private EffectExecuteTask asyncEffectExecuteTask;
	private EffectExecuteTask effectExecuteTask;
	private CECallerTask ceCallerTask;
	private CEPlayerTask cePlayerTask;
	private SpecialMiningTask specialMiningTask;
	private GuardManager guardManager;
	private GuardTask guardTask;
	private BlockTask blockTask;
    private ArrowTask arrowTask;
	private Database database;

	@Override
	public void onEnable() {
		CustomEnchantment.instance = this;

		setup();
	}

	@Override
	public void onDisable() {
		this.asyncEffectExecuteTask.cancel();
		this.effectExecuteTask.cancel();
		this.cePlayerTask.cancel();
		this.specialMiningTask.cancel();
		this.guardTask.cancel();
		this.blockTask.cancel();
		this.database.disconnect();
		unregisterCMenu();
	}

	public void registerCMenu() {
		if (Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
			CustomMenuAPI.registerPlugin(this, getMenuFolder());
		}
	}

	public void unregisterCMenu() {
		if (Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
			CustomMenuAPI.unregisterPlugin(this);
		}
	}

	public void setup() {
        setupFilter();
		setupCommand();
		setupTradeItemCompare();
		setupCEItem();
		setupCEPlayerExpansion();
		setupPlayerSpecialMining();
		setupExecute();
		setupFile();
		setupEffect();
		setupCondition();
		setupConfig();
		setupTask();
		setupPlayers();
		setupGuard();
		setupListener();
		setupDatabase();
		setupMenu();
		setupCatalog();

        Bukkit.getScheduler().runTask(this, () -> setupPlaceholders());

		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			registerCMenu();
		});
	}

    public void setupFilter() {
        FilterRegister.register();
    }

	public void setupCommand() {
		AdvancedCommandBuilder builder = AdvancedCommandBuilder.builder().plugin(this).rootCommand("customenchantment");

		new CustomEnchantmentCommand(this).onRegister(builder);

		builder.build();

		AdvancedCommandBuilder bookCraftBuilder = AdvancedCommandBuilder.builder().plugin(this).rootCommand("bookcraft");
		bookCraftBuilder.commandExecutor(new AdvancedCommandExecutor() {
			public boolean onCommand(CommandSender sender, Argument arg) {
				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player) sender;
				CustomMenuAPI.getCPlayer(player).openCustomMenu(BookcraftMenu.MENU_NAME, true);
				return true;
			}
		}).end();
		bookCraftBuilder.build();

        AdvancedCommandBuilder bookUpgradeBuilder = AdvancedCommandBuilder.builder().plugin(this).rootCommand("bookupgrade");
        bookUpgradeBuilder.commandExecutor(new AdvancedCommandExecutor() {
            public boolean onCommand(CommandSender sender, Argument arg) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                CustomMenuAPI.getCPlayer(player).openCustomMenu(BookUpgradeMenu.MENU_NAME, true);
                return true;
            }
        }).end();
        bookUpgradeBuilder.build();

		AdvancedCommandBuilder tinkererBuilder = AdvancedCommandBuilder.builder().plugin(this).rootCommand("tinkerer");
		tinkererBuilder.commandExecutor(new AdvancedCommandExecutor() {
			public boolean onCommand(CommandSender sender, Argument arg) {
				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player) sender;
				CustomMenuAPI.getCPlayer(player).openCustomMenu("tinkerer", true);
				return true;
			}
		}).end();
		tinkererBuilder.build();

		AdvancedCommandBuilder anvilBuilder = AdvancedCommandBuilder.builder().plugin(this).rootCommand("ceanvil");
		anvilBuilder.commandExecutor(new AdvancedCommandExecutor() {
			public boolean onCommand(CommandSender sender, Argument arg) {
				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player) sender;
				CustomMenuAPI.getCPlayer(player).openCustomMenu("ce-anvil", true);
				return true;
			}
		}).end();
		anvilBuilder.build();

		AdvancedCommandBuilder nameTagBuilder = AdvancedCommandBuilder.builder().plugin(this).rootCommand("nametag");
		new CommandNameTag().onRegister(nameTagBuilder);
		nameTagBuilder.build();
	}

	public void setupMenu() {
		CEAnvilMenu.registerView1(CEItemType.WEAPON, Slot1CEWeaponView.class);
        CEAnvilMenu.registerView2("default", Slot2CEDefaultView.class);
		CEAnvilMenu.registerView2(CEItemType.REMOVE_ENCHANT, Slot2CERemoveEnchantView.class);
		CEAnvilMenu.registerView2(CEItemType.ENCHANT_POINT, Slot2CEEnchantPointView.class);
		CEAnvilMenu.registerView2(CEItemType.BOOK, Slot2CEBookView.class);
		CEAnvilMenu.registerView2(CEItemType.PROTECT_DEAD, Slot2CEProtectDeadView.class);
		CEAnvilMenu.registerView2(CEItemType.REMOVE_PROTECT_DEAD, Slot2CERemoveProtectDeadView.class);
        CEAnvilMenu.registerView2(CEItemType.LORE_FORMAT, Slot2CELoreFormatView.class);
        CEAnvilMenu.registerView2(CEItemType.REMOVE_ENCHANT_POINT, Slot2CERemoveEnchantPointView.class);
		CEAnvilMenu.registerView2(CEItemType.PROTECT_DESTROY, Slot2CEProtectDestroyView.class);
		CEAnvilMenu.registerView2(CEItemType.EARSE_ENCHANT, Slot2CEEraseEnchantView.class);
	}

	public void setupDatabase() {
		FileUtils.createFile(getDatabaseFile());

		this.database = new Database(getDatabaseFile());
		this.database.connect();
		this.database.init();

		CustomEnchantmentDebug.log("Success connect and init database!");
	}

	public void setupCEItem() {
		CEItemRegister.register(CEWeapon.class);
		CEItemRegister.register(CEMask.class);
		CEItemRegister.register(CEBanner.class);
		CEItemRegister.register(CEBook.class);
		CEItemRegister.register(CEProtectDead.class);
		CEItemRegister.register(CERemoveProtectDead.class);
		CEItemRegister.register(CEProtectDestroy.class);
		CEItemRegister.register(CENameTag.class);
		CEItemRegister.register(CEEnchantPoint.class);
		CEItemRegister.register(CEIncreaseRateBook.class);
		CEItemRegister.register(CERandomBook.class);
		CEItemRegister.register(CERemoveEnchant.class);
        CEItemRegister.register(CERemoveEnchantPoint.class);
		CEItemRegister.register(CEEraseEnchant.class);
        CEItemRegister.register(CELoreFormat.class);
	}

	public void setupCEPlayerExpansion() {
		CEPlayerExpansionRegister.register(PlayerStorage.class);
		CEPlayerExpansionRegister.register(PlayerTemporaryStorage.class);
		CEPlayerExpansionRegister.register(PlayerVanillaAttribute.class);
		CEPlayerExpansionRegister.register(PlayerCustomAttribute.class);
		CEPlayerExpansionRegister.register(PlayerPotion.class);
        CEPlayerExpansionRegister.register(PlayerSet.class);
		CEPlayerExpansionRegister.register(PlayerCECooldown.class);
		CEPlayerExpansionRegister.register(PlayerCEManager.class);
		CEPlayerExpansionRegister.register(PlayerAbility.class);
		CEPlayerExpansionRegister.register(PlayerMobBonus.class);
		CEPlayerExpansionRegister.register(PlayerBlockBonus.class);
		CEPlayerExpansionRegister.register(PlayerSpecialMining.class);
		CEPlayerExpansionRegister.register(PlayerNameTag.class);
	}
	
	public void setupPlayerSpecialMining() {
		PlayerSpecialMiningRegister.register(BlockDropBonusSpecialMine.class);
		PlayerSpecialMiningRegister.register(ExplosionSpecialMine.class);
		PlayerSpecialMiningRegister.register(FurnaceSpecialMine.class);
		PlayerSpecialMiningRegister.register(TelepathySpecialMine.class);
		PlayerSpecialMiningRegister.register(AutoSellSpecialMine.class);
	}

	public void setupExecute() {
		new GiveItemExecute().register();
		new UseItemExecute().register();

		if (Bukkit.getPluginManager().isPluginEnabled("Vouchers")) {
			new GiveVoucherItemExecute().register();
		}
	}

	public void setupFile() {
		FileUtils.createFolder(getPlayerDataFolder());
		FileUtils.createFolder(getEnchantFolder());

		ConfigUtils.setupResource(this, "/enchantment/armor.yml",
				new File(getEnchantFolder(), File.separator + "armor.yml"));
		ConfigUtils.setupResource(this, "/enchantment/axe.yml",
				new File(getEnchantFolder(), File.separator + "axe.yml"));
		ConfigUtils.setupResource(this, "/enchantment/boots.yml",
				new File(getEnchantFolder(), File.separator + "boots.yml"));
		ConfigUtils.setupResource(this, "/enchantment/bow.yml",
				new File(getEnchantFolder(), File.separator + "bow.yml"));
		ConfigUtils.setupResource(this, "/enchantment/chestplate.yml",
				new File(getEnchantFolder(), File.separator + "chestplate.yml"));
		ConfigUtils.setupResource(this, "/enchantment/helmet.yml",
				new File(getEnchantFolder(), File.separator + "helmet.yml"));
		ConfigUtils.setupResource(this, "/enchantment/hoe.yml",
				new File(getEnchantFolder(), File.separator + "hoe.yml"));
		ConfigUtils.setupResource(this, "/enchantment/leggings.yml",
				new File(getEnchantFolder(), File.separator + "leggings.yml"));
		ConfigUtils.setupResource(this, "/enchantment/mask.yml",
				new File(getEnchantFolder(), File.separator + "mask.yml"));
		ConfigUtils.setupResource(this, "/enchantment/mix.yml",
				new File(getEnchantFolder(), File.separator + "mix.yml"));
		ConfigUtils.setupResource(this, "/enchantment/pickaxe.yml",
				new File(getEnchantFolder(), File.separator + "pickaxe.yml"));
		ConfigUtils.setupResource(this, "/enchantment/set.yml",
				new File(getEnchantFolder(), File.separator + "set.yml"));
		ConfigUtils.setupResource(this, "/enchantment/space.yml",
				new File(getEnchantFolder(), File.separator + "space.yml"));
		ConfigUtils.setupResource(this, "/enchantment/sword.yml",
				new File(getEnchantFolder(), File.separator + "sword.yml"));
		ConfigUtils.setupResource(this, "/bookcraft.yml", new File(getDataFolder(), File.separator + "bookcraft.yml"));
		ConfigUtils.setupResource(this, "/groups.yml", new File(getDataFolder(), File.separator + "groups.yml"));
		ConfigUtils.setupResource(this, "/items.yml", new File(getDataFolder(), File.separator + "items.yml"));
		ConfigUtils.setupResource(this, "/messages.yml", new File(getDataFolder(), File.separator + "messages.yml"));
		ConfigUtils.setupResource(this, "/tinkerer.yml", new File(getDataFolder(), File.separator + "tinkerer.yml"));
		ConfigUtils.setupResource(this, "/vanilla-items.yml",
				new File(getDataFolder(), File.separator + "vanilla-items.yml"));
	}

	public void setupTradeItemCompare() {
		if (Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
			new CustomEnchantmentTradeItemCompare().register();
			new CustomEnchantmentItemDisplaySetup().register();
		}
	}

	public void setupEffect() {
		new EffectAddForeverPotion().register();
		new EffectRemovePotion().register();
		new EffectAddPotion().register();
		new EffectRemoveForeverPotion().register();
		new EffectMessage().register();
		new EffectRemoveTask().register();
		new EffectEnableMultipleArrow().register();
		new EffectDisableMultipleArrow().register();
		new EffectAddAttribute().register();
		new EffectRemoveAttribute().register();
		new EffectHealth().register();
		new EffectFood().register();
		new EffectExp().register();
		new EffectOxygen().register();
		new EffectAbsorptionHeart().register();
		new EffectDurability().register();
		new EffectOnFire().register();
		new EffectPull().register();
		new EffectLightning().register();
		new EffectTeleport().register();
		new EffectPacketParticle().register();
		new EffectActiveAbility().register();
		new EffectDeactiveAbility().register();
        new EffectActiveDash().register();
        new EffectDeactiveDash().register();
        new EffectActiveDoubleJump().register();
        new EffectDeactiveDoubleJump().register();
		new EffectActiveEquipSlot().register();
		new EffectDeactiveEquipSlot().register();
		new EffectAddMobBonus().register();
		new EffectRemoveMobBonus().register();
		new EffectAddBlockBonus().register();
		new EffectRemoveBlockBonus().register();
		new EffectPacketRedstoneParticle().register();
		new EffectExplosion().register();
		new EffectPlaySound().register();
		new EffectNumberStorage().register();
		new EffectTextStorage().register();
		new EffectAddCustomAttribute().register();
		new EffectRemoveCustomAttribute().register();
		new EffectAddFurnaceMining().register();
		new EffectRemoveFurnaceMining().register();
		new EffectAddExplosionMining().register();
		new EffectRemoveExplosionMining().register();
		new EffectAddRandomPotion().register();
		new EffectRemoveRandomPotion().register();
		new EffectAddBlockDropBonusMining().register();
		new EffectRemoveBlockDropBonusMining().register();
		new EffectEnableTelepathy().register();
		new EffectDisableTelepathy().register();
		new EffectFixedPull().register();
		new EffectSummonGuard().register();
		new EffectSummonBabyZombieGuard().register();
		new EffectRemoveGuard().register();
		new EffectSetBlock().register();
		new EffectAdvancedMessage().register();
		new EffectDealDamage().register();
		new EffectSetFlight().register();
		new EffectShootArrow().register();
		new EffectSummonCustomGuard().register();
		new EffectEnableAutoSell().register();
		new EffectDisableAutoSell().register();
        new EffectBlockForeverPotion().register();
        new EffectUnblockForeverPotion().register();
	}

	public void setupCondition() {
		new ConditionEquipSlot().register();
		new ConditionEntityType().register();
		new ConditionExp().register();
		new ConditionFood().register();
		new ConditionFoodPercent().register();
		new ConditionHasEnemy().register();
		new ConditionHealth().register();
		new ConditionHealthPercent().register();
		new ConditionHold().register();
		new ConditionLevel().register();
		new ConditionOxygen().register();
		new ConditionOxygenPercent().register();
		new ConditionNumberStorage().register();
		new ConditionTextStorage().register();
		new ConditionDamageCause().register();
		new ConditionHasDamageCause().register();
		new ConditionOnFire().register();
		new ConditionItemConsume().register();
		new ConditionOutOfSight().register();
		new ConditionCanAttack().register();
		new ConditionFactionRelation().register();
		new ConditionAllowFlight().register();
		new ConditionInFactionTerriority().register();
		new ConditionHasNearbyEnemy().register();
		new ConditionInCombat().register();
		new ConditionOnGround().register();
		new ConditionActiveEquipSlot().register();
		new ConditionOnlyActiveEquip().register();
        new ConditionFakeSource().register();
	}

	public void setupListener() {
		new PlayerListener(this);
		new InventoryListener(this);
		new EntityListener(this);
		new BlockListener(this);
		new CEProtectDeadListener(this);
		new GuardListener(this);
		new BannerListener(this);

		if (Bukkit.getPluginManager().isPluginEnabled("StackMob")) {
			new MobStackDeathListener(this);
		} else {
			new MobDeathListener(this);
		}

		if (Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
			new CMenuListener(this);
		}

		if (Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
			new McMMOListener(this);
		}
	}

	public void setupConfig() {
		FileUtils.createFile(getGroupFile());
		FileUtils.createFile(getItemFile());
		FileUtils.createFile(getVanillaItemFile());
		FileUtils.createFile(getSaveItemFile());
		FileUtils.createFile(getBookCraftFile());
        FileUtils.createFile(getBookUpgradeFile());
        FileUtils.createFile(getBookUpgradeFolder());
		FileUtils.createFile(getTinkererFile());
		FileUtils.createFile(getMessagesFile());
		FileUtils.createFolder(getEnchantFolder());
		FileUtils.createFolder(getMenuFolder());

		this.ceEnchantMap = new CEEnchantMap();
		this.ceGroupMap = new CEGroupMap();
		this.ceItemStorageMap = new CEItemStorageMap();

		CustomEnchantmentMessage.setConfig(new AdvancedFileConfiguration(getMessagesFile()));

		saveDefaultConfig();
		reloadConfig();

		CEConfig config = new CEConfig();
		config.loadConfig(getConfigFile());

		CEGroupConfig groupConfig = new CEGroupConfig();
		groupConfig.loadConfig(getGroupFile());

		CEEnchantConfig enchantConfig = new CEEnchantConfig();
		enchantConfig.loadConfig(getEnchantFolder());

		CEItemConfig ceItemConfig = new CEItemConfig();
		ceItemConfig.loadConfig(getItemFile());

		VanillaItemConfig vanillaItemConfig = new VanillaItemConfig();
		vanillaItemConfig.loadConfig(getVanillaItemFile());

        BookUpgradeMenu.setSettings(null);
		BookcraftConfig bookcraftConfig = new BookcraftConfig();
		bookcraftConfig.loadConfig(getBookCraftFile());

        BookUpgradeConfig bookUpgradeConfig = new BookUpgradeConfig();
        bookUpgradeConfig.loadConfig(getBookUpgradeFile());
        bookUpgradeConfig.loadConfig(getBookUpgradeFolder());

		TinkererConfig tinkererConfig = new TinkererConfig();
		tinkererConfig.loadConfig(getTinkererFile());
	}

	public void setupTask() {
		this.asyncEffectExecuteTask = new EffectExecuteTask(true);
		this.asyncEffectExecuteTask.runTaskTimerAsynchronously(this, 0, 1);

		this.effectExecuteTask = new EffectExecuteTask(false);
		this.effectExecuteTask.runTaskTimer(this, 0, 1);

		this.cePlayerTask = new CEPlayerTask(this);
		this.cePlayerTask.runTaskTimer(this, 0, 20);

		this.ceCallerTask = new CECallerTask(this);
		this.ceCallerTask.runTaskTimer(this, 0, 1);

		this.specialMiningTask = new SpecialMiningTask(this);
		this.specialMiningTask.runTaskTimer(this, 0, 1);

		this.blockTask = new BlockTask(this);
		this.blockTask.runTaskTimer(this, 0, 1);

        this.arrowTask = new ArrowTask();
        this.arrowTask.runTaskTimer(this, 0, 20);

		new BukkitRunnable() {
			public void run() {
				for (CEPlayer cePlayer : getCEPlayerMap().getCEPlayers()) {
					cePlayer.getStorage().getConfig().save();
				}
			}
		}.runTaskTimerAsynchronously(instance, 0, 20 * 60 * 15);
	}

	public void setupPlayers() {
		this.cePlayerMap = new CEPlayerMap();

		new BukkitRunnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					CEPlayer cePlayer = CEAPI.getCEPlayer(player);
					cePlayer.onJoin();
				}
			}
		}.runTaskAsynchronously(this);
	}

    public void setupPlaceholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CustomEnchantmentPlaceholder().register();
        }
    }

	public void setupGuard() {
		this.guardManager = new GuardManager();
		this.guardTask = new GuardTask(this);
		this.guardTask.runTaskTimer(this, 0, 20);
	}

	public void setupCatalog() {
		if (Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
			new CEBookCatalog().register();
		}
	}

	public void addEffectTask(EffectTaskSeparate effectTask) {
		this.asyncEffectExecuteTask.addEffectDataList(effectTask.getEffectAsyncList());
		this.effectExecuteTask.addEffectDataList(effectTask.getEffectList());
	}

	public void removeEffectTask(CEPlayer caller, String name) {
		this.asyncEffectExecuteTask.removeEffectData(caller, name);
		this.effectExecuteTask.removeEffectData(caller, name);
	}

	public void removeEffectTask(String playerName, String name) {
		this.asyncEffectExecuteTask.removeEffectData(playerName, name);
		this.effectExecuteTask.removeEffectData(playerName, name);
	}

	public File getPlayerDataFolder() {
		return new File(getDataFolder(), "player-data");
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

	public File getItemFile() {
		return new File(getDataFolder(), "items.yml");
	}

	public File getVanillaItemFile() {
		return new File(getDataFolder(), "vanilla-items.yml");
	}

	public File getSaveItemFile() {
		return new File(getDataFolder(), "save-items.yml");
	}

	public File getBookCraftFile() {
		return new File(getDataFolder(), "bookcraft.yml");
	}

    public File getBookUpgradeFile() {
        return new File(getDataFolder(), "bookupgrade.yml");
    }

    public File getBookUpgradeFolder() {
        return new File(getDataFolder(), "bookupgrade");
    }

	public File getDatabaseFile() {
		return new File(getDataFolder(), "data.db");
	}

	public File getEnchantFolder() {
		return new File(getDataFolder(), "enchantment");
	}

	public File getMenuFolder() {
		return new File(getDataFolder(), "menu");
	}

	public CEPlayerMap getCEPlayerMap() {
		return cePlayerMap;
	}

	public CEEnchantMap getCEEnchantMap() {
		return ceEnchantMap;
	}

	public CEGroupMap getCEGroupMap() {
		return ceGroupMap;
	}

	public CEItemStorageMap getCEItemStorageMap() {
		return ceItemStorageMap;
	}

	public SpecialMiningTask getSpecialMiningTask() {
		return specialMiningTask;
	}

	public GuardManager getGuardManager() {
		return guardManager;
	}

	public GuardTask getGuardTask() {
		return guardTask;
	}

	public BlockTask getBlockTask() {
		return blockTask;
	}

	public Database getDatabase() {
		return database;
	}

	public static CustomEnchantment instance() {
		return instance;
	}
}