package sonar.core.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import sonar.core.SonarCore;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.InventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.api.wrappers.InventoryWrapper;

public class InventoryHelper extends InventoryWrapper {

	public StoredItemStack getStackToAdd(long inputSize, StoredItemStack stack, StoredItemStack returned) {
		StoredItemStack simulateStack = null;
		if (returned == null || returned.stored == 0) {
			simulateStack = new StoredItemStack(stack.getItemStack(), inputSize);
		} else {
			simulateStack = new StoredItemStack(stack.getItemStack(), inputSize - returned.stored);
		}
		return simulateStack;
	}

	public StorageSize addInventoryToList(List<StoredItemStack> list, IInventory inv) {
		long stored = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				stored += stack.stackSize;
				addStackToList(list, inv.getStackInSlot(i));
			}
		}
		int max = inv.getInventoryStackLimit() * inv.getSizeInventory();
		return new StorageSize(stored, max);
	}

	public StorageSize addItemHandlerToList(List<StoredItemStack> list, IItemHandler inv) {
		long stored = 0;
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				stored += stack.stackSize;
				addStackToList(list, inv.getStackInSlot(i));
			}
		}
		return new StorageSize(stored, inv.getSlots() * 64); // guessing the max size is 64...
	}

	private void addStackToList(List<StoredItemStack> list, ItemStack stack) {
		int pos = 0;
		for (StoredItemStack storedStack : list) {
			if (storedStack.equalStack(stack)) {
				list.get(pos).add(stack);
				return;
			}
			pos++;
		}
		list.add(new StoredItemStack(stack));
	}

	public void addStackToList(List<StoredItemStack> list, StoredItemStack stack) {
		if (stack == null || list == null) {
			return;
		}
		int pos = 0;
		for (StoredItemStack storedStack : list) {
			if (storedStack.equalStack(stack.item)) {
				list.get(pos).add(stack);
				return;
			}
			pos++;
		}
		list.add(stack);
	}

	public void spawnStoredItemStack(StoredItemStack drop, World world, int x, int y, int z, EnumFacing side) {
		List<EntityItem> drops = new ArrayList();
		while (!(drop.stored <= 0)) {
			ItemStack dropStack = drop.getItemStack();
			dropStack.stackSize = (int) Math.min(drop.stored, dropStack.getMaxStackSize());
			drop.stored -= dropStack.stackSize;
			drops.add(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, dropStack));
		}
		if (drop.stored < 0) {
			SonarCore.logger.error("ERROR: Excess Items in Drop");
		}
		for (EntityItem item : drops) {
			item.motionX = 0;
			item.motionY = 0;
			item.motionZ = 0;
			if (side == EnumFacing.NORTH) {
				item.motionZ = -0.1;
			}
			if (side == EnumFacing.SOUTH) {
				item.motionZ = 0.1;
			}
			if (side == EnumFacing.WEST) {
				item.motionX = -0.1;
			}
			if (side == EnumFacing.EAST) {
				item.motionX = 0.1;
			}
			world.spawnEntityInWorld(item);
		}
	}

	public StoredItemStack addItems(TileEntity tile, StoredItemStack stack, EnumFacing dir, ActionType type, IInventoryFilter filter) {
		if (stack == null) {
			return null;
		}
		if (tile != null && (filter == null || filter.allowed(stack.getFullStack()))) {
			List<InventoryHandler> handlers = SonarCore.inventoryProviders.getObjects();
			for (InventoryHandler handler : handlers) {
				if (handler.canHandleItems(tile, dir)) {
					StoredItemStack returned = handler.addStack(stack.copy(), tile, dir, type);
					StoredItemStack add = getStackToAdd(stack.getStackSize(), stack.copy(), returned);
					return add;
				}
			}
		}
		return null;
	}

	public StoredItemStack removeItems(TileEntity tile, StoredItemStack stack, EnumFacing dir, ActionType type, IInventoryFilter filter) {
		if (tile != null && filter == null || filter.allowed(stack.getFullStack())) {
			List<InventoryHandler> handlers = SonarCore.inventoryProviders.getObjects();
			for (InventoryHandler handler : handlers) {
				if (handler.canHandleItems(tile, dir)) {
					StoredItemStack returned = handler.removeStack(stack.copy(), tile, dir, type);
					StoredItemStack remove = getStackToAdd(stack.getStackSize(), stack.copy(), returned);
					return remove;
				}
			}
		}
		return null;
	}

	public void transferItems(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, IInventoryFilter filter) {
		if (from != null && to != null) {
			ArrayList<StoredItemStack> stacks = new ArrayList();
			List<InventoryHandler> handlers = SonarCore.inventoryProviders.getObjects();
			for (InventoryHandler handler : handlers) {
				if (handler.canHandleItems(from, dirFrom)) {
					handler.getItems(stacks, from, dirFrom);
					break;
				}
			}
			if(stacks.isEmpty()){
				return;
			}
			for (StoredItemStack stack : stacks) {
				StoredItemStack removed = removeItems(from, stack.copy(), dirFrom, ActionType.SIMULATE, filter);
				if (removed != null) {
					StoredItemStack add = addItems(to, removed.copy(), dirTo, ActionType.SIMULATE, filter);
					if (add != null) {
						removeItems(from, add.copy(), dirFrom, ActionType.PERFORM, filter);
						addItems(to, removed.copy(), dirTo, ActionType.PERFORM, filter);
					}
				}
			}
		}
	}

	public static interface IInventoryFilter {
		public boolean allowed(ItemStack stack);
	}

}
