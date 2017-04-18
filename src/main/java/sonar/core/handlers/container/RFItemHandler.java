package sonar.core.handlers.container;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

@EnergyContainerHandler(modid = "sonarcore", priority = 3)
public class RFItemHandler implements ISonarEnergyContainerHandler {

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return stack.getItem() != null && stack.getItem() instanceof IEnergyContainerItem;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
		if (item instanceof IEnergyContainerItem) {
			IEnergyContainerItem receiver = (IEnergyContainerItem) item;
			if (receiver.getMaxEnergyStored(stack) > 0) {
				int transferRF = Math.min(receiver.getMaxEnergyStored(stack) - receiver.getEnergyStored(stack), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
				if (transferRF > 0)
					transfer.stored -= receiver.receiveEnergy(stack, transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
		if (item instanceof IEnergyContainerItem) {
			IEnergyContainerItem receiver = (IEnergyContainerItem) item;
			if (receiver.getMaxEnergyStored(stack) > 0) {
				int transferRF = Math.min(receiver.getEnergyStored(stack), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
				if (transferRF > 0)
					transfer.stored -= receiver.extractEnergy(stack, transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, ItemStack stack) {
		IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
		if (item == null) {
			return;
		}
		if (item instanceof IEnergyContainerItem) {
			if (item.getMaxEnergyStored(stack) > 0) {
				IEnergyContainerItem receiver = (IEnergyContainerItem) item;
				energyStack.setStorageValues(receiver.getEnergyStored(stack), receiver.getMaxEnergyStored(stack));
				energyStack.setMaxInput(receiver.receiveEnergy(stack, Integer.MAX_VALUE, true));
				energyStack.setMaxOutput(receiver.extractEnergy(stack, Integer.MAX_VALUE, true));
			}
		}
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}

}
