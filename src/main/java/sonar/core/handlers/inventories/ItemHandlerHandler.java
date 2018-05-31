package sonar.core.handlers.inventories;
/*
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.InventoryHandler;
import sonar.core.api.inventories.ISonarInventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.inventory.handling.ItemTransferHelper;

@InventoryHandler(modid = "sonarcore", priority = 1)
public class ItemHandlerHandler implements ISonarInventoryHandler {

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
        return getStack(slot, handler, dir);
    }

    public static StoredItemStack getStack(int slot, IItemHandler handler, EnumFacing dir) {
		if (slot < handler.getSlots()) {
            return new StoredItemStack(handler.getStackInSlot(slot));
		}
		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
		return ItemTransferHelper.addItemHandlerToList(storedStacks, handler);
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
        return addStack(add, handler, dir, action);
    }

    public static StoredItemStack addStack(StoredItemStack add, IItemHandler handler, EnumFacing dir, ActionType action) {
		for (int i = 0; i < handler.getSlots(); i++) {
			if (add == null || add.stored == 0)
				return null;
			ItemStack stack = handler.insertItem(i, add.getFullStack(), action.shouldSimulate());
			if (!stack.isEmpty() && add.stored != 0) {
				add.remove(SonarAPI.getItemHelper().getStackToAdd(add.stored, add, new StoredItemStack(stack)));
			} else if(stack.isEmpty()){
				add.stored -= add.getFullStack().getCount();
			}
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
        return removeStack(remove, handler, dir, action);
    }

    public static StoredItemStack removeStack(StoredItemStack remove, IItemHandler handler, EnumFacing dir, ActionType action) {
		for (int i = 0; i < handler.getSlots(); i++) {
			if (remove == null || remove.stored == 0)
				return null;
			ItemStack current = handler.getStackInSlot(i);
			if (!current.isEmpty() && remove.equalStack(current)) {
				int removeSize = (int) Math.min(current.getCount(), remove.getStackSize());
				ItemStack stack = handler.extractItem(i, removeSize, action.shouldSimulate());
				if (!stack.isEmpty()) {
					remove.remove(new StoredItemStack(stack));
				}
			}
		}
		return remove;
	}

    @Override
    public boolean isLargeInventory() {
        return false; //some may be, most won't
    }
}
*/