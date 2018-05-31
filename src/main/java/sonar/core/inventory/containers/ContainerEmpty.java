package sonar.core.inventory.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.inventory.TransferSlotsManager;

import javax.annotation.Nonnull;

public class ContainerEmpty extends ContainerSync {

	public ContainerEmpty(InventoryPlayer player, TileEntitySonar tile) {
		super(tile);
		addInventory(player, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

    @Nonnull
    @Override
	public final ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return TransferSlotsManager.DEFAULT.transferStackInSlot(this, null, player, slotID);
	}
}
