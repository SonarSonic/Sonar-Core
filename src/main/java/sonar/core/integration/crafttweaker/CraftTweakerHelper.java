package sonar.core.integration.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeItemStack;
import sonar.core.recipes.RecipeOreStack;

import javax.annotation.Nullable;

public class CraftTweakerHelper {

    @Nullable
    public static ISonarRecipeObject convertItemIngredient(IIngredient i){
        if(i.getItems().size() > 0){
            if(i instanceof IOreDictEntry){
                IOreDictEntry oreDict = (IOreDictEntry) i;
                return new RecipeOreStack(oreDict.getName(), oreDict.getAmount());
            }
            return new RecipeItemStack(CraftTweakerMC.getItemStack(i), false);
        }
        return null;
    }

}
