package sonar.core.common.item;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import sonar.core.utils.SonarCompat;

public class InventoryItem implements IInventory {
	private String name = "Inventory Item";
	private final ItemStack invItem;
	public List<ItemStack> inventory;
	private static String invTag = "inv";
	private String tag;
	private boolean useStackTag;
	public int size;

	public InventoryItem(ItemStack stack, int size, String tag, boolean useStackTag) {
		inventory = Lists.newArrayListWithCapacity(size);
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

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (!SonarCompat.isEmpty(stack)) {
			if (SonarCompat.getCount(stack) > amount) {
				stack = stack.splitStack(amount);
				markDirty();
			} else {
				setInventorySlotContents(slot, SonarCompat.getEmpty());
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, SonarCompat.getEmpty());
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		setInventorySlotContents(slot, stack, false);
	}

	public void setInventorySlotContents(int slot, ItemStack stack, boolean isRemote) {
		inventory.set(slot, stack);

		if (!SonarCompat.isEmpty(stack) && SonarCompat.getCount(stack) > getInventoryStackLimit()) {
			stack = SonarCompat.setCount(stack, getInventoryStackLimit());
		}
		if (!isRemote) {
			markDirty();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return name.length() > 0;
	}

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
			writeToNBT(invItem.getSubCompound(tag, true));
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return !(itemstack.getItem() instanceof InventoryContainerItem);
	}

	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList items = compound.getTagList(invTag, Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				inventory.set(slot, SonarCompat.getItem(item));
			}
		}
	}

	/** A custom method to write our inventory to an ItemStack's NBT compound */
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList items = new NBTTagList();

		int size = 0;
		for (int i = 0; i < getSizeInventory(); ++i) {
			ItemStack stack = getStackInSlot(i);
			if (!SonarCompat.isEmpty(stack)) {
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
			return stack.getSubCompound(tag, true).getInteger("invsize");
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
			this.setInventorySlotContents(i, SonarCompat.getEmpty());
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventory) {
			if (!SonarCompat.isEmpty(itemstack)) {
				return false;
			}
		}
		return true;
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	//typo
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return isUsableByPlayer(player);
	}
}