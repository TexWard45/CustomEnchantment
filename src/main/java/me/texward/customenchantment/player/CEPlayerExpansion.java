package me.texward.customenchantment.player;

import org.bukkit.entity.Player;

/**
 * Fast access to CEPlayer's variable and method
 * 
 * @author nhata
 *
 */
public abstract class CEPlayerExpansion implements ICEPlayerEvent {
	protected CEPlayer cePlayer;
	protected Player player;

	public CEPlayerExpansion(CEPlayer cePlayer) {
		this.cePlayer = cePlayer;
		this.player = cePlayer.getPlayer();
	}

	public CEPlayer getCEPlayer() {
		return cePlayer;
	}

	public Player getPlayer() {
		return player;
	}

	public abstract void onJoin();

	public abstract void onQuit();
}
