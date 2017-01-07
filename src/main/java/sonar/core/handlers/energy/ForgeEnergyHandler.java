package sonar.core.handlers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

@EnergyHandler(modid = "sonarcore", handlerID = ForgeEnergyHandler.name)
public class ForgeEnergyHandler implements ISonarEnergyHandler {

	public static final String name = "Forge-Energy-Handler";

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, dir);
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile == null) {
			return energyStack;
		}
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		energyStack.setStorageValues(storage.getEnergyStored(), storage.getMaxEnergyStored());
		energyStack.setMaxInput(storage.canReceive() ? storage.receiveEnergy(Integer.MAX_VALUE, true) : 0);
		energyStack.setMaxOutput(storage.canExtract() ? storage.extractEnergy(Integer.MAX_VALUE, true) : 0);
		return energyStack;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		if (dir == null || storage.canReceive()) {
			int transferRF = Math.min(storage.getMaxEnergyStored(), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
			transfer.stored -= storage.receiveEnergy(transferRF, action.shouldSimulate());
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		if (dir == null || storage.canExtract()) {
			int transferRF = Math.min(storage.getMaxEnergyStored(), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
			transfer.stored -= storage.extractEnergy(transferRF, action.shouldSimulate());
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}
}