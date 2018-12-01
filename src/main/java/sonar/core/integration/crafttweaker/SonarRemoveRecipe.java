package sonar.core.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.recipes.RecipeObjectType;

import java.util.ArrayList;
import java.util.List;

public class SonarRemoveRecipe implements IAction {

    public RecipeHelperV2 recipes;
    public ISonarRecipe recipe;
    public boolean valid = true;

    public SonarRemoveRecipe(RecipeHelperV2 recipes, RecipeObjectType type, List<IIngredient> ingredients){
        this.recipes = recipes;
        List<ISonarRecipeObject> sonar_objects = new ArrayList<>();
        for(IIngredient i : ingredients){
            ISonarRecipeObject obj = CraftTweakerHelper.convertItemIngredient(i);
            if(obj == null){
                valid = false;
                CraftTweakerAPI.logError(String.format("%s: INVALID ITEM : %s", recipes.getRecipeID(), i));
            }else{
                sonar_objects.add(obj);
            }
        }
        if(valid) {
            switch(type){
                case INPUT:
                    this.recipe = recipes.getRecipeFromInputs(null, sonar_objects.toArray());
                    break;
                case OUTPUT:
                    this.recipe = recipes.getRecipeFromOutputs(null, sonar_objects.toArray());
                    break;
            }
        }
    }

    @Override
    public void apply() {
        if(valid && recipe != null) {
            boolean result = recipes.removeRecipe(recipe);
            if (!result){
                CraftTweakerAPI.logError(String.format("%s: Removing Recipe - Failed to remove recipe %s", recipes.getRecipeID()));
            }
        }
    }

    @Override
    public String describe() {
        if(!valid || recipe == null ){
            return "INVALID RECIPE";
        }
        return String.format("Removing %s recipe (%s = %s)", recipes.getRecipeID(), recipe.inputs(), recipe.outputs());
    }
}
