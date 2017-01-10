package sonar.core.handlers.energy;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.EUHelper;

@EnergyHandler(modid = "ic2", handlerID = EUProvider.name)
public class EUProvider implements ISonarEnergyHandler {

	public static final String name = "EU-Provider";

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergySink || tile instanceof IEnergySource || tile instanceof IEnergyStorage;
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IEnergyStorage) {
			IEnergyStorage storage = (IEnergyStorage) tile;
			energyStack.setStorageValues(storage.getStored() * 4, storage.getCapacity() * 4);
			energyStack.setMaxOutput((long) (storage.getOutputEnergyUnitsPerTick() * 4));
		}
		if (tile instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) tile;
			energyStack.setMaxInput((long) (sink.getDemandedEnergy() * 4));
		}
		if (tile instanceof IEnergySource) {
			IEnergySource source = (IEnergySource) tile;
			energyStack.setMaxOutput((long) (source.getOfferedEnergy() * 4));
		}
		return energyStack;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IEnergyStorage) {
			IEnergyStorage sink = (IEnergyStorage) tile;
			int before = sink.getStored();
			if (!action.shouldSimulate()) {
				transfer.stored -= sink.addEnergy((int) Math.min(sink.getCapacity() - before, transfer.stored));
			} else {
				transfer.stored -= Math.min(sink.getCapacity() - before, transfer.stored);
			}
		} else if (tile instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) tile;
			if (!action.shouldSimulate()) {
				transfer.stored -= (sink.injectEnergy(dir, transfer.stored, EUHelper.getVoltage(sink.getSinkTier())));
			} else {
				transfer.stored -= Math.min(sink.getDemandedEnergy(), EUHelper.getVoltage(sink.getSinkTier()));
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IEnergyStorage) {
			IEnergyStorage sink = (IEnergyStorage) tile;
			int before = sink.getStored();
			if (!action.shouldSimulate()) {
				transfer.stored -= sink.addEnergy(-(int) Math.min(before, transfer.stored));
			} else {
				transfer.stored -= Math.min(before, transfer.stored);
			}
		} else if (tile instanceof IEnergySource) {
			IEnergySource source = (IEnergySource) tile;
			double amount = Math.min(EUHelper.getVoltage(source.getSourceTier()),Math.min(transfer.stored, source.getOfferedEnergy()));
			if (!action.shouldSimulate()) {
				source.drawEnergy(amount);
				transfer.stored -= amount;
			} else {
				transfer.stored -= amount;
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.EU;
	}

}