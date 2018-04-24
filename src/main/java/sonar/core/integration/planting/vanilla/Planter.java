package sonar.core.integration.planting.vanilla;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import sonar.core.common.item.SonarSeeds;
import sonar.core.integration.planting.IPlanter;

public class Planter implements IPlanter {

	@Override
	public boolean isLoadable() {
		return true;
	}

	@Override
	public String getName() {
		return "Vanilla Planter";
	}

	@Override
	public boolean canTierPlant(ItemStack stack, int tier) {
		if(stack.getItem() instanceof SonarSeeds){
			return ((SonarSeeds)stack.getItem()).canTierUse(tier);
		}
        return stack.getItem() instanceof IPlantable;
	}

	@Override
	public EnumPlantType getPlantType(ItemStack stack, World world, BlockPos pos) {
		IPlantable plant = (IPlantable) stack.getItem();
		return plant.getPlantType(world, pos);
	}

	@Override
	public IBlockState getPlant(ItemStack stack, World world, BlockPos pos) {
		IPlantable plant = (IPlantable) stack.getItem();
		BlockPos blockPos = pos.offset(EnumFacing.DOWN);
		IBlockState state = world.getBlockState(blockPos);
		Block base = state.getBlock();
		if (!base.isAir(state, world, blockPos) && base.canSustainPlant(state, world, blockPos, EnumFacing.UP, plant)) {
			return plant.getPlant(world, pos);
		}else{
			return null;
		}
	}
}
