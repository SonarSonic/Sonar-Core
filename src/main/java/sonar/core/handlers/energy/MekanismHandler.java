package sonar.core.handlers.energy;

import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.CapabilityUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

@EnergyHandler(modid = "mekanism", priority = 4)
public class MekanismHandler implements ISonarEnergyHandler {

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
        return tile != null && CapabilityUtils.hasCapability(tile, Capabilities.ENERGY_STORAGE_CAPABILITY, dir) || CapabilityUtils.hasCapability(tile, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, dir) || tile instanceof IStrictEnergyStorage;
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
        IStrictEnergyStorage storage = CapabilityUtils.getCapability(tile, Capabilities.ENERGY_STORAGE_CAPABILITY, dir);
		if (storage == null && tile instanceof IStrictEnergyStorage) {
			storage = (IStrictEnergyStorage) tile;
		}
		if (storage != null) {
			energyStack.setStorageValues((long) (storage.getEnergy() / 10), (long) (storage.getMaxEnergy() / 10));
		}
		return energyStack;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		IStrictEnergyAcceptor acceptor = CapabilityUtils.getCapability(tile, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, dir);
		if (acceptor == null && tile instanceof IStrictEnergyAcceptor) {
			acceptor = (IStrictEnergyAcceptor) tile;
		}
		if (acceptor != null && acceptor.canReceiveEnergy(dir)) {			
            transfer.stored -= acceptor.acceptEnergy(dir, transfer.stored, action.shouldSimulate());
            /*IStrictEnergyStorage storage = CapabilityUtils.getCapability(tile, Capabilities.ENERGY_STORAGE_CAPABILITY, dir);
            if (storage == null && tile instanceof IStrictEnergyStorage) {
                storage = (IStrictEnergyStorage) tile;
            }
            transfer.stored -= action.shouldSimulate() ? Math.min(storage.getMaxEnergy() - storage.getEnergy(), transfer.stored) : acceptor.acceptEnergy(dir, transfer.stored, false);*/
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
        IStrictEnergyOutputter outputter = CapabilityUtils.getCapability(tile, Capabilities.ENERGY_OUTPUTTER_CAPABILITY, dir);
        if (outputter == null && tile instanceof IStrictEnergyOutputter) {
            outputter = (IStrictEnergyOutputter) tile;
        }
        if (outputter != null && outputter.canOutputEnergy(dir)) {
            IStrictEnergyStorage storage = CapabilityUtils.getCapability(tile, Capabilities.ENERGY_STORAGE_CAPABILITY, dir);
		if (storage == null && tile instanceof IStrictEnergyStorage) {
			storage = (IStrictEnergyStorage) tile;
		}
			double maxRemove = Math.min(transfer.stored, storage.getEnergy());
            transfer.stored -= action.shouldSimulate() ? maxRemove : outputter.pullEnergy(dir, maxRemove, false);
		}
		return transfer;
	}
	
	@Override
	public EnergyType getProvidedType() {
		return EnergyType.MJ;
	}
}
