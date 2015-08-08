package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cofh.api.tileentity.IReconfigurableSides;

public abstract class TileEntitySidedInventory extends TileEntityInventory implements IReconfigurableSides, ISidedInventory {

	public int[] sides = new int[6];
	public int[] input, output;

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.sides = nbt.getIntArray("SideConfig");
		if (sides == null || sides.length != 6) {
			sides = new int[6];
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setIntArray("SideConfig", this.sides);

	}

	public boolean getBlockTexture(int side, int metadata) {
		if (sides == null || sides.length != 6) {
			sides = new int[6];
		}
		if (side > sides.length) {
			return true;
		}
		return sides[side] == 0;
	}

	public abstract void sendPacket(int dimension, int side, int value);

	@Override
	public int[] getAccessibleSlotsFromSide(int slot) {
		return sides[slot] == 0 ? input : output;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return true;
	}

	@Override
	public boolean decrSide(int side) {
		if (!this.getWorldObj().isRemote) {
			if (sides[side] >= getNumConfig(side)-1) {
				sides[side] = 0;
			} else {
				sides[side] = getNumConfig(side)-1;
			}
			sendPacket(this.worldObj.provider.dimensionId, side, sides[side]);
		}
		return false;
	}

	@Override
	public boolean incrSide(int side) {
		if (!this.getWorldObj().isRemote) {
			if (sides[side] >= getNumConfig(side)-1) {
				sides[side] = 0;
			} else {
				sides[side] = getNumConfig(side)-1;
			}
			sendPacket(this.worldObj.provider.dimensionId, side, sides[side]);
		}
		return false;
	}

	@Override
	public boolean resetSides() {
		return false;
	}

	@Override
	public int getNumConfig(int side) {
		return 2;
	}

	@Override
	public boolean setSide(int side, int config) {
		sides[side] = config;
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return true;
	}

}