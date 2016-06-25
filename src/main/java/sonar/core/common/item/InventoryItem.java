package sonar.core.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;

public class InventoryItem implements IInventory {
	private String name = "Inventory Item";
	private final ItemStack invItem;
	private ItemStack[] inventory;
	private static String invTag = "inv";
	private String tag;
	private boolean useStackTag;
	public int size;

	public InventoryItem(ItemStack stack, int size, String tag, boolean useStackTag) {
		inventory = new ItemStack[size];
		invItem = stack;
		this.tag = tag;
		this.useStackTag = useStackTag;
		if (useStackTag) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			readFromNBT(stack.getTagCompound());
		} else {
			readFromNBT(stack.getSubCompound(tag, true));
		}
	}

	public int getSizeInventory() {
		return inventory.length;
	}

	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

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

	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return stack;
	}

	public void setInventorySlotContents(int slot, ItemStack stack) {
		setInventorySlotContents(slot, stack, false);
	}

	public void setInventorySlotContents(int slot, ItemStack stack, boolean isRemote) {
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		if (!isRemote) {
			markDirty();
		}
	}

	public String getName() {
		return name;
	}

	public boolean hasCustomName() {
		return name.length() > 0;
	}

	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(name);
	}

	public boolean hasCustomInventoryName() {
		return name.length() > 0;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void markDirty() {
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				inventory[i] = null;
			}
		}
		if (useStackTag) {
			writeToNBT(invItem.getTagCompound());
		} else {
			writeToNBT(invItem.getSubCompound(tag, true));
		}
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
	}

	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return !(itemstack.getItem() instanceof InventoryContainerItem);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList(invTag, Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	/** A custom method to write our inventory to an ItemStack's NBT compound */
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList items = new NBTTagList();

		int size = 0;
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
				size++;
			}
		}
		compound.setTag(invTag, items);
		compound.setInteger("invsize", size);
	}

	public int getItemsStored(ItemStack stack) {
		if (this.useStackTag) {
			if (stack.hasTagCompound()) {
				return stack.getTagCompound().getInteger(tag + "size");
			} else {
				return 0;
			}
		} else {
			return stack.getSubCompound(tag, true).getInteger("invsize");
		}
	}

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

}