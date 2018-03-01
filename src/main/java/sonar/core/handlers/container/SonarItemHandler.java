package sonar.core.handlers.container;

import net.minecraft.item.ItemStack;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.ISonarEnergyItem;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;
import sonar.core.utils.SonarCompat;

@EnergyContainerHandler(modid = "sonarcore", priority = 1)
public class SonarItemHandler implements ISonarEnergyContainerHandler {

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return !SonarCompat.isEmpty(stack) && stack.getItem() instanceof ISonarEnergyItem;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		ISonarEnergyItem item = (ISonarEnergyItem) stack.getItem();
		if (item instanceof ISonarEnergyItem) {
            if (item.getFullCapacity(stack) > 0) {
                long transferRF = Math.min(item.getFullCapacity(stack) - item.getEnergyLevel(stack), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
                if (transferRF > 0)
                    transfer.stored -= item.addEnergy(stack, transferRF, action);
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		ISonarEnergyItem item = (ISonarEnergyItem) stack.getItem();
		if (item instanceof ISonarEnergyItem) {
            if (item.getFullCapacity(stack) > 0) {
                long transferRF = Math.min(item.getEnergyLevel(stack), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
                if (transferRF > 0)
                    transfer.stored -= item.removeEnergy(stack, transferRF, action);
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, ItemStack stack) {
		ISonarEnergyItem item = (ISonarEnergyItem) stack.getItem();
		if (item == null) {
			return;
		}
		if (item instanceof ISonarEnergyItem) {
            if (item.getFullCapacity(stack) > 0) {
                energyStack.setStorageValues(item.getEnergyLevel(stack), item.getFullCapacity(stack));
                energyStack.setMaxInput(item.addEnergy(stack, Integer.MAX_VALUE, ActionType.SIMULATE));
                energyStack.setMaxOutput(item.removeEnergy(stack, Integer.MAX_VALUE, ActionType.SIMULATE));
			}
		}
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}
}
