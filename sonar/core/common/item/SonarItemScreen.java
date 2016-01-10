package sonar.core.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class SonarItemScreen extends SonarItem {

	public abstract Block getScreenBlock();

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz) {
		if (side == 0) {
			return false;
		} else if (!world.getBlock(x, y, z).getMaterial().isSolid() || world.getBlock(x, y, z) == getScreenBlock() || !world.getBlock(x, y, z).hasTileEntity(world.getBlockMetadata(x, y, z))) {
			return false;
		} else {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			x = x + dir.offsetX;
			y = y + dir.offsetY;
			z = z + dir.offsetZ;

			if (!player.canPlayerEdit(x, y, z, side, stack)) {
				return false;
			} else if (world.isRemote) {
				return true;
			} else {

				if (world.isAirBlock(x, y, z)) {
					if (side != 1) {
						world.setBlock(x, y, z, getScreenBlock(), side, 3);
						--stack.stackSize;
					}
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
