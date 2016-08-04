package sonar.core.handlers.container;

import net.minecraft.item.ItemStack;
import sonar.core.api.energy.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyItem;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class SonarItemHandler extends EnergyContainerHandler {

	@Override
	public String getName() {
		return "Sonar Item Handler";
	}

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return stack.getItem() != null && stack.getItem() instanceof ISonarEnergyItem;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		ISonarEnergyItem item = (ISonarEnergyItem) stack.getItem();
		if (item instanceof ISonarEnergyItem) {
			ISonarEnergyItem receiver = (ISonarEnergyItem) item;
			if (receiver.getFullCapacity(stack) > 0) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= receiver.addEnergy(stack, transferRF, action);
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
			ISonarEnergyItem receiver = (ISonarEnergyItem) item;
			if (receiver.getFullCapacity(stack) > 0) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= receiver.removeEnergy(stack, transferRF, action);
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
			ISonarEnergyItem receiver = (ISonarEnergyItem) item;
			if (receiver.getFullCapacity(stack) > 0) {
				energyStack.setStorageValues(receiver.getEnergyLevel(stack), receiver.getFullCapacity(stack));
				energyStack.setMaxInput(receiver.addEnergy(stack, Integer.MAX_VALUE, ActionType.SIMULATE));
				energyStack.setMaxOutput(receiver.removeEnergy(stack, Integer.MAX_VALUE, ActionType.SIMULATE));
			}
		}
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}

}
