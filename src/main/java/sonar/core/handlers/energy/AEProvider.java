package sonar.core.handlers.energy;

import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.me.helpers.IGridProxyable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.AE2Helper;

@EnergyHandler(modid = "appliedenergistics2", priority = 4)
public class AEProvider implements ISonarEnergyHandler {

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
        return !tile.isInvalid() && (tile instanceof IAEPowerStorage || tile instanceof IEnergyGrid);
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			energyStack.setUsage((long) grid.getAvgPowerUsage());
			energyStack.setStorageValues((long) grid.getStoredPower(), (long) grid.getMaxStoredPower());
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage power = (IAEPowerStorage) tile;
			energyStack.setStorageValues((long) power.getAECurrentPower(), (long) power.getAEMaxPower());
		}
		return energyStack;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
        if (!(tile instanceof IGridProxyable) || ((IGridProxyable) tile).getProxy().getNode() != null) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
                double max = Math.min(transfer.stored, grid.getMaxStoredPower() - grid.getStoredPower());
                transfer.stored -= max - grid.injectPower(max, AE2Helper.getActionable(action));
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
                double max = Math.min(transfer.stored, grid.getAEMaxPower() - grid.getAECurrentPower());
                transfer.stored -= max - grid.injectAEPower(max, AE2Helper.getActionable(action));
            }
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
        if (!(tile instanceof IGridProxyable) || ((IGridProxyable) tile).getProxy().getNode() != null) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
                double max = Math.min(transfer.stored, grid.getMaxStoredPower() - grid.getStoredPower());
                transfer.stored -= grid.extractAEPower(max, AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
                double max = Math.min(transfer.stored, grid.getAECurrentPower());
                transfer.stored -= grid.extractAEPower(max, AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
            }
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.AE;
	}
}
