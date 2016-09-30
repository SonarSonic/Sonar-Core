package sonar.core.recipes;

public enum RecipeObjectType {
	INPUT, OUTPUT;

	public boolean checkStackSize(int required, int given) {
		return this == RecipeObjectType.INPUT ? given >= required : given == required;
	}
}
