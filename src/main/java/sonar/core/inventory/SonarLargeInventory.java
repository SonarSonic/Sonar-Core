package sonar.core.inventory;

import java.util.ArrayList;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncNBTAbstractList;
import sonar.core.network.sync.SyncPart;

/** FIXME HELP ME!!!! */
public class SonarLargeInventory extends DirtyPart implements IItemHandler, INBTSyncable, ISyncPart {

	public StoredItemStack[] slots;
	public int limit = 64;
	public final TileEntity tile;
	public int numStacks = 4;
	public int size;
	// public int max;

	public SonarLargeInventory(TileEntity tile, int size, int numStacks) {
		this.size = size;
		this.slots = new StoredItemStack[size];
		this.tile = tile;
		this.numStacks = numStacks;
	}

	public SonarLargeInventory setStackLimit(int limit) {
		this.limit = limit;
		return this;
	}
	/* public StoredItemStack buildItemStack(ArrayList<ItemStack> list) { StoredItemStack stack = null; if (list == null) { return stack; } for (ItemStack item : list) { if (item != null) { if (stack == null) { stack = new StoredItemStack(item); } else { stack.add(item); } } } return stack; }
	 * 
	 * public ArrayList<ItemStack> buildArrayList(StoredItemStack stack) { ArrayList<ItemStack> list = new ArrayList<ItemStack>(); while (stack.stored > 0) { ItemStack item = stack.getFullStack(); list.add(item.copy()); stack.remove(item.copy()); } return list; } */

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

	public ItemStack getStackInSlot(int slot) {
		int target = (int) Math.floor(slot / numStacks);
		StoredItemStack stack = slots[target];
		if (stack == null || stack.getStackSize() == 0) {
			return null;
		} else {
			int actualSize = getSlotSize(stack, target, slot);
			return actualSize == 0 ? null : stack.copy().setStackSize(actualSize).getActualStack();
		}
	}

	public int getSlotSize(StoredItemStack stack, int target, int slot) {
		int maxStackSize = stack.item.getMaxStackSize();
		int pos = slot - target * numStacks;
		int size = (int) stack.getStackSize();
		int actualSize = size >= (pos * maxStackSize) ? maxStackSize : Math.min(0, (int) (size - pos * maxStackSize));
		return actualSize;
	}

	@Override
	public int getSlots() {
		return size * numStacks;
	}

	public int getLargeSize() {
		return size;
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

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack != null && stack.stackSize != 0) {
			int target = (int) Math.floor(slot / numStacks);
			StoredItemStack stored = slots[target];
			if (stored == null || stored.getStackSize() == 0 || stored.equalStack(stack) && stored.getStackSize() < numStacks * stack.getMaxStackSize()) {
				int maxAdd = (int) Math.min(numStacks * stack.getMaxStackSize() - stored.getStackSize(), stack.stackSize);
				if (maxAdd != 0) {
					if (!simulate)
						stored.stored += maxAdd;
					return maxAdd == stack.stackSize ? null : new StoredItemStack(stack.copy()).setStackSize(stack.stackSize - maxAdd).getActualStack();
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			int toRemove = Math.min(amount, stack.stackSize);
			if (toRemove == 0)
				return null;
			int target = (int) Math.floor(slot / numStacks);
			StoredItemStack stored = slots[target];
			if (!simulate) {
				stored.stored -= toRemove;
				if (stored.stored == 0) {
					slots[target] = null;
				}
			}
			return new StoredItemStack(stack.copy()).setStackSize(toRemove).getActualStack();
		}
		return null;
	}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return true;
	}

	public void markDirty() {
		tile.markDirty();
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

}