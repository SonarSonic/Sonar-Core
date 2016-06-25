package sonar.core.api.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockInteraction;


/** for blocks which can be clicked with left click */
public interface IInteractBlock {
	
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact);

	public boolean allowLeftClick();
	
	public boolean isClickableSide(World world, BlockPos pos, int side);
}
