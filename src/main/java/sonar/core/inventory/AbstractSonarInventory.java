package sonar.core.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.InventoryHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.ISyncPart;

public abstract class AbstractSonarInventory<T extends AbstractSonarInventory> extends DirtyPart implements ISonarInventory, ISyncPart {

	public NonNullList<ItemStack> slots;
	public int size;
	public int limit = 64;
	public EnumFacing face = null;
	public IItemHandler embeddedHandler = new EmbeddedHandler(this);

	public static class EmbeddedHandler implements IItemHandler {
		AbstractSonarInventory inv;

		public EmbeddedHandler(AbstractSonarInventory inv) {
			this.inv = inv;
		}

		@Override
		public int getSlots() {
			return inv.getSlots();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return inv.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return inv.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return inv.extractItem(slot, amount, simulate);
		}

		@Override
		public int getSlotLimit(int slot) {
			return inv.getSlotLimit(slot);
		}

	}

	public AbstractSonarInventory(int size) {
		super();
		this.size = size;
		this.slots = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}

	public IItemHandler getItemHandler(EnumFacing side) {
		face = side;
		return embeddedHandler;
	}

	public T setStackLimit(int limit) {
		this.limit = limit;
		return (T) this;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.SAVE)) {
			this.slots = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(nbt, this.slots);
		}
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.SAVE)) {
			ItemStackHelper.saveAllItems(nbt, this.slots);
		}
		return nbt;
	}

	public int getSizeInventory() {
		return size;
	}

	public ItemStack getStackInSlot(int slot) {
		return slots.get(slot);
	}

	public ItemStack decrStackSize(int index, int count) {
		ItemStack split = ItemStackHelper.getAndSplit(this.slots, index, count);
		if (!ItemStack.areItemsEqual(split, slots.get(index))) {
			markDirty();
		}
		return split;
	}

	public ItemStack removeStackFromSlot(int index) {
		ItemStack remove = ItemStackHelper.getAndRemove(this.slots, index);
		if (!ItemStack.areItemsEqual(remove, slots.get(index))) {
			markDirty();
		}
		return remove;
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		this.slots.set(index, stack);		
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
        this.markDirty();
	}

	public int getInventoryStackLimit() {
		return limit;
	}

	public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	/*
		*/
	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {
	}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); i++)
			this.setInventorySlotContents(i, ItemStack.EMPTY);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync.isType(SyncType.SAVE);
	}

	@Override
	public String getTagName() {
		return "Items";
	}

	@Override
	public int getSlots() {
		return getSizeInventory();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		StoredItemStack add = new StoredItemStack(stack);
		boolean bool = InventoryHelper.addStack(this, add, slot, this.getInventoryStackLimit(), ActionType.getTypeForAction(simulate));
		return bool ? add.getActualStack() : ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		final ItemStack stored = getStackInSlot(slot);
		if (stored.isEmpty()) {
			return ItemStack.EMPTY;
		}
		StoredItemStack remove = new StoredItemStack(stored, amount);
		boolean bool = InventoryHelper.removeStack(this, remove, stored, slot, ActionType.getTypeForAction(simulate));
		remove = SonarAPI.getItemHelper().getStackToAdd(amount, new StoredItemStack(stored), remove);
		return remove.getActualStack();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.slots) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.getInventoryStackLimit();
	}
}
