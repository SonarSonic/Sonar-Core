package sonar.core.helpers;

import java.util.List;

import sonar.core.integration.jei.JEIRecipe;

public interface ISonarRecipeHelper<T extends JEIRecipe> {
	public String getRecipeID();

	public List<T> getJEIRecipes();	
	
}
