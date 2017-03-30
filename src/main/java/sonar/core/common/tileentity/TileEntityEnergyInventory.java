package sonar.core.common.tileentity;

import java.util.List;

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
		SonarAPI.getEnergyHelper().dischargeItem(slots().get(id), this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public void charge(int id) {
		SonarAPI.getEnergyHelper().chargeItem(slots().get(id), this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public SonarInventory inv;

	public SonarInventory inv() {
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
	
	public List<ItemStack> slots() {
		return inv.slots;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		inv().readData(nbt, type);
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		inv().writeData(nbt, type);
		return nbt;
	}

	public int getSizeInventory() {
		return inv().getSizeInventory();
	}

	public ItemStack getStackInSlot(int slot) {
		return inv().getStackInSlot(slot);
	}

	public ItemStack decrStackSize(int slot, int var2) {
		return inv().decrStackSize(slot, var2);
	}

	public ItemStack removeStackFromSlot(int slot) {
		return inv().getStackInSlot(slot);
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv().setInventorySlotContents(i, itemstack);
	}

	public int getInventoryStackLimit() {
		return inv().getInventoryStackLimit();
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	public void openInventory(EntityPlayer player) {
		inv().openInventory(player);
	}

	public void closeInventory(EntityPlayer player) {
		inv().closeInventory(player);
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inv().isItemValidForSlot(slot, stack);
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
		return inv().getField(id);
	}

	public void setField(int id, int value) {
		inv().setField(id, value);
	}

	public int getFieldCount() {
		return inv().getFieldCount();
	}

	public void clear() {
		inv().clear();
	}

	@Override
	public boolean isEmpty() {
		return inv().isEmpty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return inv().isUsableByPlayer(player);
	}
}
