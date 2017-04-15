package sonar.core.handlers.inventories;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.InventoryHandler;
import sonar.core.api.inventories.ISonarInventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.inventory.GenericInventoryHandler;

@InventoryHandler(modid = "sonarcore", handlerID = IInventoryHandler.name, priority = 0)
public class IInventoryHandler implements ISonarInventoryHandler {

	public static final String name = "Standard Inventory";

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof IInventory;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		return GenericInventoryHandler.getStack(slot, (IInventory) tile, dir);
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		return GenericInventoryHandler.getItems(storedStacks, (IInventory) tile, dir);
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		return GenericInventoryHandler.addStack(add, (IInventory) tile, dir, action);
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		return GenericInventoryHandler.removeStack(remove, (IInventory) tile, dir, action);
	}

	@Override
	public boolean isLargeInventory() {
		return false;
	}
}
