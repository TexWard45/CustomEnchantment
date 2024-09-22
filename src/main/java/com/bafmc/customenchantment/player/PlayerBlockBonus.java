package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.player.bonus.BlockBonus;

public class PlayerBlockBonus extends CEPlayerExpansion {
	private BlockBonus expBonus;
	private BlockBonus moneyBonus;

	public PlayerBlockBonus(CEPlayer cePlayer) {
		super(cePlayer);
		this.expBonus = new BlockBonus();
		this.moneyBonus = new BlockBonus();
	}

	public void onJoin() {

	}

	public void onQuit() {

	}

	public BlockBonus getExpBonus() {
		return expBonus;
	}

	public BlockBonus getMoneyBonus() {
		return moneyBonus;
	}

}
