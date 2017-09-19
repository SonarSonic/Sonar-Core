package sonar.core.integration.jei;

import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

public interface ISonarJEIRecipeBuilder {

    Object buildRecipe(ISonarRecipe recipe, RecipeHelperV2<ISonarRecipe> helper);
}
