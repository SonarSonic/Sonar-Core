package sonar.core.common.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.handlers.energy.DischargeValues;
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
		EnergyTransferHandler.INSTANCE_SC.chargeItem(Lists.newArrayList(storage.getInternalWrapper()), slots().get(id), maxTransfer);
	}

	public void discharge(int id) {
		long maxTransfer = CHARGING_RATE != 0 ? Math.min(CHARGING_RATE, getStorage().getMaxExtract()) : getStorage().getMaxExtract();
		long transferred = EnergyTransferHandler.INSTANCE_SC.dischargeItem(Lists.newArrayList(storage.getInternalWrapper()), slots().get(id), maxTransfer);
		if(transferred == 0){
			int value = DischargeValues.getValueOf(slots().get(id));
			if(value > 0 && storage.getEnergyStored() + value <= storage.getMaxEnergyStored()){
				storage.setEnergyStored(value + storage.getEnergyStored());
				slots().get(id).shrink(1);
			}
		}
	}
}
