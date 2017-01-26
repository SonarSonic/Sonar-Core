package sonar.core.handlers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.energy.GenericTeslaHandler;

@EnergyHandler(modid = "tesla", handlerID = TeslaHandler.name, priority = 2)
public class TeslaHandler implements ISonarEnergyHandler {

	public static final String name = "TESLA-Provider";

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		return GenericTeslaHandler.canProvideEnergy(tile, dir);
	}

	@Override
	public StoredEnergyStack getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		return GenericTeslaHandler.getEnergy(energyStack, tile, dir);
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		return GenericTeslaHandler.addEnergy(transfer, tile, dir, action);
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		return GenericTeslaHandler.removeEnergy(transfer, tile, dir, action);
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.TESLA;
	}
}