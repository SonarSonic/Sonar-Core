package sonar.core.common.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.handlers.inventories.SonarLargeInventory;
import sonar.core.handlers.inventories.SonarLargeInventoryTile;

public class TileEntityLargeInventory extends TileEntitySonar implements ISonarInventoryTile {
	public final SonarLargeInventoryTile inv = new SonarLargeInventoryTile(this);

	public TileEntityLargeInventory() {}

	public TileEntityLargeInventory(int size, int stackSize) {
		inv.setSize(size);
		inv.setStackSize(stackSize);
		syncList.addPart(inv);
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

	@Override
	public SonarLargeInventory inv() {
		return inv;
	}
}
