package sonar.core.api.blocks;

import sonar.core.api.utils.BlockInteraction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;


/** for blocks which can be clicked with left click */
public interface IInteractBlock {
	
	public boolean operateBlock(World world, BlockPos pos, EntityPlayer player, BlockInteraction interact);

	public boolean allowLeftClick();
	
	public boolean isClickableSide(World world, BlockPos pos, int side);
}
