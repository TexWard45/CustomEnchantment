package com.bafmc.customenchantment.enchant;

public class Cooldown implements Cloneable {
	private long start;
	private long end;
	private long countdown;

	public Cooldown(long cooldown) {
		this.countdown = cooldown;
	}

	public void start() {
		start = System.currentTimeMillis();
		end = start + countdown;
	}

	public void stop() {
		start = 0;
		end = 0;
	}

	public boolean isInCooldown() {
		if (start == 0 || System.currentTimeMillis() > end) {
			return false;
		}
		return true;
	}

	public void setStart(Long start, Long end) {
		this.start = start;
		this.end = end;
	}

	public Long getStart() {
		return start;
	}

	public Long getEnd() {
		return end;
	}

	public void setCountdown(long countdown) {
		this.countdown = countdown;
	}

	public long getCountdown() {
		return countdown;
	}

	@Override
	public Cooldown clone() {
		try {
			return (Cooldown) super.clone();
		} catch (CloneNotSupportedException e) {
			return new Cooldown(countdown);
		}
	}

	@Override
	public String toString() {
		return "Cooldown [start=" + start + ", end=" + end + ", countdown=" + countdown + "]";
	}
}
