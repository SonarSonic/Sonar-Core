package sonar.core.api.planting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISonarFertiliser {

	boolean canFertilise(World world, BlockPos pos, IBlockState state);
	
	boolean canGrow(World world, BlockPos pos, IBlockState state);

	boolean grow(World world, BlockPos pos, IBlockState state);
}
