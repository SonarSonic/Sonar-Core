package sonar.core.integration.crafttweaker;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import sonar.core.recipes.DefinedRecipeHelper;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.recipes.ValueHelperV2;

import java.util.ArrayList;
import java.util.List;

public class SonarAddRecipe<HELPER extends RecipeHelperV2> implements IAction {

    public HELPER recipes;
    public List<ISonarRecipeObject> sonar_inputs = new ArrayList<>();
    public List<ISonarRecipeObject> sonar_outputs = new ArrayList<>();
    public boolean valid = true;

    public SonarAddRecipe(HELPER recipes, List<IIngredient> inputs, List<IIngredient> outputs){
        this.recipes = recipes;
        for(IIngredient i : inputs){
            ISonarRecipeObject obj = CraftTweakerHelper.convertItemIngredient(i);
            if(obj == null){
                valid = false;
                CraftTweakerAPI.logError(String.format("%s: INVALID INPUT: %s", recipes.getRecipeID(), i));
            }else{
                sonar_inputs.add(obj);
            }
        }
        for(IIngredient i : outputs){
            ISonarRecipeObject obj = CraftTweakerHelper.convertItemIngredient(i);
            if(obj == null){
                valid = false;
                CraftTweakerAPI.logError(String.format("%s: INVALID OUTPUT : %s", recipes.getRecipeID(), i));
            }else{
                sonar_outputs.add(obj);
            }
        }
    }

    @Override
    public void apply() {
        if(valid){
            boolean isShapeless = !(recipes instanceof DefinedRecipeHelper) || ((DefinedRecipeHelper) recipes).shapeless;
            recipes.addRecipe(recipes.buildRecipe(sonar_inputs, sonar_outputs, new ArrayList<>(), isShapeless));
        }
    }

    @Override
    public String describe() {
        return String.format("Adding %s recipe (%s = %s)", recipes.getRecipeID(), RecipeHelperV2.getValuesFromList(sonar_inputs), RecipeHelperV2.getValuesFromList(sonar_outputs));
    }

    public static class Value extends SonarAddRecipe<ValueHelperV2> {

        public int recipeValue;

        public Value(ValueHelperV2 helper, List<IIngredient> inputs, List<IIngredient> outputs, int recipeValue) {
            super(helper, inputs, outputs);
            this.recipeValue = recipeValue;
        }

        @Override
        public void apply() {
            if(valid){
                recipes.addRecipe(recipes.buildRecipe(sonar_inputs, sonar_outputs, Lists.newArrayList(recipeValue), recipes.shapeless));
            }
        }
    }
}
