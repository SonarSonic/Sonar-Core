package sonar.core.recipes;

import java.util.ArrayList;

public interface IRecipeHelperV2<T extends ISonarRecipe> {

    ArrayList<T> getRecipes();
	
    String getRecipeID();
}
