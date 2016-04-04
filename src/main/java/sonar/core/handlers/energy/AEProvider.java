package sonar.core.handlers.energy;
/*
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.ActionType;
import sonar.core.api.EnergyHandler;
import sonar.core.api.EnergyType;
import sonar.core.api.StoredEnergyStack;
import sonar.core.integration.AE2Helper;

public class AEProvider extends EnergyHandler {

	public static String name = "AE-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return (tile instanceof IAEPowerStorage || tile instanceof IEnergyGrid);
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			energyStack.setUsage((long) grid.getAvgPowerUsage());
			energyStack.setStorageValues((long) grid.getStoredPower(), (long) grid.getMaxStoredPower());
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage power = (IAEPowerStorage) tile;
			energyStack.setStorageValues((long) power.getAECurrentPower(), (long) power.getAEMaxPower());
		}
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
		if(transfer.stored==0)
			transfer=null;
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
		if(transfer.stored==0)
			transfer=null;
		return transfer;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.AE;
	}
}
*/