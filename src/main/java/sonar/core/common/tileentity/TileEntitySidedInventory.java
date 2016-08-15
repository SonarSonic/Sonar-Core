package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

public abstract class TileEntitySidedInventory extends TileEntityInventory implements IMachineSides, ISidedInventory {

	public MachineSides sides = new MachineSides(MachineSideConfig.INPUT, this, MachineSideConfig.NONE);
	public int[] input, output;

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		sides.readFromNBT(nbt);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		sides.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return sides.getSideConfig(side).isInput() ? input : output;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		return sides.getSideConfig(direction).isInput() && inv.isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return sides.getSideConfig(direction).isOutput() && inv.isItemValidForSlot(index, stack);
	}

	@Override
	public MachineSides getSideConfigs() {
		return sides;
	}
}