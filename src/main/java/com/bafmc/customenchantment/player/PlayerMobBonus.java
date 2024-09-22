package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.player.bonus.EntityTypeBonus;

public class PlayerMobBonus extends CEPlayerExpansion {
	private EntityTypeBonus expBonus;
	private EntityTypeBonus moneyBonus;
	private EntityTypeBonus msExpBonus;

	public PlayerMobBonus(CEPlayer cePlayer) {
		super(cePlayer);
		this.expBonus = new EntityTypeBonus();
		this.moneyBonus = new EntityTypeBonus();
		this.msExpBonus = new EntityTypeBonus();
	}

	public void onJoin() {

	}

	public void onQuit() {

	}

	public EntityTypeBonus getExpBonus() {
		return expBonus;
	}

	public EntityTypeBonus getMoneyBonus() {
		return moneyBonus;
	}

	public EntityTypeBonus getMobSlayerExpBonus() {
		return msExpBonus;
	}

}
