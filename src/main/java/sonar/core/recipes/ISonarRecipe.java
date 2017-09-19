package sonar.core.recipes;

import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public interface ISonarRecipe {
    List<ISonarRecipeObject> inputs();

    List<ISonarRecipeObject> outputs();

    boolean matchingInputs(Object[] inputs);

    boolean matchingOutputs(Object[] outputs);

    boolean canUseRecipe(EntityPlayer player);
}