package com.bafmc.customenchantment.enchant;

public enum StepAction {
	CONTINUE(true, false), BREAK(false, true);

	private boolean isContinue;
	private boolean isBreak;

	private StepAction(boolean isContinue, boolean isBreak) {
		this.isContinue = isContinue;
		this.isBreak = isBreak;
	}
	
	public static StepAction valueOf(boolean isBreak) {
		return isBreak ? StepAction.BREAK : StepAction.CONTINUE;
	}
	
	public static StepAction valueOf(boolean isBreak, StepAction oldStepAction) {
		if (oldStepAction.isBreak) {
			return StepAction.BREAK;
		}
		return isBreak ? StepAction.BREAK : StepAction.CONTINUE;
	}

	public boolean isContinue() {
		return isContinue;
	}

	public boolean isBreak() {
		return isBreak;
	}
}