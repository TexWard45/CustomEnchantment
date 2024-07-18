package me.texward.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.texwardlib.util.DamageCalculator;
import me.texward.texwardlib.util.RandomRange;

public class EffectDealDamage extends EffectHook {
	private String format;
	private boolean potionAttacker;
	private boolean potionDefender;
	private boolean armorEnable;
	private boolean defensePointsEnable;
	private boolean toughnessEnable;

	public String getIdentify() {
		return "DEAL_DAMAGE";
	}

	public void setup(String[] args) {
		this.format = args[0];
		this.potionAttacker = Boolean.valueOf(args[1]);
		this.potionDefender = Boolean.valueOf(args[2]);
		this.armorEnable = Boolean.valueOf(args[3]);
		this.defensePointsEnable = Boolean.valueOf(args[4]);
		this.toughnessEnable = Boolean.valueOf(args[5]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		LivingEntity enemy = data.getEnemyLivingEntity();

		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		String format = this.format;
		Map<String, String> map = CEPlaceholder.getCEFunctionDataPlaceholder(format, data);
		map.put("%player_value%", String.valueOf(player.getHealth()));
		map.put("%enemy_value%", enemy != null ? String.valueOf(data.getEnemyLivingEntity().getHealth()) : "0");
		map.put("%player_max_value%", String.valueOf(player.getMaxHealth()));
		map.put("%enemy_max_value%", enemy != null ? String.valueOf(enemy.getMaxHealth()) : "0");

		format = CEPlaceholder.setPlaceholder(format, map);

		double defaultValue = player.getHealth();
		double currentValue = new RandomRange(format).getValue();
		currentValue = DamageCalculator.getDameDeal(currentValue, player, enemy, potionAttacker, potionDefender,
				armorEnable, defensePointsEnable, toughnessEnable);
		
		currentValue = defaultValue - currentValue;
		if (currentValue < 0) {
			currentValue = 0;
		}

		if (cePlayer.getDeathTime() != data.getDeathTime() || player.isDead()) {
			return;
		}

		if (currentValue <= 0) {
			cePlayer.setDeathTimeBefore(true);
			cePlayer.setDeathTime(cePlayer.getDeathTime() + 1);

			new BukkitRunnable() {
				public void run() {
					player.setHealth(0);
				}
			}.runTask(CustomEnchantment.instance());
		} else {
			player.setHealth(Math.min(player.getMaxHealth(), currentValue));
		}
	}
}
