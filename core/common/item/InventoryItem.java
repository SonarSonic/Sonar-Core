package sonar.core.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryItem implements IInventory {
	private String name = "Calculator Inventory Item";
	private ItemStack[] slots;
	private final ItemStack invStack;
	public String tag;

	public InventoryItem(ItemStack stack, int size) {
		slots = new ItemStack[size];
		this.invStack = stack;
		if (!invStack.hasTagCompound()) {
			invStack.setTagCompound(new NBTTagCompound());
		}
		readFromNBT(invStack.getTagCompound());
	}

	public InventoryItem(ItemStack stack, int size, String tag) {
		slots = new ItemStack[size];
		this.tag = tag;
		this.invStack = stack;
		if (!invStack.hasTagCompound()) {
			invStack.setTagCompound(new NBTTagCompound());
		}
		readFromNBT(invStack.getTagCompound());
	}

	@Override
	public int getSizeInventory() {
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slots[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize > amount) {
				stack = stack.splitStack(amount);
				markDirty();
			} else {
				setInventorySlotContents(slot, null);
			}
		}

		return stack;
	}

	public ItemStack incrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		stack.stackSize += amount;
		markDirty();
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		slots[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return name.length() > 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0)
				slots[i] = null;
		}
		writeToNBT(invStack.getTagCompound());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getHeldItem() == invStack;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return !(stack.getItem() instanceof InventoryContainerItem);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = null;
		if (tag == null) {
			items = compound.getTagList("ItemInventory",
					Constants.NBT.TAG_COMPOUND);
		} else {
			items = compound.getTagList(tag, Constants.NBT.TAG_COMPOUND);
		}
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte slot = item.getByte("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				slots[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}

	}

	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList items = new NBTTagList();
		int size = 0;
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
				size++;
			}
		}
		if (tag == null) {
			compound.setTag("ItemInventory", items);
			compound.setInteger("ItemInventory" + "size", size);
		} else {
			compound.setTag(tag, items);
			compound.setInteger(tag + "size", size);
		}
	}

	public int getItemsStored(ItemStack stack) {
		if (stack.hasTagCompound()) {

			if (tag == null) {
				return stack.stackTagCompound.getInteger("ItemInventory"+ "size");
			} else {
				return stack.stackTagCompound.getInteger(tag + "size");
			}
		} else {
			return 0;
		}
	}
}