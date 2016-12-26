package sonar.core.integration.planting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import sonar.core.api.IRegistryObject;

public interface IPlanter extends IRegistryObject {

	public boolean canTierPlant(ItemStack stack, int tier);

	public EnumPlantType getPlantType(ItemStack stack, World world, BlockPos pos);

	/**@param stack
	 * @param world
	 * @param pos where the plant is to be planted
	 * @return */
	public IBlockState getPlant(ItemStack stack, World world, BlockPos pos);

}
