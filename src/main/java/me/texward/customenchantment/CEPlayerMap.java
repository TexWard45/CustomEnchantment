package me.texward.customenchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.texward.customenchantment.player.CEPlayer;

public class CEPlayerMap {
	private ConcurrentHashMap<UUID, CEPlayer> map = new ConcurrentHashMap<UUID, CEPlayer>();

	/**
	 * Get CEPlayer by UUID.
	 * 
	 * @param uuid uuid of player
	 * @return cePlayer (null if not exist)
	 */
	public CEPlayer getCEPlayer(UUID uuid) {
		return map.get(uuid);
	}

	/**
	 * Get CEPlayer by Player.
	 * <p>
	 * WARNING: This method do not automatically register CEPlayer for you. You
	 * should use onJoin or register method to register player.
	 * 
	 * @param player
	 * @return cePlayer even thought register or not
	 */
	public CEPlayer getCEPlayer(Player player) {
		if (player == null) {
			return null;
		}

		UUID uuid = player.getUniqueId();

		CEPlayer cePlayer = getCEPlayer(uuid);
		if (cePlayer == null) {
			cePlayer = new CEPlayer(player);
		}

		return cePlayer;
	}

	public void registerCEPlayer(CEPlayer cePlayer) {
		if (cePlayer == null) {
			return;
		}

		map.put(cePlayer.getPlayer().getUniqueId(), cePlayer);
	}

	public void unregisterCEPlayer(CEPlayer cePlayer) {
		if (cePlayer == null) {
			return;
		}

		map.remove(cePlayer.getPlayer().getUniqueId());
	}

	public List<CEPlayer> getCEPlayers() {
		return new ArrayList<CEPlayer>(map.values());
	}
}
