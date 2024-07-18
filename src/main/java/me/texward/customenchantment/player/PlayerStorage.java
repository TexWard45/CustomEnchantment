package me.texward.customenchantment.player;

import java.io.File;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.texwardlib.configuration.AdvancedFileConfiguration;
import me.texward.texwardlib.util.FileUtils;

public class PlayerStorage extends CEPlayerExpansion {
	private AdvancedFileConfiguration config;

	public PlayerStorage(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
		this.setup();
	}

	public void onQuit() {
		this.config.save();
	}

	public void setup() {
		File file = getPlayerDataFile();
		FileUtils.createFile(file);
		this.config = new AdvancedFileConfiguration(file);
	}

	public AdvancedFileConfiguration getConfig() {
		return config;
	}

	public File getPlayerDataFile() {
		return CustomEnchantment.instance().getPlayerDataFile(player);
	}
}
