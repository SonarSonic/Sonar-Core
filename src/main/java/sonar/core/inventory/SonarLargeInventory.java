package sonar.core.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.ISyncPart;

import javax.annotation.Nonnull;

public class SonarLargeInventory extends DirtyPart implements IItemHandler, INBTSyncable, ISyncPart {

	public StoredItemStack[] slots;
	public int limit = 64;
	public int numStacks = 4;
	public int size;
	// public int max;

	public SonarLargeInventory(int size, int numStacks) {
		super();
		this.size = size;
		this.slots = new StoredItemStack[size];
		this.numStacks = numStacks;
	}

	public SonarLargeInventory setStackLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			if (nbt.hasKey(getTagName())) {// legacy support
				NBTTagList list = nbt.getTagList(getTagName(), 10);
				StoredItemStack[] stacks = new StoredItemStack[size];
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound compound = list.getCompoundTagAt(i);
					byte b = compound.getByte("Slot");
					if (b >= 0 && b < size) {
						stacks[b] = NBTHelper.instanceNBTSyncable(StoredItemStack.class, compound);
					}
				}
				slots = stacks;
			}
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < size; i++) {
				StoredItemStack stack = slots[i];
				if (stack != null && stack.getStackSize() != 0) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					list.appendTag(stack.writeData(compound, type));
				}
			}
			nbt.setTag(getTagName(), list);
		}
		return nbt;
	}

	@Nonnull
    @Override
	public ItemStack getStackInSlot(int slot) {
		StoredItemStack largeStack = getLargeStack(slot);
		if(largeStack!=null){
			ItemStack stack = largeStack.getItemStack().copy();
			stack.setCount((int) largeStack.getStackSize());
			return stack;
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public int getSlots() {
		return size;
	}

	public long getCount(int slot) {
		StoredItemStack largeStack = getLargeStack(slot);
		return largeStack == null ? 0 : largeStack.getStackSize();
	}

	public StoredItemStack getLargeStack(int slot) {
		if (size > slot) {
			return slots[slot];
		}
		return null;
	}

	public void setLargeStack(int slot, StoredItemStack stack) {
		slots[slot] = stack;
	}

	@Nonnull
    @Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (!stack.isEmpty() && isItemValidForSlot(slot, stack)) {
			StoredItemStack stored = slots[slot];			
			if (stored == null || stored.getStackSize() == 0 || stored.equalStack(stack) && stored.getStackSize() < numStacks * stack.getMaxStackSize()) {
				int maxAdd = (int) Math.min(numStacks * stack.getMaxStackSize() - (stored != null ? stored.getStackSize() : 0), stack.getCount());
				if (maxAdd != 0) {
					if (!simulate) {
						if (stored != null && stored.getStackSize() != 0) {
							stored.stored += maxAdd;
						} else {
							stored = slots[slot] = new StoredItemStack(stack.copy(), maxAdd);
						}
					}
					return maxAdd == stack.getCount() ? ItemStack.EMPTY : new StoredItemStack(stack.copy()).setStackSize(stack.getCount() - maxAdd).getActualStack();
				}
			}
		}
		return stack;
	}

	@Nonnull
    @Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		StoredItemStack largeStack = slots[slot];
		if (largeStack != null && largeStack.getStackSize() > 0) {
			int toRemove = (int) Math.min(amount, largeStack.getStackSize());
			if (toRemove == 0)
				return ItemStack.EMPTY;
			if (!simulate) {
				largeStack.stored -= toRemove;
			}
			return largeStack.copy().setStackSize(toRemove).getActualStack();
		}
		return ItemStack.EMPTY;
	}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return true;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync == SyncType.SAVE;
	}

	@Override
	public String getTagName() {
		return "Items";
	}

	public void markDirty() {
		markChanged();
	}

	@Override
	public int getSlotLimit(int slot) {
		return limit;
	}
}