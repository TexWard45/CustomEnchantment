package me.texward.customenchantment.enchant;

public class EffectSettings implements Cloneable {
	private String name;
	private Target target;
	private Target targetOther;
	private TargetFilter targetFilter;
	private long delay;
	private long period;
	private boolean effectAfterDead;

	public EffectSettings() {
	}

	public EffectSettings(String name, Target target, Target targetOther, TargetFilter targetFilter, long delay,
			long period, boolean effectAfterDead) {
		this.name = name;
		this.target = target;
		this.targetOther = targetOther;
		this.targetFilter = targetFilter;
		this.delay = delay;
		this.period = period;
		this.effectAfterDead = effectAfterDead;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public Target getTargetOther() {
		return targetOther;
	}

	public void setTargetOther(Target targetOther) {
		this.targetOther = targetOther;
	}

	public TargetFilter getTargetFilter() {
		return targetFilter;
	}

	public void setTargetFilter(TargetFilter targetFilter) {
		this.targetFilter = targetFilter;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public boolean isEffectAfterDead() {
		return effectAfterDead;
	}

	public void setEffectAfterDead(boolean effectAfterDead) {
		this.effectAfterDead = effectAfterDead;
	}

	public EffectSettings clone() {
		try {
			return (EffectSettings) super.clone();
		} catch (CloneNotSupportedException e) {
			return new EffectSettings(name, target, targetOther, targetFilter, delay, period, effectAfterDead);
		}
	}
}
