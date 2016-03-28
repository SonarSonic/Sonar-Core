package sonar.core.integration.fmp.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.SonarTileInventory;

public abstract class InventoryTileHandler extends TileHandler implements IInventory {

	public InventoryTileHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public SonarTileInventory inv;

	public SonarTileInventory getTileInv() {
		return inv;
	}

	public ItemStack[] slots() {
		return inv.slots;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE)
			getTileInv().readData(nbt, type);
	}

	@Override
	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE)
			getTileInv().writeData(nbt, type);
	}

	public int getSizeInventory() {
		return getTileInv().getSizeInventory();
	}

	public ItemStack getStackInSlot(int slot) {
		return getTileInv().getStackInSlot(slot);
	}

	public ItemStack decrStackSize(int slot, int var2) {
		return getTileInv().decrStackSize(slot, var2);
	}

	public ItemStack getStackInSlotOnClosing(int slot) {
		return getTileInv().getStackInSlot(slot);
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getTileInv().setInventorySlotContents(i, itemstack);
	}

	public int getInventoryStackLimit() {
		return getTileInv().getInventoryStackLimit();
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	public void openInventory(EntityPlayer player) {
		getTileInv().openInventory(player);
	}

	public void closeInventory(EntityPlayer player) {
		getTileInv().closeInventory(player);
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return getTileInv().isItemValidForSlot(slot, stack);
	}

	public String getName() {
		if (this.tile.getBlockType() == null) {
			return "Sonar Inventory";
		}
		return tile.getBlockType().getLocalizedName();
	}

	public boolean hasCustomName() {
		return false;
	}

	public IChatComponent getDisplayName() {
		return new ChatComponentText(tile.getBlockType().getLocalizedName());
	}

	public int getField(int id) {
		return getTileInv().getField(id);
	}

	public void setField(int id, int value) {
		getTileInv().setField(id, value);
	}

	public int getFieldCount() {
		return getTileInv().getFieldCount();
	}

	public void clear() {
		getTileInv().clear();
	}

}
