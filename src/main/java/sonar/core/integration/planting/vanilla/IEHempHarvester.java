package sonar.core.integration.planting.vanilla;

import java.util.List;

import com.google.common.collect.Lists;

import blusunrize.immersiveengineering.common.blocks.plant.BlockIECrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import sonar.core.integration.planting.IHarvester;
/* this is causing weirdness and it's not worth fixing right now...
public class IEHempHarvester implements IHarvester {

	@Override
	public boolean isLoadable() {
		return Loader.isModLoaded("immersiveengineering");
	}

	@Override
	public String getName() {
		return "Hemp Harvester";
	}

	@Override
	public boolean canHarvest(World world, BlockPos pos, IBlockState state) {
		if(state.getBlock() instanceof BlockIECrop){
			return true;
		}
		return false;
	}

	@Override
	public boolean isReady(World world, BlockPos pos, IBlockState state) {
		IBlockState upper = world.getBlockState(pos.offset(EnumFacing.UP));
		if(upper.getBlock() instanceof BlockIECrop){
			return !((BlockIECrop)upper.getBlock()).canGrow(world, pos.offset(EnumFacing.UP), upper, world.isRemote);
		}
		return false;
	}

	@Override
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState state, int fortune) {	
		List<ItemStack> drops = Lists.newArrayList();
		((BlockIECrop) state.getBlock()).getDrops(world, pos, state, fortune).forEach(stack -> drops.add((ItemStack) stack));
		((BlockIECrop) state.getBlock()).getDrops(world, pos.offset(EnumFacing.UP), world.getBlockState(pos.offset(EnumFacing.UP)), fortune).forEach(stack -> drops.add((ItemStack) stack));
		
		return drops;
	}

	@Override
	public void harvest(World world, BlockPos pos, IBlockState state, boolean keepBlock) {
		world.setBlockToAir(pos.offset(EnumFacing.UP));
		if (keepBlock) {
			world.setBlockState(pos, state.getBlock().getDefaultState());
			return;
		}
		world.setBlockToAir(pos);		
	}
}
*/