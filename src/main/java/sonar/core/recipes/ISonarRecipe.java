package sonar.core.recipes;

import java.util.List;

public interface ISonarRecipe {
	public List inputs();

	public List outputs();

	public boolean matchingInputs(Object... inputs);

	public boolean matchingOutputs(Object... outputs);
}