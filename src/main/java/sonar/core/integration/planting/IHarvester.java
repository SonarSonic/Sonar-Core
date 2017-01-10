package sonar.core.integration.planting;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.IRegistryObject;

public interface IHarvester extends IRegistryObject {
	
	public boolean canHarvest(World world, BlockPos pos, IBlockState state);
	
	public boolean isReady(World world, BlockPos pos, IBlockState state);
	
	public List<ItemStack> getDrops(World world, BlockPos pos, IBlockState state, int fortune);
	
	public void harvest(World world, BlockPos pos, IBlockState state, boolean keepBlock);		
	
}
