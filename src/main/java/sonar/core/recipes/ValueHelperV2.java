package sonar.core.recipes;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class ValueHelperV2 extends DefinedRecipeHelper<DefaultSonarRecipe.Value> {

	public ValueHelperV2(int inputSize, int outputSize, boolean shapeless) {
		super(inputSize, outputSize, shapeless);
	}

    @Override
	public DefaultSonarRecipe.Value buildRecipe(ArrayList<ISonarRecipeObject> recipeInputs, ArrayList<ISonarRecipeObject> recipeOutputs, List additionals, boolean shapeless) {
		return new DefaultSonarRecipe.Value(recipeInputs, recipeOutputs, shapeless, (int) additionals.get(0));
	}

	public abstract static class SimpleValueHelper extends ValueHelperV2 {

		public SimpleValueHelper() {
			super(1, 0, false);
		}

		public int getValue(EntityPlayer player, Object... obj) {
			DefaultSonarRecipe.Value recipe = getRecipeFromInputs(player, obj);
			return recipe == null ? 0 : recipe.getValue();
		}
	}
}
