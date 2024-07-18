package me.texward.customenchantment.enchant.effect;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.texwardlib.util.EnumUtils;
import me.texward.texwardlib.util.RandomRange;

public class EffectPlaySound extends EffectHook {
	private List<Sound> sound;
	private RandomRange volumn;
	private RandomRange pitch;
	private double distance;

	public String getIdentify() {
		return "PLAY_SOUND";
	}

	public void setup(String[] args) {
		this.sound = EnumUtils.getEnumListByString(Sound.class, args[0]);
		this.volumn = new RandomRange(args[1]);
		this.pitch = new RandomRange(args[2]);
		if (args.length > 3) {
			this.distance = Double.valueOf(args[3]);
		}
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		float volumn = (float) this.volumn.getValue();
		float pitch = (float) this.pitch.getValue();

		if (distance != 0) {
			Location location = player.getLocation();
			String world = location.getWorld().getName();
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (target.getWorld().getName().equals(world)
						&& location.distance(target.getLocation()) <= this.distance) {
					for (Sound sound : this.sound) {
						target.playSound(player.getLocation(), sound, volumn, pitch);
					}
				}
			}
		} else {
			for (Sound sound : this.sound) {
				player.playSound(player.getLocation(), sound, volumn, pitch);
			}
		}
	}
}
