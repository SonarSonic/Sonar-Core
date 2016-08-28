package sonar.core.api.inventories;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.ISonarStack;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncNBTAbstract;

public class StoredItemStack implements ISonarStack<StoredItemStack> {

	public ItemStack item;
	public long stored;

	public StoredItemStack() {
	}

	public StoredItemStack(ItemStack stack) {
		this.item = stack.copy();
		this.stored = stack.stackSize;
	}

	public StoredItemStack(ItemStack stack, long stored) {
		this.item = stack.copy();
		this.stored = stored;
	}

	public void add(ItemStack stack) {
		if (equalStack(stack)) {
			stored += stack.stackSize;
		}
	}

	public void remove(ItemStack stack) {
		if (equalStack(stack)) {
			stored -= stack.stackSize;
		}
	}

	public void add(StoredItemStack stack) {
		if (equalStack(stack.item)) {
			stored += stack.stored;
		}
	}

	public void remove(StoredItemStack stack) {
		if (equalStack(stack.item)) {
			stored -= stack.stored;
		}
	}

	public StoredItemStack copy() {
		return new StoredItemStack(this.item, this.stored);
	}

	public StoredItemStack setStackSize(long size) {
		this.stored = size;
		return this;
	}

	public boolean equalStack(ItemStack stack) {
		if (this.item == null || stack == null || stack.stackSize == 0) {
			return false;
		}
		if (!this.item.isItemEqual(stack)) {
			return false;
		}
		if (!ItemStack.areItemStackTagsEqual(stack, this.item)) {
			return false;
		}
		return true;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		item = ItemStack.loadItemStackFromNBT(nbt);
		stored = nbt.getLong("stored");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		item.writeToNBT(nbt);
		nbt.setLong("stored", stored);
		return nbt;
	}

	public static StoredItemStack readFromBuf(ByteBuf buf) {
		return new StoredItemStack(ByteBufUtils.readItemStack(buf), buf.readLong());
	}

	public static void writeToBuf(ByteBuf buf, StoredItemStack storedStack) {
		ByteBufUtils.writeItemStack(buf, storedStack.item);
		buf.writeLong(storedStack.stored);
	}

	public boolean equals(Object obj) {
		if (obj instanceof StoredItemStack) {
			StoredItemStack target = (StoredItemStack) obj;
			if (equalStack(target.item) && this.stored == target.stored) {
				return true;
			}
		}
		return false;
	}

	public ItemStack getItemStack() {
		return item;
	}

	public long getStackSize() {
		return stored;
	}

	public int getValidStackSize() {
		return (int) Math.min(stored, item.getMaxStackSize());
	}

	public int getItemDamage() {
		return item.getItemDamage();
	}

	public NBTTagCompound getTagCompound() {
		return item.getTagCompound();
	}

	public ItemStack getFullStack() {
		int min = getValidStackSize();
		ItemStack stack = item.copy();
		stack.stackSize = min;
		return stack;
	}

	public ItemStack getActualStack() {
		ItemStack fullStack = getFullStack();
		if (fullStack.stackSize <= 0) {
			return null;
		}
		return fullStack;
	}

	public static ItemStack getActualStack(StoredItemStack stack) {
		if (stack == null) {
			return null;
		}
		return stack.getActualStack();
	}

	@Override
	public StorageTypes getStorageType() {
		return StorageTypes.ITEMS;
	}

	public String toString() {
		if (item != null) {
			return this.stored + "x" + this.item.getUnlocalizedName() + "@" + item.getItemDamage();
		} else {
			return super.toString() + " : NULL";
		}
	}

}
