package me.texward.customenchantment.enchant;

public abstract class ConditionHook implements Cloneable {
	private ConditionSettings settings;

	/**
	 * Update target by settings and execute based on data
	 * 
	 * @param data
	 */
	public boolean updateAndMatch(CEFunctionData data) {
		Target target = getSettings().getTarget();
		
		if (data.getTarget() != target) {
			data = data.clone();
			data.setTarget(target);
		}

		return settings.isNegative() ? !match(data) : match(data);
	}
	
	public abstract String getIdentify();

	public abstract void setup(String[] args);

	/**
	 * Check condition is passed by data
	 * 
	 * @param data
	 * @return
	 */
	public abstract boolean match(CEFunctionData data);

	public boolean register() {
		return Condition.register(this);
	}

	public ConditionHook clone() {
		try {
			return (ConditionHook) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public ConditionSettings getSettings() {
		return settings;
	}

	public void setSettings(ConditionSettings settings) {
		this.settings = settings;
	}

}
