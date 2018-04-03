package sonar.core.inventory;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

	public final SubItemHandler sub_handler;
	public NonNullList<ItemStack> slots;
	public int size;
	public int limit = 64;
	public EnumFacing face;	

	public AbstractSonarInventory(int size) {
		super();
		this.size = size;
		this.slots = NonNullList.withSize(size, ItemStack.EMPTY);
		this.sub_handler = new SubItemHandler(this);
	}
	
	public static class SubItemHandler implements IItemHandler{
		
		public AbstractSonarInventory inv;
		
		public SubItemHandler(AbstractSonarInventory inv){
			this.inv = inv;
		}

		@Override
		public int getSlots() {
			return inv.getSizeInventory();
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
			return inv.getInventoryStackLimit();
		}
	}

	public List<ItemStack> slots() {
		return this.slots;
	}

	@Override
	public IItemHandler getItemHandler(EnumFacing side) {
		face = side;
		return sub_handler;
	}

	@Override
	public T setStackLimit(int limit) {
		this.limit = limit;
		return (T) this;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (canSync(type)) {
			this.slots = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(nbt, this.slots);
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (canSync(type)) {
			ItemStackHelper.saveAllItems(nbt, this.slots);
		}
		return nbt;
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slots.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack split = ItemStackHelper.getAndSplit(this.slots, index, count);
		if (!ItemStack.areItemsEqual(split, slots.get(index))) {
			markDirty();
		}
		return split;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack remove = ItemStackHelper.getAndRemove(this.slots, index);
		if (!ItemStack.areItemsEqual(remove, slots.get(index))) {
			markDirty();
		}
		return remove;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.slots.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return limit;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); i++){
			this.setInventorySlotContents(i, ItemStack.EMPTY);
		}
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
		return sync.isType(getSyncTypes());
	}

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.SAVE };
	}

	@Override
	public String getTagName() {
		return "Items";
	}
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		StoredItemStack add = new StoredItemStack(stack);
		boolean bool = InventoryHelper.addStack(this, add, slot, this.getInventoryStackLimit(), ActionType.getTypeForAction(simulate));
		return bool ? add.getActualStack() : ItemStack.EMPTY;
	}

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
}
