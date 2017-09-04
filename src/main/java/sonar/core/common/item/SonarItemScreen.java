package sonar.core.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SonarItemScreen extends SonarItem {

	public abstract Block getScreenBlock();
	
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if (side == EnumFacing.DOWN) {
			return EnumActionResult.PASS;
		}
		Block target = world.getBlockState(pos).getBlock();
        if (target == getScreenBlock() || !target.hasTileEntity(world.getBlockState(pos))) {
			return EnumActionResult.PASS;
		} else {
			if (!player.canPlayerEdit(pos, side, stack)) {
				return EnumActionResult.PASS;
			} else if (world.isRemote) {
				return EnumActionResult.SUCCESS;
			} else {
				if (world.isAirBlock(pos)) {
					if (side != EnumFacing.UP) {
						world.setBlockState(pos, getScreenBlock().getDefaultState(), 3);
						stack.shrink(1);
					}
					return EnumActionResult.SUCCESS;
				} else {
					return EnumActionResult.PASS;
				}
			}
		}
	}
}
