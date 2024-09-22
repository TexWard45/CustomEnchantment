package com.bafmc.customenchantment.player;

public class PlayerNameTag extends CEPlayerExpansion {
	private String display;

	public PlayerNameTag(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
		this.display = null;
	}

	public void onQuit() {

	}

	public String getDisplay() {
		return display;
	}
	
	public void setDisplay(String display) {
		this.display = display;
	}
}
