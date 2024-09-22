package com.bafmc.customenchantment.enchant;

public interface IEffectAction {
	/**
	 * Update target by settings and execute based on data
	 * 
	 * @param data
	 */
	public void updateAndExecute(CEFunctionData data);

	/**
	 * Execute based on data
	 * 
	 * @param data
	 */
	public void execute(CEFunctionData data);
}
