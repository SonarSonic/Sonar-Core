package sonar.core.handlers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.energy.GenericForgeEnergyHandler;

@EnergyHandler(modid = "sonarcore", priority = 0)
public class ForgeEnergyHandler implements ISonarEnergyHandler {

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return GenericForgeEnergyHandler.canProvideEnergy(tile, dir);
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		return GenericForgeEnergyHandler.getEnergy(energyStack, tile, dir);
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		return GenericForgeEnergyHandler.addEnergy(transfer, tile, dir, action);
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		return GenericForgeEnergyHandler.removeEnergy(transfer, tile, dir, action);
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.FE;
	}
}