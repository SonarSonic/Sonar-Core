package sonar.core.inventory;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
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

	public ItemStack[] slots;
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

	}

	public AbstractSonarInventory(int size) {
		this.slots = new ItemStack[size];
	}

	public IItemHandler getItemHandler(EnumFacing side){
		face = side;
		return embeddedHandler;
	}

	public T setStackLimit(int limit) {
		this.limit = limit;
		return (T) this;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.SAVE)) {
			NBTTagList list = nbt.getTagList(getTagName(), 10);
			this.slots = new ItemStack[this.getSizeInventory()];
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				byte b = compound.getByte("Slot");
				if (b >= 0 && b < this.slots.length) {
					this.slots[b] = ItemStack.loadItemStackFromNBT(compound);
				}
			}
		}
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.SAVE)) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < this.slots.length; i++) {
				if (this.slots[i] != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					this.slots[i].writeToNBT(compound);
					list.appendTag(compound);
				}
			}
			nbt.setTag(getTagName(), list);
		}
		return nbt;
	}

	public int getSizeInventory() {
		return this.slots.length;
	}

	public ItemStack getStackInSlot(int slot) {
		return this.slots[slot];
	}

	public ItemStack decrStackSize(int slot, int var2) {
		if (this.slots[slot] != null) {
			setChanged(true);
			if (this.slots[slot].stackSize <= var2) {
				ItemStack itemstack = this.slots[slot];
				this.slots[slot] = null;
				return itemstack;
			}
			ItemStack itemstack = this.slots[slot].splitStack(var2);

			if (this.slots[slot].stackSize == 0) {
				this.slots[slot] = null;
			}
			return itemstack;
		}

		return null;
	}

	public ItemStack removeStackFromSlot(int i) {
		if (this.slots[i] != null) {
			setChanged(true);
			ItemStack itemstack = this.slots[i];
			this.slots[i] = null;
			return itemstack;
		}
		return null;
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.slots[i] = itemstack;

		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		setChanged(true);
	}

	public int getInventoryStackLimit() {
		return limit;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
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
			this.setInventorySlotContents(i, null);
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
		return bool ? add.getActualStack() : null;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		final ItemStack stored = getStackInSlot(slot);
		if (stored == null || stored.stackSize == 0 || amount == 0) {
			return null;
		}
		StoredItemStack remove = new StoredItemStack(stored, amount);
		boolean bool = InventoryHelper.removeStack(this, remove, stored, slot, ActionType.getTypeForAction(simulate));
		remove = SonarAPI.getItemHelper().getStackToAdd(amount, new StoredItemStack(stored), remove);
		return remove.getActualStack();
	}

	public void markDirty() {
	}

}
