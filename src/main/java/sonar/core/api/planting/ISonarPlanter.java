package sonar.core.api.planting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISonarPlanter {

    default boolean canTierPlant(ItemStack seed, int tier){ return true;};

    boolean isPlantable(ItemStack seed);

    boolean canPlant(ItemStack seed, World world, BlockPos pos);

    boolean doPlant(ItemStack seed, World world, BlockPos pos);
}
