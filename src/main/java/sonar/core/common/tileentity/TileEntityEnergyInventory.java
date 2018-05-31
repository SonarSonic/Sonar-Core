package sonar.core.common.tileentity;

import net.minecraft.item.ItemStack;
import sonar.core.api.SonarAPI;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.inventory.SonarInventoryTile;

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

	public void discharge(int id) {		
		SonarAPI.getEnergyHelper().dischargeItem(slots().get(id), this, CHARGING_RATE != 0 ? Math.min(CHARGING_RATE, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public void charge(int id) {
		SonarAPI.getEnergyHelper().chargeItem(slots().get(id), this, CHARGING_RATE != 0 ? Math.min(CHARGING_RATE, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}
}
