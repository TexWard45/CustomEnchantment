package me.texward.customenchantment.api;

public enum CompareOperation {
	SMALLER("<"), BIGGER(">"), NOT_EQUALS("!="), EQUALS("="), EQUALSIGNORECASE("=="), SMALLEREQUALS("<="),
	BIGGEREQUALS(">=");

	public String operation;

	CompareOperation(String operation) {
		this.operation = operation;
	}

	public static CompareOperation getOperation(String arg0) {
		for (CompareOperation m : CompareOperation.values()) {
			if (m.operation.equals(arg0)) {
				return m;
			}
		}
		return null;
	}

	public static boolean compare(double arg0, double arg1, CompareOperation m) {
		switch (m) {
		case BIGGER:
			return arg0 > arg1;
		case EQUALS:
			return arg0 == arg1;
		case EQUALSIGNORECASE:
			return arg0 == arg1;
		case SMALLER:
			return arg0 < arg1;
		case BIGGEREQUALS:
			return arg0 >= arg1;
		case SMALLEREQUALS:
			return arg0 <= arg1;
		default:
			return false;
		}
	}

	public static boolean compare(int arg0, int arg1, CompareOperation m) {
		switch (m) {
		case BIGGER:
			return arg0 > arg1;
		case EQUALS:
			return arg0 == arg1;
		case EQUALSIGNORECASE:
			return arg0 == arg1;
		case SMALLER:
			return arg0 < arg1;
		case BIGGEREQUALS:
			return arg0 >= arg1;
		case SMALLEREQUALS:
			return arg0 <= arg1;
		default:
			return false;
		}
	}
}
