package sonar.core.inventory;

import java.util.List;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.IItemHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.InventoryHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.SonarCompat;

public abstract class AbstractSonarInventory<T extends AbstractSonarInventory> extends DirtyPart implements ISonarInventory, ISyncPart {

	public final SubItemHandler sub_handler;
	public List<ItemStack> slots;
	public int size;
	public int limit = 64;
	public EnumFacing face;	

	public AbstractSonarInventory(int size) {
		super();
		this.size = size;
		this.slots = Lists.newArrayListWithCapacity(size);
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
			NBTTagList list = nbt.getTagList(getTagName(), 10);
			this.slots = Lists.newArrayListWithCapacity(this.getSizeInventory());
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				byte b = compound.getByte("Slot");
				if (b >= 0 && b < this.slots.size()) {
					slots.set(b, ItemStack.loadItemStackFromNBT(compound));
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (canSync(type)) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < this.slots.size(); i++) {
				ItemStack stack = slots.get(i);
				if (stack != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					stack.writeToNBT(compound);
					list.appendTag(compound);
				}
			}
			nbt.setTag(getTagName(), list);
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
		ItemStack current = slots.get(index);
		if (current != null) {
			if (current.stackSize <= count) {
				ItemStack itemstack = current;
				slots.set(index, SonarCompat.getEmpty());
				markDirty();
				return itemstack;
			}
			ItemStack itemstack = slots.get(index).splitStack(count);
	
			if (slots.get(index).stackSize == 0) {
				slots.set(index, SonarCompat.getEmpty());
			}
			markDirty();
			return itemstack;
		}
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (slots.get(index) != null) {
			ItemStack itemstack = slots.get(index);
			slots.set(index, SonarCompat.getEmpty());
			markDirty();
			return itemstack;
		}
		return SonarCompat.getEmpty();
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.slots.set(index, stack);
		if (SonarCompat.getCount(stack) > this.getInventoryStackLimit()) {
			stack = SonarCompat.setCount(stack, this.getInventoryStackLimit());
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
		for (int i = 0; i < this.getSizeInventory(); i++)
			this.setInventorySlotContents(i, SonarCompat.getEmpty());
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
		return bool ? add.getActualStack() : SonarCompat.getEmpty();
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		final ItemStack stored = getStackInSlot(slot);
		if (SonarCompat.isEmpty(stored)) {
			return SonarCompat.getEmpty();
		}
		StoredItemStack remove = new StoredItemStack(stored, amount);
		boolean bool = InventoryHelper.removeStack(this, remove, stored, slot, ActionType.getTypeForAction(simulate));
		remove = SonarAPI.getItemHelper().getStackToAdd(amount, new StoredItemStack(stored), remove);
		return remove.getActualStack();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.slots) {
			if (!SonarCompat.isEmpty(itemstack)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
}
