package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

public class TileEntitySidedInventory extends TileEntityInventory implements IMachineSides, ISidedInventory {

	public MachineSides sides = new MachineSides(MachineSideConfig.INPUT, this, MachineSideConfig.NONE);
	public int[] input, output;
	
	public TileEntitySidedInventory() {
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

    @Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability || super.hasCapability(capability, facing);
	}

    @Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
			return (T) inv.getItemHandler(facing);
		}
		return super.getCapability(capability, facing);
	}
}