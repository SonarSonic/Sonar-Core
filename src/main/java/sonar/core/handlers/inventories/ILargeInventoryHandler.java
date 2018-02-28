package sonar.core.handlers.inventories;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.InventoryHandler;
import sonar.core.api.inventories.ISonarInventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.inventory.ILargeInventory;
import sonar.core.inventory.SonarLargeInventory;

@InventoryHandler(modid = "sonarcore", priority = 0)
public class ILargeInventoryHandler implements ISonarInventoryHandler {
	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof ILargeInventory;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		SonarLargeInventory inv = ((ILargeInventory) tile).getTileInv();
		return inv.getLargeStack(slot);
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		SonarLargeInventory inv = ((ILargeInventory) tile).getTileInv();
		long max = inv.getSlots() * inv.limit;
		long stored = 0;
		for (StoredItemStack stack : inv.slots) {
			if (stack != null) {
				SonarAPI.getItemHelper().addStackToList(storedStacks, stack.copy());
				stored += stack.stored;
			}
		}
		return new StorageSize(stored, max);
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		SonarLargeInventory inv = ((ILargeInventory) tile).getTileInv();
		for (int i = 0; i < inv.getSlots(); i++) {
			if (add == null || add.stored == 0)
				return null;
			ItemStack stack = inv.insertItem(i, add.getFullStack(), action.shouldSimulate());
			if (!stack.isEmpty() && add.stored != 0) {
				add.remove(SonarAPI.getItemHelper().getStackToAdd(add.stored, add, new StoredItemStack(stack)));
			} else {
				add.stored -= add.getFullStack().getCount();
			}
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		SonarLargeInventory inv = ((ILargeInventory) tile).getTileInv();
		for (int i = 0; i < inv.getSlots(); i++) {
			if (remove == null || remove.stored == 0)
				return null;
			ItemStack current = inv.getStackInSlot(i);
			if (!current.isEmpty() && remove.equalStack(current)) {
				int removeSize = (int) Math.min(current.getCount(), remove.getStackSize());
				ItemStack stack = inv.extractItem(i, removeSize, action.shouldSimulate());
				if (!stack.isEmpty()) {
					remove.remove(new StoredItemStack(stack));
				}
			}
		}
		return remove;
	}

	@Override
	public boolean isLargeInventory() {
		return false; // the name suggest otherwise but ILargeInventories
						// generally don't have that many slots
	}
}
