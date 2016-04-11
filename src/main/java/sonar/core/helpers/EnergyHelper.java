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
	/* public StoredEnergyStack getStackToAdd(long inputSize, StoredEnergyStack stack, StoredEnergyStack returned) { StoredEnergyStack simulateStack = null; System.out.print(stack.stored); if (returned == null || returned.stored == 0) { simulateStack = stack.copy().setStackSize(inputSize); } else { simulateStack = stack.copy().setStackSize(inputSize - returned.stored); } return simulateStack; } */
	public long receiveEnergy(TileEntity tile, long maxReceive, EnumFacing dir, ActionType type) {
		if (maxReceive != 0 && tile != null) {
			List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects();
			for (EnergyHandler handler : handlers) {
				if (handler.canProvideEnergy(tile, dir)) {
					// maxReceive is converted from RF to the handlers type
					maxReceive = StoredEnergyStack.convert(maxReceive, EnergyType.RF, handler.getProvidedType());
					// the receiving is performed in the correct format
					maxReceive = handler.receiveEnergy(maxReceive, tile, dir, type);
					// then converted back to RF
					maxReceive = StoredEnergyStack.convert(maxReceive, handler.getProvidedType(), EnergyType.RF);
					return maxReceive;
				}
			}
		}
		return maxReceive;
	}

	public long extractEnergy(TileEntity tile, long maxExtract, EnumFacing dir, ActionType type) {
		if (maxExtract !=0 && tile != null) {
			List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects();
			for (EnergyHandler handler : handlers) {
				if (handler.canProvideEnergy(tile, dir)) {
					// maxReceive is converted from RF to the handlers type
					maxExtract = StoredEnergyStack.convert(maxExtract, EnergyType.RF, handler.getProvidedType());
					// the extracting is performed in the correct format
					maxExtract = handler.extractEnergy(maxExtract, tile, dir, type);
					// then converted back to RF
					maxExtract = StoredEnergyStack.convert(maxExtract, handler.getProvidedType(), EnergyType.RF);
					return maxExtract;
				}
			}
		}
		return maxExtract;
	}

	public long transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, final long maxTransferRF) {
		if (from != null && !from.getWorld().isRemote && to != null && maxTransferRF != 0) {
			long maxExtract = extractEnergy(from, maxTransferRF, dirFrom, ActionType.SIMULATE);
			long maxReceive = receiveEnergy(to, maxTransferRF, dirTo, ActionType.SIMULATE);
			long maxTransfer = Math.min(maxTransferRF - maxExtract, maxTransferRF - maxReceive);
			// System.out.print(maxTransfer);
			if (maxTransfer != 0) {
				receiveEnergy(to, maxTransfer, dirTo, ActionType.PERFORM);
				extractEnergy(from, maxTransfer, dirFrom, ActionType.PERFORM);
				return maxTransfer;
			}
		}
		return 0;
	}
	
	
}
