package sonar.core.common.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import sonar.core.inventory.ILargeInventory;
import sonar.core.inventory.SonarLargeInventory;

public class TileEntityLargeInventory extends TileEntitySonar implements ILargeInventory {
	public SonarLargeInventory inv;

	public TileEntityLargeInventory() {}
	
	public TileEntityLargeInventory(int size, int numStacks) {
		inv = new SonarLargeInventory(this, size, numStacks);
		syncParts.add(inv);
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {
			return (T) inv;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public SonarLargeInventory getTileInv() {
		return inv;
	}

}
