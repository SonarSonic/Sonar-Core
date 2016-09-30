package sonar.core.recipes;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public interface ISonarRecipe {
	public List<ISonarRecipeObject> inputs();

	public List<ISonarRecipeObject> outputs();

	public boolean matchingInputs(Object[] inputs);

	public boolean matchingOutputs(Object[] outputs);	

	public boolean canUseRecipe(EntityPlayer player);
}