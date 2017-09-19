package sonar.core.integration.planting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.IRegistryObject;

import java.util.List;
	
public interface IHarvester extends IRegistryObject {
	
    boolean canHarvest(World world, BlockPos pos, IBlockState state);
	
    boolean isReady(World world, BlockPos pos, IBlockState state);
	
    List<ItemStack> getDrops(World world, BlockPos pos, IBlockState state, int fortune);
	
    void harvest(World world, BlockPos pos, IBlockState state, boolean keepBlock);
}
