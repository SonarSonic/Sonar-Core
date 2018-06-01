package sonar.core.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class InventoryItem implements IInventory {
	private String name = "Inventory Item";
	private final ItemStack invItem;
	public NonNullList<ItemStack> inventory;
	private static String invTag = "inv";
	private String tag;
	private boolean useStackTag;
	public int size;

	public InventoryItem(ItemStack stack, int size, String tag, boolean useStackTag) {
		inventory = NonNullList.withSize(size, ItemStack.EMPTY);
		invItem = stack;
		this.tag = tag;
		this.useStackTag = useStackTag;
		if (useStackTag) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			readFromNBT(stack.getTagCompound());
		} else {
			readFromNBT(stack.getOrCreateSubCompound(tag));
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Nonnull
    @Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	@Nonnull
    @Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty()) {
			if (stack.getCount() > amount) {
				stack = stack.splitStack(amount);
				markDirty();
			} else {
				setInventorySlotContents(slot, ItemStack.EMPTY);
			}
		}
		return stack;
	}

	@Nonnull
    @Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		setInventorySlotContents(slot, stack, false);
	}

	public void setInventorySlotContents(int slot, ItemStack stack, boolean isRemote) {
		inventory.set(slot, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
		if (!isRemote) {
			markDirty();
		}
	}

	@Nonnull
    @Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return name.length() > 0;
	}

	@Nonnull
    @Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(name);
	}

	public boolean hasCustomInventoryName() {
		return name.length() > 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		if (useStackTag) {
			writeToNBT(invItem.getTagCompound());
		} else {
			writeToNBT(invItem.getOrCreateSubCompound(tag));
		}
	}

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
		return true;
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList(invTag, Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				inventory.set(slot, new ItemStack(item));
			}
		}
	}

	/** A custom method to write our inventories to an ItemStack's NBT compound */
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList items = new NBTTagList();

		int size = 0;
		for (int i = 0; i < getSizeInventory(); ++i) {
			ItemStack stack = getStackInSlot(i);
			if (!stack.isEmpty()) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				stack.writeToNBT(item);
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
			return stack.getOrCreateSubCompound(tag).getInteger("invsize");
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); i++)
			this.setInventorySlotContents(i, ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventory) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return true;
	}
}