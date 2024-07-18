package me.texward.customenchantment.enchant.effect;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.texward.customenchantment.api.LocationFormat;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.texwardlib.util.RandomRange;

public class EffectExplosion extends EffectHook {
	private String format;
	private RandomRange power;
	private boolean setFire;
	private boolean breakBlock;

	public boolean isAsync() {
		return false;
	}

	public String getIdentify() {
		return "EXPLOSION";
	}

	public void setup(String[] args) {
		this.format = args[0];
		this.power = new RandomRange(args[1]);
		this.setFire = Boolean.valueOf(args[2]);
		this.breakBlock = Boolean.valueOf(args[3]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		LivingEntity enemy = data.getEnemyLivingEntity();

		Location location = new LocationFormat(format).getLocation(player, enemy);
		if (location != null)
			location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(),
					(float) power.getIntValue(), setFire, breakBlock);
	}
}
