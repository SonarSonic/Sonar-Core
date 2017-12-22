package sonar.core.energy;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class GenericForgeEnergyHandler {

	public static boolean canProvideEnergy(ICapabilityProvider tile, EnumFacing dir) {
		return tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, dir);
	}

	public static StoredEnergyStack getEnergy(StoredEnergyStack energyStack, ICapabilityProvider tile, EnumFacing dir) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		if (storage != null) {
			energyStack.setStorageValues(storage.getEnergyStored(), storage.getMaxEnergyStored());
			energyStack.setMaxInput(storage.canReceive() ? storage.receiveEnergy(Integer.MAX_VALUE, true) : 0);
			energyStack.setMaxOutput(storage.canExtract() ? storage.extractEnergy(Integer.MAX_VALUE, true) : 0);
		}
		return energyStack;
	}

	public static StoredEnergyStack addEnergy(StoredEnergyStack transfer, ICapabilityProvider tile, EnumFacing dir, ActionType action) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		if (storage != null && (dir == null || storage.canReceive())) {
			int transferRF = (int) Math.min(Integer.MAX_VALUE, transfer.stored);
			if (transferRF > 0)
				transfer.stored -= storage.receiveEnergy(transferRF, action.shouldSimulate());
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	public static StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ICapabilityProvider tile, EnumFacing dir, ActionType action) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		if (storage != null && (dir == null || storage.canExtract())) {
			int transferRF = (int) Math.min(storage.getEnergyStored(), Math.min(Integer.MAX_VALUE, transfer.stored));
			if (transferRF > 0){
				transfer.stored -= storage.extractEnergy(transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}
}
