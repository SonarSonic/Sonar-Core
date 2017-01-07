package sonar.core.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import sonar.core.api.SonarAPI;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.SonarInventory;

public class TileEntityEnergyInventory extends TileEntityEnergy implements IInventory {

	public void discharge(int id) {
		slots()[id] = SonarAPI.getEnergyHelper().dischargeItem(slots()[id], this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public void charge(int id) {
		slots()[id] = SonarAPI.getEnergyHelper().chargeItem(slots()[id], this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public SonarInventory inv;

	public SonarInventory getTileInv() {
		return inv;
	}
	
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
			return (T) inv.getItemHandler(facing);
		}
		return super.getCapability(capability, facing);
	}
	
	public ItemStack[] slots() {
		return inv.slots;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		getTileInv().readData(nbt, type);
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		getTileInv().writeData(nbt, type);
		return nbt;
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

	public ItemStack removeStackFromSlot(int slot) {
		return getTileInv().getStackInSlot(slot);
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getTileInv().setInventorySlotContents(i, itemstack);
	}

	public int getInventoryStackLimit() {
		return getTileInv().getInventoryStackLimit();
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
		if (this.blockType == null) {
			return "Sonar Inventory";
		}
		return this.blockType.getLocalizedName();
	}

	public boolean hasCustomName() {
		return false;
	}

	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(this.blockType.getLocalizedName());
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
