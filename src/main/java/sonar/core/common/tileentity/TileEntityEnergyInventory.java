package sonar.core.common.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.handlers.energy.EnergyTransferHandler;
import sonar.core.handlers.inventories.SonarInventoryTile;

import java.util.List;

public class TileEntityEnergyInventory extends TileEntityEnergy implements ISonarInventoryTile {

	public int CHARGING_RATE;

	public final SonarInventoryTile inv = new SonarInventoryTile(this);
	{
		syncList.addPart(inv);
	}

	public ISonarInventory inv() {
		return inv;
	}

	public List<ItemStack> slots(){
		return inv.slots();
	}

	public void charge(int id) {
		long maxTransfer = CHARGING_RATE != 0 ? Math.min(CHARGING_RATE, getStorage().getMaxReceive()) : getStorage().getMaxReceive();
		EnergyTransferHandler.chargeItem(Lists.newArrayList(storage.getInternalWrapper()), slots().get(id), maxTransfer);
	}

	public void discharge(int id) {
		long maxTransfer = CHARGING_RATE != 0 ? Math.min(CHARGING_RATE, getStorage().getMaxExtract()) : getStorage().getMaxExtract();
		EnergyTransferHandler.dischargeItem(Lists.newArrayList(storage.getInternalWrapper()), slots().get(id), maxTransfer);
	}
}
