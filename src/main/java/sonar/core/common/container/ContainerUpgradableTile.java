package sonar.core.common.container;

import com.jaquadro.minecraft.storagedrawers.inventory.InventoryUpgrade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.upgrades.IUpgradableTile;
import sonar.core.api.upgrades.IUpgradeInventory;
import sonar.core.inventory.ContainerSync;

public class ContainerUpgradableTile extends ContainerSync {

	IUpgradeInventory inv = null;

	public ContainerUpgradableTile(TileEntity tile) {
		super(((IUpgradableTile) tile).getUpgradeInventory(), tile);
		this.inv = ((IUpgradableTile) tile).getUpgradeInventory();
		
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
