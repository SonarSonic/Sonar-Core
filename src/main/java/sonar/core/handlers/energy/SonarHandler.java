package sonar.core.handlers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.network.sync.SyncEnergyStorage;

public class SonarHandler extends EnergyHandler {

	public static String name = "Sonar-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile != null && (tile instanceof ISonarEnergyTile && (dir==null || ((ISonarEnergyTile) tile).getModeForSide(dir).canConnect()));
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		SyncEnergyStorage storage = (SyncEnergyStorage) ((ISonarEnergyTile) tile).getStorage();
		energyStack.setStorageValues(storage.getEnergyStored(), storage.getMaxEnergyStored());
		EnergyMode mode = dir==null? ((ISonarEnergyTile) tile).getModeForSide(dir) : EnergyMode.SEND_RECIEVE;
		if (mode.canRecieve()) {
			energyStack.setMaxInput(storage.addEnergy(Long.MAX_VALUE, ActionType.SIMULATE));
		}
		if (mode.canSend()) {
			energyStack.setMaxOutput(storage.removeEnergy(Long.MAX_VALUE, ActionType.SIMULATE));
		}
		return energyStack;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {	
		SyncEnergyStorage storage = (SyncEnergyStorage) ((ISonarEnergyTile) tile).getStorage();
		EnergyMode mode = dir==null? ((ISonarEnergyTile) tile).getModeForSide(dir) : EnergyMode.SEND_RECIEVE;
		if (mode.canRecieve()) {
			transfer.stored -= storage.addEnergy(transfer.stored, action);
		}			
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {	
		SyncEnergyStorage storage = (SyncEnergyStorage) ((ISonarEnergyTile) tile).getStorage();
		EnergyMode mode = dir==null? ((ISonarEnergyTile) tile).getModeForSide(dir) : EnergyMode.SEND_RECIEVE;
		if (mode.canSend()) {
			transfer.stored -= storage.removeEnergy(transfer.stored, action);
		}			
		return transfer;
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}
}