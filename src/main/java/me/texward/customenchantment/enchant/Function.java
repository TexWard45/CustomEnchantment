package me.texward.customenchantment.enchant;

public class Function {
	private Condition condition;
	private Effect effect;
	private CEFunctionData data;

	public Function(Condition condition, Effect effect, CEFunctionData data) {
		this.condition = condition;
		this.effect = effect;
		this.data = data;
	}

	public void call() {
		if (condition.check(data)) {
			effect.execute(data);
		}
	}

	public Condition getCondition() {
		return condition;
	}

	public Effect getEffect() {
		return effect;
	}

	public CEFunctionData getData() {
		return data;
	}
}
