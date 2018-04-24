package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

import javax.annotation.Nonnull;

public class TileEntitySidedInventory extends TileEntityInventory implements IMachineSides, ISidedInventory {

	public MachineSides sides = new MachineSides(MachineSideConfig.INPUT, this, MachineSideConfig.NONE);
	public int[] input, output;
	
	public TileEntitySidedInventory() {
		super();
		syncList.addPart(sides);
	}

	@Nonnull
    @Override
	public int[] getSlotsForFace(@Nonnull EnumFacing side) {
		return sides.getSideConfig(side).isInput() ? input : output;
	}

	@Override
	public boolean canInsertItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
		return sides.getSideConfig(direction).isInput() && inv.isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
		return sides.getSideConfig(direction).isOutput() && inv.isItemValidForSlot(index, stack);
	}

	@Override
	public MachineSides getSideConfigs() {
		return sides;
	}
}