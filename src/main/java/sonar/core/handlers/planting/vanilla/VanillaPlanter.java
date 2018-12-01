package sonar.core.handlers.planting.vanilla;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import sonar.core.SonarConstants;
import sonar.core.api.asm.ASMPlanter;
import sonar.core.api.planting.ISonarPlanter;
import sonar.core.common.item.SonarSeeds;

@ASMPlanter(modid = SonarConstants.MODID, priority = 10)
public class VanillaPlanter implements ISonarPlanter {

	@Override
	public boolean canTierPlant(ItemStack seed, int tier){
		if(seed.getItem() instanceof SonarSeeds){
			return ((SonarSeeds)seed.getItem()).canTierUse(tier);
		}
		return true;
	}

	@Override
	public boolean isPlantable(ItemStack seed) {
		return seed.getItem() instanceof IPlantable;
	}

	@Override
	public boolean canPlant(ItemStack seed, World world, BlockPos pos) {
		if(world.isAirBlock(pos)){
			BlockPos offsetPos = pos.offset(EnumFacing.DOWN);
			IBlockState state = world.getBlockState(offsetPos);
			return state.getBlock().canSustainPlant(state, world, offsetPos, EnumFacing.UP, (IPlantable)seed.getItem());
		}
		return false;
	}

	@Override
	public boolean doPlant(ItemStack seed, World world, BlockPos pos) {
		IPlantable plant = (IPlantable)seed.getItem();
		world.setBlockState(pos, plant.getPlant(world, pos));
		return true;
	}
}
