package sonar.core.integration.planting.vanilla;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.integration.planting.IFertiliser;

import java.util.Random;

public class Fertiliser implements IFertiliser {

	@Override
	public boolean isLoadable() {
		return true;
	}

	@Override
	public String getName() {
		return "Bonemeal";
	}

	@Override
	public boolean canFertilise(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof IGrowable;
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		return ((IGrowable) state.getBlock()).canGrow(world, pos, state, isClient);
	}

	@Override
	public boolean canUseFertiliser(ItemStack fertiliser, World world, Random rand, BlockPos pos, IBlockState state) {
		return fertiliser.getItem() == Items.DYE && fertiliser.getItemDamage() ==EnumDyeColor.WHITE.getDyeDamage() && ((IGrowable) state.getBlock()).canUseBonemeal(world, rand, pos, state);
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		((IGrowable) state.getBlock()).grow(world, rand, pos, state);
	}
}
