package sonar.core.common.tileentity;

import sonar.core.api.SonarAPI;
import sonar.core.inventory.ISonarInventory;
import sonar.core.inventory.ISonarInventoryTile;

public class TileEntityEnergyInventory extends TileEntityEnergy implements ISonarInventoryTile {

	public ISonarInventory inv;

	public ISonarInventory inv() {
		return inv;
	}
	
	public void discharge(int id) {		
		SonarAPI.getEnergyHelper().dischargeItem(slots().get(id), this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public void charge(int id) {
		SonarAPI.getEnergyHelper().chargeItem(slots().get(id), this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}
}
