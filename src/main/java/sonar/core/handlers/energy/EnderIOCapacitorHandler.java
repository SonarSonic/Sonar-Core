package sonar.core.handlers.energy;

import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.energy.GenericForgeEnergyHandler;

@EnergyHandler(modid = "EnderIO", priority = -1)
public class EnderIOCapacitorHandler implements ISonarEnergyHandler {

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof TileCapBank && GenericForgeEnergyHandler.canProvideEnergy(tile, dir);
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		TileCapBank bank = (TileCapBank) tile;
		ICapBankNetwork network = bank.getNetwork();
		if (network != null) {
			energyStack.setStorageValues(network.getEnergyStoredL(), network.getMaxEnergyStoredL());
			energyStack.setMaxInput(network.getMaxInput());
			energyStack.setMaxOutput(network.getMaxOutput());
			return energyStack;
		}
		return GenericForgeEnergyHandler.getEnergy(energyStack, tile, dir);
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		TileCapBank bank = (TileCapBank) tile;
		ICapBankNetwork network = bank.getNetwork();
		if (network != null) {
			int transferRF = Math.min(network.getMaxInput(), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
			transfer.stored -= network.receiveEnergy(transferRF, action.shouldSimulate());
			if (transfer.stored == 0)
				transfer = null;
			return transfer;
		}
		return GenericForgeEnergyHandler.addEnergy(transfer, tile, dir, action);
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		TileCapBank bank = (TileCapBank) tile;
		ICapBankNetwork network = bank.getNetwork();
		if (network != null) {
			int transferRF = Math.min(Math.min(network.getMaxOutput(), (int) (network.getEnergyStoredL())), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
			transfer.stored -= transferRF;
			if (!action.shouldSimulate()) {
				network.addEnergy(-transferRF);
			}
			if (transfer.stored == 0)
				transfer = null;
			return transfer;
		}
		return GenericForgeEnergyHandler.removeEnergy(transfer, tile, dir, action);
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.FE;
	}
}