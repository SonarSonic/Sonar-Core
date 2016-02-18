package sonar.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** for blocks which can be clicked with left click */
public interface IInteractBlock {
	
	public boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, BlockInteraction interact);

	public boolean allowLeftClick();
	
	public boolean isClickableSide(World world, int x, int y, int z, int side);
}
