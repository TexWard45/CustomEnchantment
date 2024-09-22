package com.bafmc.customenchantment.player;

import java.io.File;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.FileUtils;

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
