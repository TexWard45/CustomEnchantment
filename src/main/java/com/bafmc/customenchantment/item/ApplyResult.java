package com.bafmc.customenchantment.item;

public enum ApplyResult {
	/** 
	 * Nothing happen
	 */
	NOTHING, 
	/** 
	 * Cancel event
	 */
	CANCEL, 
	/** 
	 * Remove cursor item, update click item
	 */
	SUCCESS, 
	/** 
	 * Remove cursor item, not update click item
	 */
	FAIL,
	/** 
	 * Remove cursor item, and update click item
	 */
	FAIL_AND_UPDATE,
	/** 
	 * Remove cursor and click item
	 */
	DESTROY;
}
