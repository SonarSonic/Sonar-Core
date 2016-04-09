package sonar.core.upgrades;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.upgrades.IUpgradableTile;
import sonar.core.api.upgrades.IUpgradeInventory;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;

public class MachineUpgrade extends SonarItem {

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitx, float hity, float hitz) {
		TileEntity tile = world.getTileEntity(pos);
		if (!world.isRemote && tile != null && tile instanceof IUpgradableTile) {
			IUpgradeInventory upgrades = ((IUpgradableTile) tile).getUpgradeInventory();
			if (!player.isSneaking()) {
				if (upgrades.addUpgrade(player.getHeldItem())) {
					stack.stackSize -= 1;
					FontHelper.sendMessage("" + upgrades.getInstalledUpgrades(), world, player);
				} else {
					if (upgrades.getAllowedUpgrades().contains(SonarCore.machineUpgrades.getSecondaryObject(stack.getItem()))) {
						FontHelper.sendMessage(FontHelper.translate("upgrade.maximum"), world, player);
					}else{
						FontHelper.sendMessage(FontHelper.translate("upgrade.incompatible"), world, player);
					}
				}
			} else {
				FontHelper.sendMessage(FontHelper.translate("upgrade.accepted") + ": " + upgrades.getAllowedUpgrades(), world, player);
			}
		}
		return true;
	}
}
