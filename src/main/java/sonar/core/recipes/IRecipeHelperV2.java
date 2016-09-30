package sonar.core.recipes;

import java.util.ArrayList;

public interface IRecipeHelperV2<T extends ISonarRecipe> {

	public ArrayList<T> getRecipes();
	
	public String getRecipeID();
}
