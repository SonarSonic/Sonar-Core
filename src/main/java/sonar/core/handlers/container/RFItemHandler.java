package sonar.core.handlers.container;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import sonar.core.api.asm.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

@EnergyContainerHandler(modid = "redstoneflux", priority = 3)
public class RFItemHandler implements ISonarEnergyContainerHandler {

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IEnergyContainerItem;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
		if (item instanceof IEnergyContainerItem) {
            if (item.getMaxEnergyStored(stack) > 0) {
                int transferRF = Math.min(item.getMaxEnergyStored(stack) - item.getEnergyStored(stack), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
                if (transferRF > 0)
                    transfer.stored -= item.receiveEnergy(stack, transferRF, action.shouldSimulate());
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
            if (item.getMaxEnergyStored(stack) > 0) {
                int transferRF = Math.min(item.getEnergyStored(stack), transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE);
                if (transferRF > 0)
                    transfer.stored -= item.extractEnergy(stack, transferRF, action.shouldSimulate());
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
                energyStack.setStorageValues(item.getEnergyStored(stack), item.getMaxEnergyStored(stack));
                energyStack.setMaxInput(item.receiveEnergy(stack, Integer.MAX_VALUE, true));
                energyStack.setMaxOutput(item.extractEnergy(stack, Integer.MAX_VALUE, true));
			}
		}
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}
}
