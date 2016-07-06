package sonar.core.handlers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.common.tileentity.TileEntityEnergy.EnergyMode;
import sonar.core.network.sync.SyncEnergyStorage;

/**a special EnergyHandler specifically for charging items*/
public class SpecialSonarHandler extends EnergyHandler {

	public static String name = "Sonar-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile != null && (tile instanceof ISonarEnergyTile);
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile == null) {
			return;
		}
		SyncEnergyStorage storage = (SyncEnergyStorage) ((ISonarEnergyTile) tile).getStorage();
		energyStack.setStorageValues(storage.getEnergyStored(), storage.getMaxEnergyStored());
		energyStack.setMaxInput(storage.receiveEnergy(Integer.MAX_VALUE, true));
		energyStack.setStorageValues(storage.getEnergyStored(), storage.getMaxEnergyStored());
		energyStack.setMaxOutput(storage.extractEnergy(Integer.MAX_VALUE, true));
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		EnumFacing side = dir;
		SyncEnergyStorage storage = (SyncEnergyStorage) ((ISonarEnergyTile) tile).getStorage();
		int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
		transfer.stored -= storage.receiveEnergy(transferRF, action.shouldSimulate());
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		EnumFacing side = dir;
		SyncEnergyStorage storage = (SyncEnergyStorage) ((ISonarEnergyTile) tile).getStorage();

		transfer.stored -= storage.extractEnergy(transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE, action.shouldSimulate());

		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}
}