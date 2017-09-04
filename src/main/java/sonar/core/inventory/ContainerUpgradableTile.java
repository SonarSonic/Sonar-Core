package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.api.upgrades.IUpgradableTile;
import sonar.core.api.upgrades.IUpgradeInventory;
import sonar.core.common.tileentity.TileEntitySonar;

public class ContainerUpgradableTile extends ContainerSync {

    IUpgradeInventory inv;

	public ContainerUpgradableTile(TileEntitySonar tile) {
		super(((IUpgradableTile) tile).getUpgradeInventory(), tile);
		this.inv = ((IUpgradableTile) tile).getUpgradeInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
