package sonar.core.helpers;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.api.wrappers.EnergyWrapper;

public class EnergyHelper extends EnergyWrapper {
	/** changes = returns amount remaining */

	public long receiveEnergy(TileEntity tile, long maxReceive, EnumFacing dir, ActionType type) {
		if (maxReceive != 0 && tile != null) {
			EnergyHandler handler = this.canTransferEnergy(tile, dir);
			if (handler != null) {
				long receive = StoredEnergyStack.convert(maxReceive, EnergyType.RF, handler.getProvidedType());
				StoredEnergyStack stack = handler.addEnergy(new StoredEnergyStack(EnergyType.RF).setStackSize(receive), tile, dir, type);

				long remain = stack == null ? 0 : stack.getStackSize();
				remain = StoredEnergyStack.convert(remain, handler.getProvidedType(), EnergyType.RF);
				return maxReceive - remain;
			}
		}
		return 0;
	}

	public long extractEnergy(TileEntity tile, long maxExtract, EnumFacing dir, ActionType type) {
		if (maxExtract != 0 && tile != null) {
			EnergyHandler handler = this.canTransferEnergy(tile, dir);
			if (handler != null) {
				long receive = StoredEnergyStack.convert(maxExtract, EnergyType.RF, handler.getProvidedType());
				StoredEnergyStack stack = handler.removeEnergy(new StoredEnergyStack(EnergyType.RF).setStackSize(receive), tile, dir, type);

				long remain = stack == null ? 0 : stack.getStackSize();
				remain = StoredEnergyStack.convert(remain, handler.getProvidedType(), EnergyType.RF);
				return maxExtract - remain;
			}
		}
		return 0;
	}

	/** returns amount transferred **/
	public long transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, final long maxTransferRF) {
		if (from != null && !from.getWorld().isRemote && to != null && maxTransferRF != 0) {
			long maxTransfer = Math.min(extractEnergy(from, maxTransferRF, dirFrom, ActionType.SIMULATE), receiveEnergy(to, maxTransferRF, dirTo, ActionType.SIMULATE));
			if (maxTransfer != 0) {
				return extractEnergy(from, receiveEnergy(to, maxTransfer, dirTo, ActionType.PERFORM), dirFrom, ActionType.PERFORM);
			}
		}

		return 0;
	}
	public EnergyHandler canTransferEnergy(TileEntity tile, EnumFacing dir) {
		List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects();
		for (EnergyHandler handler : handlers) {
			if (handler.canProvideEnergy(tile, dir)) {
				return handler;
			}
		}
		return null;
	}
}
