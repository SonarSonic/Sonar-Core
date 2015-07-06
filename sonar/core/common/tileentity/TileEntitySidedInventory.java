package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.ISonarSides;

public abstract class TileEntitySidedInventory extends TileEntityInventory implements ISonarSides, ISidedInventory {

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
		if (side == metadata) {
			return sides[side] == 0;
		}
		if (metadata != side) {
			return sides[side] == 0;
		}

		return false;
	}

	@Override
	public boolean canBeConfigured() {
		return true;
	}

	@Override
	public void increaseSide(int side, int dimension) {
		if (!this.worldObj.isRemote) {
			if (sides[side] >= 1) {
				sides[side] = 0;
			} else {
				sides[side] = 1;
			}
			sendPacket(dimension, side, sides[side]);
		}
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
	public void setSide(int side, int value) {
		this.sides[side] = value;
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

	}

}