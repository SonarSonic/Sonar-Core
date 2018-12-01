package sonar.core.handlers.planting.vanilla;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarConstants;
import sonar.core.api.asm.ASMHarvester;
import sonar.core.api.planting.ISonarHarvester;

@ASMHarvester(modid = SonarConstants.MODID, priority = 10)
public class VanillaHarvester implements ISonarHarvester {

	@Override
	public boolean canHarvest(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof IGrowable;
	}

	@Override
	public boolean isReady(World world, BlockPos pos, IBlockState state) {
		return !((IGrowable) state.getBlock()).canGrow(world, pos, state, false);
	}

	@Override
	public boolean doHarvest(NonNullList<ItemStack> drops, World world, BlockPos pos, IBlockState state, int fortune, boolean keepBlock) {
		world.getBlockState(pos).getBlock().getDrops(drops, world, pos, state, fortune);
		if (!keepBlock) {
			world.setBlockToAir(pos);
		}else{
			//remove duplicates?
			world.setBlockState(pos, state.getBlock().getDefaultState());
		}
		return true;
	}
}
