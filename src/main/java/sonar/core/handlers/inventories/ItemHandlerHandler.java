package sonar.core.handlers.inventories;

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

@InventoryHandler(modid = "sonarcore", handlerID = ItemHandlerHandler.name, priority = 1)
public class ItemHandlerHandler implements ISonarInventoryHandler {

	public static final String name = "Item Handler Inventory";

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
		if (slot < handler.getSlots()) {
			ItemStack stack = handler.getStackInSlot(slot);
			if (stack != null)
				return new StoredItemStack(stack);
		}
		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
		return SonarAPI.getItemHelper().addItemHandlerToList(storedStacks, handler);
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
		for (int i = 0; i < handler.getSlots(); i++) {
			if (add == null || add.stored == 0)
				return null;
			ItemStack stack = handler.insertItem(i, add.getFullStack(), action.shouldSimulate());
			if (stack != null && add.stored != 0) {
				add.remove(SonarAPI.getItemHelper().getStackToAdd(add.stored, add, new StoredItemStack(stack)));
			} else {
				add.stored -= add.getFullStack().stackSize;
			}
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
		for (int i = 0; i < handler.getSlots(); i++) {
			if (remove == null || remove.stored == 0)
				return null;
			ItemStack current = handler.getStackInSlot(i);
			if (current != null && remove.equalStack(current)) {
				int removeSize = (int) Math.min(current.stackSize, remove.getStackSize());
				ItemStack stack = handler.extractItem(i, removeSize, action.shouldSimulate());
				if (stack != null) {
					remove.remove(new StoredItemStack(stack));
				}
			}
		}
		return remove;
	}
}
