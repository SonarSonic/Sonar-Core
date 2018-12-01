package sonar.core.handlers.planting.vanilla;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarConstants;
import sonar.core.SonarCore;
import sonar.core.api.asm.ASMFertiliser;
import sonar.core.api.planting.ISonarFertiliser;

@ASMFertiliser(modid = SonarConstants.MODID, priority = 10)
public class VanillaFertiliser implements ISonarFertiliser {

	@Override
	public boolean canFertilise(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof IGrowable;
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state) {
		return ((IGrowable) state.getBlock()).canGrow(world, pos, state, false);
	}

	@Override
	public boolean grow(World world, BlockPos pos, IBlockState state) {
		((IGrowable) state.getBlock()).grow(world, SonarCore.rand, pos, state);
		return true;
	}
}
