package sonar.core.api.planting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISonarHarvester {
	
    boolean canHarvest(World world, BlockPos pos, IBlockState state);
	
    boolean isReady(World world, BlockPos pos, IBlockState state);

    boolean doHarvest(NonNullList<ItemStack> drops, World world, BlockPos pos, IBlockState state, int fortune, boolean keepBlock);
}
