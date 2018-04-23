package sonar.core.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import sonar.core.SonarCore;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.ISonarInventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.api.wrappers.InventoryWrapper;
import sonar.core.handlers.inventories.IInventoryHandler;
import sonar.core.inventory.IAdditionalInventory;
import sonar.core.inventory.IDropInventory;

@Deprecated
public class InventoryHelper extends InventoryWrapper {

	public static ISonarInventoryHandler defHandler = new IInventoryHandler();

	public static boolean addStack(IInventory inv, StoredItemStack add, int slot, int limit, ActionType action) {
		if (inv.isItemValidForSlot(slot, add.item)) {
			final ItemStack stored = inv.getStackInSlot(slot);
			if (!stored.isEmpty()) {
				ItemStack stack = stored.copy();
				if (add.equalStack(stack) && stack.getCount() < limit && stack.getCount() < stack.getMaxStackSize()) {
					long used = Math.min(add.item.getMaxStackSize() - stack.getCount(), Math.min(add.stored, limit - stack.getCount()));
					if (used > 0) {
						stack.setCount((int) (stack.getCount() + used));
						add.stored -= used;
						if (!action.shouldSimulate()) {
							inv.setInventorySlotContents(slot, stack.copy());
							inv.markDirty();
						}
					}
				}
			} else {
				long used = Math.min(add.item.getMaxStackSize(), Math.min(add.stored, limit));
				if (used > 0) {
					ItemStack stack = new StoredItemStack(add.getFullStack()).setStackSize(used).getFullStack();
					add.stored -= used;
					if (!action.shouldSimulate()) {
						inv.setInventorySlotContents(slot, stack);
						inv.markDirty();
					}
				}
			}
			if (add.stored == 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean removeStack(IInventory inv, StoredItemStack remove, ItemStack stored, int slot, ActionType action) {
		ItemStack stack = stored.copy();
		if (remove.equalStack(stack)) {
			long used = Math.min(remove.stored, Math.min(inv.getInventoryStackLimit(), stack.getCount()));
			stack.setCount((int) (stack.getCount() - used));
			remove.stored -= used;
			if (stack.getCount() == 0) {
				stack = ItemStack.EMPTY;
			}
			if (!action.shouldSimulate()) {
				inv.setInventorySlotContents(slot, stack);
				inv.markDirty();
			}
			if (remove.stored == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public StoredItemStack getStackToAdd(long inputSize, StoredItemStack stack, StoredItemStack returned) {
		StoredItemStack simulateStack = null;
		if (returned == null || returned.stored == 0) {
			simulateStack = new StoredItemStack(stack.getItemStack(), inputSize);
		} else {
			simulateStack = new StoredItemStack(stack.getItemStack(), inputSize - returned.stored);
		}
		return simulateStack;
	}

	@Override
	public StorageSize addInventoryToList(List<StoredItemStack> list, IInventory inv) {
		long stored = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				stored += stack.getCount();
				addStackToList(list, inv.getStackInSlot(i));
			}
		}
		int max = inv.getInventoryStackLimit() * inv.getSizeInventory();
		return new StorageSize(stored, max);
	}

	@Override
	public StorageSize addItemHandlerToList(List<StoredItemStack> list, IItemHandler inv) {
		long stored = 0;
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				stored += stack.getCount();
				addStackToList(list, stack);
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

	@Override
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
	@Override
	public void spawnStoredItemStack(StoredItemStack drop, World world, int x, int y, int z, EnumFacing side) {
		spawnStoredItemStackDouble(drop, world, x + 0.5, y + 0.5, z + 0.5, side);
	}

	@Override
	public void spawnStoredItemStackDouble(StoredItemStack drop, World world, double x, double y, double z, EnumFacing side) {
		while (!(drop.stored <= 0)) {
			ItemStack dropStack = drop.getItemStack().copy();
			dropStack.setCount((int) Math.min(drop.stored, dropStack.getMaxStackSize()));
			drop.stored -= dropStack.getCount();		
			
			EntityItem entity = new EntityItem(world, x, y, z, dropStack);
			entity.setPickupDelay(40);
			entity.motionX = side.getFrontOffsetX()/16;
			entity.motionY = side.getFrontOffsetY()/16;
			entity.motionZ = side.getFrontOffsetZ()/16;
			world.spawnEntity(entity);
		}
	}

	public ItemStack addItems(TileEntity tile, StoredItemStack stack, ActionType type) {
		if (stack == null || tile == null) {
			return ItemStack.EMPTY;
		}
		StoredItemStack returned = defHandler.addStack(stack.copy(), tile, null, type);
		StoredItemStack add = getStackToAdd(stack.getStackSize(), stack.copy(), returned);
		return add.getActualStack();
	}

	public ItemStack removeItems(TileEntity tile, StoredItemStack stack, ActionType type) {
		if (stack == null || tile == null) {
			return ItemStack.EMPTY;
		}
		StoredItemStack returned = defHandler.removeStack(stack.copy(), tile, null, type);
		StoredItemStack add = getStackToAdd(stack.getStackSize(), stack.copy(), returned);
		return add.getActualStack();
	}

	@Override
	public StoredItemStack addItems(TileEntity tile, StoredItemStack stack, EnumFacing dir, ActionType type, IInventoryFilter filter) {
		long maxAdd = maxAdd(filter, stack.getStackSize());
		if (maxAdd == 0) {
			return null;
		}
		if (tile != null && (filter == null || filter.allowed(stack.getFullStack()))) {
			List<ISonarInventoryHandler> handlers = SonarCore.inventoryHandlers;
			for (ISonarInventoryHandler handler : handlers) {
				if (handler.canHandleItems(tile, dir)) {
					StoredItemStack returned = handler.addStack(stack.copy().setStackSize(maxAdd), tile, dir, type);
					StoredItemStack add = getStackToAdd(maxAdd, stack.copy(), returned);
					onAdd(filter, add.getStackSize());
					return add;
				}
			}
		}
		return null;
	}

	@Override
	public StoredItemStack removeItems(TileEntity tile, StoredItemStack stack, EnumFacing dir, ActionType type, IInventoryFilter filter) {
		long maxRemove = maxRemove(filter, stack.getStackSize());
		if (maxRemove == 0) {
			return null;
		}
		if (tile != null && (filter == null || filter.allowed(stack.getFullStack()))) {
			List<ISonarInventoryHandler> handlers = SonarCore.inventoryHandlers;
			for (ISonarInventoryHandler handler : handlers) {
				if (handler.canHandleItems(tile, dir)) {
					StoredItemStack returned = handler.removeStack(stack.copy().setStackSize(maxRemove), tile, dir, type);
					StoredItemStack remove = getStackToAdd(maxRemove, stack.copy(), returned);
					onRemove(filter, remove.getStackSize());
					return remove;
				}
			}
		}
		return null;
	}

	@Override
	public void transferItems(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, IInventoryFilter filter) {
		if (from != null && to != null) {
			ArrayList<StoredItemStack> stacks = new ArrayList<>();
			List<ISonarInventoryHandler> handlers = SonarCore.inventoryHandlers;
			for (ISonarInventoryHandler handler : handlers) {
				if (handler.canHandleItems(from, dirFrom)) {
					handler.getItems(stacks, from, dirFrom);
					break;
				}
			}
			if (stacks.isEmpty()) {
				return;
			}
			for (StoredItemStack stack : stacks) {
				StoredItemStack removed = removeItems(from, stack.copy(), dirFrom, ActionType.SIMULATE, copy(filter));
				if (removed != null) {
					StoredItemStack add = addItems(to, removed.copy(), dirTo, ActionType.SIMULATE, copy(filter));
					if (add != null) {
						removeItems(from, add.copy(), dirFrom, ActionType.PERFORM, filter);
						addItems(to, removed.copy(), dirTo, ActionType.PERFORM, filter);
					}
				}
			}
		}
	}

	public static IInventoryFilter copy(IInventoryFilter filter) {
		if (filter == null || !(filter instanceof ITransferOverride)) {
			return null;
		}
		return ((ITransferOverride) filter).copy();
	}

	public static void reset(IInventoryFilter filter) {
		if (filter == null || !(filter instanceof ITransferOverride)) {
			return;
		}
		((ITransferOverride) filter).reset();
	}

	public static long maxAdd(IInventoryFilter filter, long l) {
		if (filter == null || !(filter instanceof ITransferOverride)) {
			return l;
		}
		return Math.min(((ITransferOverride) filter).getMaxAdd(), l);
	}

	public static void onAdd(IInventoryFilter filter, long added) {
		if (filter == null || !(filter instanceof ITransferOverride)) {
			return;
		}
		((ITransferOverride) filter).add(added);
	}

	public static long maxRemove(IInventoryFilter filter, long maxRemove) {
		if (filter == null || !(filter instanceof ITransferOverride)) {
			return maxRemove;
		}
		return Math.min(((ITransferOverride) filter).getMaxRemove(), maxRemove);
	}

	public static void onRemove(IInventoryFilter filter, long removed) {
		if (filter == null || !(filter instanceof ITransferOverride)) {
			return;
		}
		((ITransferOverride) filter).remove(removed);
	}

	public static void dropInventory(Object inventory, World world, BlockPos pos, IBlockState state) {
		if (inventory == null) {
			return;
		}
		List<ItemStack> drops = new ArrayList<>();
			if (inventory instanceof IDropInventory) {
				IDropInventory inv = (IDropInventory) inventory;
				if (inv.canDrop() && inv.dropSlots() != null) {
					int[] slots = inv.dropSlots();
					for (int slot : slots) {
						ItemStack itemstack = inv.getStackInSlot(slot);
						if (!itemstack.isEmpty()) {
							drops.add(itemstack);
						}
					}
				}
			} else if (inventory instanceof IInventory) {
				IInventory inv = (IInventory) inventory;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack itemstack = inv.getStackInSlot(i);
					if (!itemstack.isEmpty()) {
						drops.add(itemstack);
					}
				}
			}
			if (inventory instanceof IAdditionalInventory) {
				IAdditionalInventory additionalInv = (IAdditionalInventory) inventory;
				ItemStack[] additionalStacks = additionalInv.getAdditionalStacks();
				if (additionalStacks != null) {
					for (ItemStack stack : additionalStacks) {
						if (!stack.isEmpty()) {
							drops.add(stack);
						}
					}
				}
			}

		for (ItemStack stack : drops) {
			if (!stack.isEmpty()) {
				float f = SonarCore.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = SonarCore.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = SonarCore.rand.nextFloat() * 0.8F + 0.1F;

				EntityItem dropStack = new EntityItem(world, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, stack);
				world.spawnEntity(dropStack);
			}
		}
	}

	public static class DefaultTransferOverride implements ITransferOverride {

		public long maxAdd, maxRemove;
		public long currentAdd, currentRemove;

		public DefaultTransferOverride(long max) {
			this.maxAdd = max;
			this.currentAdd = max;
			this.maxRemove = max;
			this.currentRemove = max;
		}

		public DefaultTransferOverride(long maxAdd, long maxRemove) {
			this.maxAdd = maxAdd;
			this.currentAdd = maxAdd;
			this.maxRemove = maxRemove;
			this.currentRemove = maxRemove;
		}

		public boolean canTransfer() {
			return currentAdd != 0 || currentRemove != 0;
		}

		@Override
		public ITransferOverride copy() {
			return new DefaultTransferOverride(maxAdd, maxRemove);
		}

		@Override
		public boolean allowed(ItemStack stack) {
			return true;
		}

		@Override
		public void reset() {
			currentAdd = maxAdd;
			currentRemove = maxRemove;
		}

		@Override
		public void add(long added) {
			currentAdd -= added;
		}

		@Override
		public void remove(long removed) {
			currentRemove -= removed;
		}

		@Override
		public long getMaxRemove() {
			return currentRemove;
		}

		@Override
		public long getMaxAdd() {
			return currentAdd;
		}

	}

	public interface ITransferOverride extends IInventoryFilter {

		ITransferOverride copy();

		void reset();

		void add(long added);

		void remove(long removed);

		long getMaxRemove();

		long getMaxAdd();
	}

	public interface IInventoryFilter {
		boolean allowed(ItemStack stack);
	}
}
