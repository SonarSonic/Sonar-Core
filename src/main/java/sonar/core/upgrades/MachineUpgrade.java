package sonar.core.upgrades;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.upgrades.IUpgradableTile;
import sonar.core.api.upgrades.IUpgradeInventory;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;

public class MachineUpgrade extends SonarItem {

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (!world.isRemote && tile != null && tile instanceof IUpgradableTile) {
			ItemStack stack = player.getHeldItem(hand);
			IUpgradeInventory upgrades = ((IUpgradableTile) tile).getUpgradeInventory();
			if (!player.isSneaking()) {
				if (upgrades.addUpgrade(player.getHeldItemMainhand())) {
					stack.shrink(1);
                    FontHelper.sendMessage(String.valueOf(upgrades.getInstalledUpgrades()), world, player);
				} else {
					if (upgrades.getAllowedUpgrades().contains(SonarCore.machineUpgrades.getSecondaryObject(stack.getItem()))) {
						FontHelper.sendMessage(FontHelper.translate("upgrade.maximum"), world, player);
					} else {
						FontHelper.sendMessage(FontHelper.translate("upgrade.incompatible"), world, player);
					}
				}
			} else {
				FontHelper.sendMessage(FontHelper.translate("upgrade.accepted") + ": " + upgrades.getAllowedUpgrades(), world, player);
			}
		}
		return EnumActionResult.SUCCESS;
	}
}
