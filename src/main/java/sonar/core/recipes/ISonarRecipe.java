package sonar.core.recipes;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public interface ISonarRecipe {
    List<ISonarRecipeObject> inputs();

    List<ISonarRecipeObject> outputs();

    boolean matchingInputs(Object[] inputs);

    boolean matchingOutputs(Object[] outputs);

    boolean canUseRecipe(EntityPlayer player);
}