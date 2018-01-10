package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

public class TileEntityEnergySidedInventory extends TileEntityEnergyInventory implements IMachineSides, ISidedInventory {

	public MachineSides sides = new MachineSides(MachineSideConfig.INPUT, this, MachineSideConfig.NONE);
	public int[] input, output;
	
	public TileEntityEnergySidedInventory() {
		super();
		syncList.addPart(sides);
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