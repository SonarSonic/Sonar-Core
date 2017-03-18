package sonar.core.handlers.energy;

import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.AE2Helper;

@EnergyHandler(modid = "appliedenergistics2", handlerID = AEProvider.name, priority = 4)
public class AEProvider implements ISonarEnergyHandler {

	public static final String name = "AE-Provider";

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return (tile instanceof IAEPowerStorage || tile instanceof IEnergyGrid);
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
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			transfer.stored = (long) grid.injectPower(Math.min(transfer.stored, 10000), AE2Helper.getActionable(action));
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
			transfer.stored = (long) grid.injectAEPower(Math.min(transfer.stored, 10000), AE2Helper.getActionable(action));
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			transfer.stored -= grid.extractAEPower((double) Math.min(transfer.stored, 10000), AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
			transfer.stored -= grid.extractAEPower(Math.min(transfer.stored, 10000), AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
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
