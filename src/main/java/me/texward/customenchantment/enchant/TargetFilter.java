package me.texward.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TargetFilter {
	private boolean enable;
	private Target target;
	private boolean exceptPlayer;
	private boolean exceptEnemy;
	private double minDistance;
	private double maxDistance;
	private int minTarget;
	private int maxTarget;

	public TargetFilter(boolean enable, Target target, boolean exceptPlayer, boolean exceptEnemy, double minDistance,
			double maxDistance, int minTarget, int maxTarget) {
		this.enable = enable;
		this.target = target;
		this.exceptPlayer = exceptPlayer;
		this.exceptEnemy = exceptEnemy;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.minTarget = minTarget;
		this.maxTarget = maxTarget;
	}

	public List<Player> getTargetsByPlayer(Player player, Player enemy) {
		player = (this.target == Target.PLAYER ? player : enemy);
		enemy = (this.target == Target.PLAYER ? enemy : player);

		List<Player> playerTargets = new ArrayList<Player>();

		World world = player.getLocation().getWorld();
		for (Player playerTarget : Bukkit.getOnlinePlayers()) {
			if (playerTargets.size() == maxTarget) {
				break;
			}

			if (exceptPlayer && player != null && playerTarget == player) {
				continue;
			}

			if (exceptEnemy && enemy != null && playerTarget == enemy) {
				continue;
			}
			
			if (playerTarget.getWorld() != world) {
				continue;
			}

			double distance = playerTarget.getLocation().distance(player.getLocation());
			if (distance < minDistance || distance > maxDistance) {
				continue;
			}

			playerTargets.add(playerTarget);
		}

		if (playerTargets.size() < minTarget) {
			return new ArrayList<Player>();
		}
		return playerTargets;
	}

	public boolean isEnable() {
		return enable;
	}

	public boolean isExceptPlayer() {
		return exceptPlayer;
	}

	public boolean isExceptEnemy() {
		return exceptEnemy;
	}

	public double getMinDistance() {
		return minDistance;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public int getMinTarget() {
		return minTarget;
	}

	public int getMaxTarget() {
		return maxTarget;
	}
}
