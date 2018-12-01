package sonar.core.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class JEISonarPlugin implements IModPlugin {

    public List<JEISonarProvider> providers = new ArrayList<>();

    public JEISonarProvider p(RecipeHelperV2 recipes, Object catalyst, Class<? extends IRecipeWrapper> recipeClass, JEISonarProvider.IRecipeFactory recipeFactory, JEISonarProvider.ICategoryFactory categoryFactory, String background, String modid){
        JEISonarProvider provider = new JEISonarProvider(recipes, catalyst, recipeClass, recipeFactory, categoryFactory, background, modid);
        providers.add(provider);
        return provider;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        providers.forEach(p -> registry.addRecipeCategories(p.categoryFactory.create(guiHelper, p)));
    }

    @Override
    public void register(IModRegistry registry) {
        providers.forEach(p -> {
            registry.addRecipes(getJEIRecipes(p), p.recipes.getRecipeID());
            registry.addRecipeCatalyst(p.catalyst, p.recipes.getRecipeID());
        });
    }

    private Collection<JEISonarRecipe> getJEIRecipes(JEISonarProvider provider) {
        List<JEISonarRecipe> jei_recipes = new ArrayList<>();
        for (Object recipe : provider.recipes.getRecipes()) {
            jei_recipes.add(provider.recipeFactory.create(provider.recipes, (ISonarRecipe) recipe));
        }
        return jei_recipes;
    }
}
