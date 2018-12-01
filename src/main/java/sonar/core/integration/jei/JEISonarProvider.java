package sonar.core.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import sonar.core.handlers.inventories.ItemStackHelper;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

public class JEISonarProvider {

    public ItemStack catalyst;
    public RecipeHelperV2 recipes;
    public String background;
    public String modid;
    public Class<? extends IRecipeWrapper> recipeClass;
    public IRecipeFactory recipeFactory;
    public ICategoryFactory categoryFactory;

    public JEISonarProvider(RecipeHelperV2 recipes, Object catalyst, Class<? extends IRecipeWrapper> recipeClass, IRecipeFactory recipeFactory, ICategoryFactory categoryFactory, String background, String modid){
        this.recipes = recipes;
        this.catalyst = ItemStackHelper.getOrCreateStack(catalyst);
        this.recipeClass = recipeClass;
        this.recipeFactory = recipeFactory;
        this.categoryFactory = categoryFactory;
        this.background = background;
        this.modid = modid;
    }

    public String getID(){
        return recipes.getRecipeID();
    }

    public interface IRecipeFactory{
        JEISonarRecipe create(RecipeHelperV2 helper, ISonarRecipe recipe);
    }

    public interface ICategoryFactory{
        JEISonarCategory create(IGuiHelper helper, JEISonarProvider handler);
    }


}
