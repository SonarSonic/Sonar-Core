package sonar.core.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class SonarItemScreen extends SonarItem {

	public abstract Block getScreenBlock();

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitx, float hity, float hitz) {
		if (side == EnumFacing.DOWN) {
			return false;
		}
		Block target = world.getBlockState(pos).getBlock();
		if (!target.getMaterial().isSolid() || target == getScreenBlock() || !target.hasTileEntity(world.getBlockState(pos))) {
			return false;
		} else {
			if (!player.canPlayerEdit(pos, side, stack)) {
				return false;
			} else if (world.isRemote) {
				return true;
			} else {
				if (world.isAirBlock(pos)) {
					if (side != EnumFacing.UP) {
						world.setBlockState(pos, getScreenBlock().getDefaultState(), 3);
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
