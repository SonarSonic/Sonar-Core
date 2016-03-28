package sonar.core.utils.upgrades;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;

public class MachineUpgrade extends SonarItem {

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitx, float hity, float hitz) {
		TileEntity tile = world.getTileEntity(pos);
		if (!world.isRemote && tile != null && tile instanceof IUpgradableTile) {
			UpgradeInventory upgrades = ((IUpgradableTile) tile).getUpgradeInventory();
			if (!player.isSneaking()) {
				if (upgrades.addUpgrade(player.getHeldItem())) {
					stack.stackSize -= 1;
				}else{
					FontHelper.sendMessage("Maximum Installed", world, player);
				}
			}else{
				FontHelper.sendMessage("Accepted Upgrades: " + upgrades.allowed, world, player);
			}
		}
		return true;
	}
}
