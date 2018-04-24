package sonar.core.api.inventories;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.ISonarStack;
import sonar.core.helpers.NBTHelper.SyncType;

public class StoredItemStack implements ISonarStack<StoredItemStack> {

	public ItemStack item = ItemStack.EMPTY;
	public long stored;

	public StoredItemStack() {}

	public StoredItemStack(ItemStack stack) {
		this.item = stack.copy();
		this.stored = stack.getCount();
	}

	public StoredItemStack(ItemStack stack, long stored) {
		this.item = stack.copy();
		this.stored = stored;
	}

	public void add(ItemStack stack) {
		if (equalStack(stack)) {
			stored += stack.getCount();
		}
	}

	public void remove(ItemStack stack) {
		if (equalStack(stack)) {
			stored -= stack.getCount();
		}
	}

    @Override
	public void add(StoredItemStack stack) {
		if (equalStack(stack.item)) {
			stored += stack.stored;
		}
	}

    @Override
	public void remove(StoredItemStack stack) {
		if (equalStack(stack.item)) {
			stored -= stack.stored;
		}
	}

    @Override
	public StoredItemStack copy() {
		return new StoredItemStack(this.item, this.stored);
	}

    public StoredItemStack setStackSize(StoredItemStack stack) {
        this.stored = stack == null ? 0 : stack.getStackSize();
        return this;
    }

    @Override
	public StoredItemStack setStackSize(long size) {
		this.stored = size;
		return this;
	}

    public static boolean isEqualStack(ItemStack main, ItemStack adding){
        return !main.isEmpty() && !adding.isEmpty() && main.isItemEqual(adding) && ItemStack.areItemStackTagsEqual(adding, main);
    }
    
	public boolean equalStack(ItemStack stack) {
        return isEqualStack(item, stack);
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		item = new ItemStack(nbt);
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
            return equalStack(target.item) && this.stored == target.stored;
		}
		return false;
	}

	public ItemStack getItemStack() {
		return item;
	}

    @Override
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
		stack.setCount(min);
		return stack;
	}

	public ItemStack getActualStack() {
		ItemStack fullStack = getFullStack();
		if (fullStack.getCount() <= 0) {
			return ItemStack.EMPTY;
		}
		return fullStack;
	}

	public static ItemStack getActualStack(StoredItemStack stack) {
		if (stack == null) {
			return ItemStack.EMPTY;
		}
		return stack.getActualStack();
	}

	@Override
	public StorageTypes getStorageType() {
		return StorageTypes.ITEMS;
	}

	public String toString() {
		if (!item.isEmpty()) {
            return this.stored + "x" + this.item.getUnlocalizedName() + '@' + item.getItemDamage();
		} else {
			return super.toString() + " : EMPTY";
		}
	}
}
