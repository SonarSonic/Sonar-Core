package sonar.core.handlers.energy;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

@EnergyHandler(modid = "redstoneflux", priority = 3)
public class RFHandler implements ISonarEnergyHandler {

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile != null && (tile instanceof IEnergyReceiver || tile instanceof IEnergyProvider);
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile == null) {
			return energyStack;
		}
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			energyStack.setStorageValues(receiver.getEnergyStored(dir), receiver.getMaxEnergyStored(dir));
			int simulateAdd = receiver.receiveEnergy(dir, Integer.MAX_VALUE, true);
			energyStack.setMaxInput(simulateAdd);
		}
		if (tile instanceof IEnergyProvider) {
			IEnergyProvider provider = (IEnergyProvider) tile;
			energyStack.setStorageValues(provider.getEnergyStored(dir), provider.getMaxEnergyStored(dir));
			int simulateRemove = provider.extractEnergy(dir, Integer.MAX_VALUE, true);
			energyStack.setMaxOutput(simulateRemove);
		}
		return energyStack;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			if (dir == null || receiver.canConnectEnergy(dir)) {
				int transferRF = (int) Math.min(Integer.MAX_VALUE, transfer.stored);
				if (transferRF > 0)
					transfer.stored -= receiver.receiveEnergy(dir, transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IEnergyProvider) {
			IEnergyProvider receiver = (IEnergyProvider) tile;
			if (receiver.canConnectEnergy(dir)) {
				int transferRF = (int) Math.min(receiver.getEnergyStored(dir), Math.min(Integer.MAX_VALUE, transfer.stored));
				if (transferRF > 0)
					transfer.stored -= receiver.extractEnergy(dir, transferRF, action.shouldSimulate());
			}
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