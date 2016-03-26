package sonar.core.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;


public class StoredItemStack {

	public ItemStack item;
	public long stored;

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

	public static StoredItemStack readFromNBT(NBTTagCompound tag) {
		return new StoredItemStack(ItemStack.loadItemStackFromNBT(tag), tag.getLong("stored"));
	}

	public static void writeToNBT(NBTTagCompound tag, StoredItemStack storedStack) {
		storedStack.item.writeToNBT(tag);
		tag.setLong("stored", storedStack.stored);
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

	public int getItemDamage() {
		return item.getItemDamage();
	}

	public NBTTagCompound getTagCompound() {
		return item.getTagCompound();
	}

	public ItemStack getFullStack() {
		int min = (int) Math.min(stored, item.getMaxStackSize());
		ItemStack stack = item.copy();
		stack.stackSize = min;
		return stack;
	}
}
