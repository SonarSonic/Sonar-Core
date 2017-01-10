package sonar.core.handlers.container;

import net.minecraft.item.ItemStack;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.energy.GenericTeslaHandler;

@EnergyContainerHandler(modid = "tesla", handlerID = TeslaItemHandler.name)
public class TeslaItemHandler implements ISonarEnergyContainerHandler {

	public static final String name = "TESLA Item Handler";

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return GenericTeslaHandler.canProvideEnergy(stack, null);
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, ItemStack stack) {
		energyStack = GenericTeslaHandler.getEnergy(energyStack, stack, null);
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		return GenericTeslaHandler.addEnergy(transfer, stack, null, action);
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		return GenericTeslaHandler.removeEnergy(transfer, stack, null, action);
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.TESLA;
	}

}
