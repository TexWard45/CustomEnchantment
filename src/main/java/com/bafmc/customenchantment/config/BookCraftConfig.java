package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.api.EconomyAPI;
import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.Path;
import com.bafmc.bukkit.config.annotation.ValueType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Getter
public class BookCraftConfig {
	@Path("money-require")
	@ValueType(Double.class)
	private Map<String, Double> moneyGroupRequireMap = new LinkedHashMap<>();

	public double getMoneyRequire(String groupName) {
		return moneyGroupRequireMap.containsKey(groupName) ? moneyGroupRequireMap.get(groupName) : 0;
	}

	public boolean isRequireMoney(Player player, String groupName) {
		return getMoneyRequire(groupName) <= EconomyAPI.getMoney(player);
	}

	public boolean payMoney(Player player, String groupName) {
		if (!isRequireMoney(player, groupName)) {
			return false;
		}
		EconomyAPI.takeMoney(player, getMoneyRequire(groupName));
		return true;
	}

	//FastCraft Zone
	public boolean isRequireMoney(Player player, String groupName, double amount) {
		return (getMoneyRequire(groupName)*amount) <= EconomyAPI.getMoney(player);
	}

	public boolean payMoney(Player player, String groupName, double amount) {
		if (!isRequireMoney(player, groupName, amount)) {
			return false;
		}
		EconomyAPI.takeMoney(player, getMoneyRequire(groupName)*amount);
		return true;
	}
}
