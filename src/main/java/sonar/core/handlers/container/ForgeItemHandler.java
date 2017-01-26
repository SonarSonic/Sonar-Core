package sonar.core.handlers.container;

import net.minecraft.item.ItemStack;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.energy.GenericForgeEnergyHandler;

@EnergyContainerHandler(modid = "sonarcore", handlerID = ForgeItemHandler.name, priority = 0)
public class ForgeItemHandler implements ISonarEnergyContainerHandler {

	public static final String name = "Forge Item Handler";

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return GenericForgeEnergyHandler.canProvideEnergy(stack, null);
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		return GenericForgeEnergyHandler.addEnergy(transfer, stack, null, action);
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		return GenericForgeEnergyHandler.removeEnergy(transfer, stack, null, action);
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, ItemStack stack) {
		energyStack = GenericForgeEnergyHandler.getEnergy(energyStack, stack, null);
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.FE;
	}

}
