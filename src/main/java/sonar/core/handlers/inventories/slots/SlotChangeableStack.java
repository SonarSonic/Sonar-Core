package sonar.core.handlers.inventories.slots;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.common.tileentity.TileEntityLargeInventory;
import sonar.core.handlers.inventories.handling.SlotSonarFiltered;

public class SlotChangeableStack extends SlotSonarFiltered {

	public TileEntityLargeInventory invTile;

	public SlotChangeableStack(TileEntityLargeInventory invTile, int index, int xPosition, int yPosition) {
		super(invTile, index, xPosition, yPosition);
		this.invTile = invTile;
	}

	@Override
	public ItemStack getStack() {
		if(invTile.isClient()) {
			StoredItemStack slot = invTile.inv.slots.get(this.getSlotIndex()).getLargeStack();
			return ItemHandlerHelper.copyStackWithSize(slot.item, (int) slot.stored);
		}else{
			return invTile.inv.slots.get(this.getSlotIndex()).getAccessStack();
		}
	}
}