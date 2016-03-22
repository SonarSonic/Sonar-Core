package sonar.core.inventory;

import sonar.core.utils.helpers.NBTHelper.SyncType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class SonarTileInventory implements IInventory {

	public ItemStack[] slots;
	public int limit = 64;
	public final TileEntity tile;

	public SonarTileInventory(TileEntity tile, int size) {
		this.slots = new ItemStack[size];
		this.tile = tile;
	}

	public SonarTileInventory setStackLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			NBTTagList list = nbt.getTagList("Items", 10);
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

	public void writeData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < this.slots.length; i++) {
				if (this.slots[i] != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					this.slots[i].writeToNBT(compound);
					list.appendTag(compound);
				}
			}
			nbt.setTag("Items", list);
		}
	}

	public int getSizeInventory() {
		return this.slots.length;
	}

	public ItemStack getStackInSlot(int slot) {
		return this.slots[slot];
	}

	public ItemStack decrStackSize(int slot, int var2) {
		if (this.slots[slot] != null) {

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

	public String getName() {
		return tile.getBlockType().getUnlocalizedName();
	}

	public boolean hasCustomName() {
		return false;
	}

	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(tile.getBlockType().getUnlocalizedName());
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

	@Override
	public void markDirty() {
		tile.markDirty();
	}

}
